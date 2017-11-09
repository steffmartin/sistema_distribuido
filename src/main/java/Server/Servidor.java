/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Grafo.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 *
 * @author heitor, marcelo, rhaniel, steffan
 */
public class Servidor implements Handler.Iface {

    // Variáveis Comuns
    private final Grafo g = new Grafo(new ConcurrentHashMap<>(), new ConcurrentHashMap<>());

    // Variáveis para funcionamento P2P
    private int m;                                  // A quantidade máxima de nós é de 2^m e os IDs vão de 0 a 2^m -1
    private int id, predecessor, sucessor;          // O ID deste servidor e do servidor anterior a ele e posterior a ele
    private String[] servers;                       // Será a lista com todos servidores (IPs e Portas) passadas no parâmetro, temporário até montar a FT
    private Object[][] ft;                          // Será a Finger Table, terá M nós indexados

    // Construtor e métodos auto-executados
    public Servidor(String[] args) throws ArrayIndexOutOfBoundsException, NumberFormatException, TException {
        super();

        // Salvando M e validando o tamanho do args (minimo 2, maximo 2^(m+1))
        m = Integer.parseInt(args[1]);
        if (args.length > Math.pow(2, m + 1) || args.length < 2) {
            throw new ArrayIndexOutOfBoundsException();
        }

        // Deixando uma lista com todos os servidores temporariamente no nó, será descartada após montar a Finger Table
        servers = new String[args.length - 2];
        System.arraycopy(args, 2, servers, 0, args.length - 2);
        boolean last = true; // Flag para que o último nó a se conectar comece a montagem da Finger Table

        // Escolhendo um ID aleatório (e verificando nos outros servidores para não repetir)
        id = (int) (Math.random() * Math.pow(2, m));
        System.out.println("Tentando usar o ID: " + id);
        for (int i = 0; i < servers.length; i += 2) {
            try {
                Handler.Client node = conectar(servers[i], servers[i + 1]);
                System.out.println("O servidor " + servers[i] + "/" + servers[i + 1] + " está usando o ID " + node.getServerId() + ".");
                if (id == node.getServerId()) {
                    id = (int) (Math.random() * Math.pow(2, m));
                    i = -2;
                    System.out.println("ID indisponível. Tentando usar novo ID: " + id);
                }
            } catch (TTransportException ex) {
                last = false;
                System.out.println("O servidor " + servers[i] + "/" + servers[i + 1] + " ainda não está online.");
            }
        }

        //O último servidor a ficar online avisa os outros para montarem a Finger Table e então monta sua própria FT
        if (last) {
            for (int i = 0; i < servers.length; i += 2) {
                conectar(servers[i], servers[i + 1]).setFt();
            }
            setFt();
        }
    }

    // Método necessário para um servidor saber o ID do outro e não repetir
    @Override
    public int getServerId() throws TException {
        return id;
    }

    // Método necessário pois a Finger Table só pode ser montada após todos ficarem online e terem seus IDs
    @Override
    public void setFt() throws TException {
        if (ft == null) {
            ft = new Object[m][2]; //M linhas e 2 colunas (ID, Endereço)

            // Obtendo IDs de todos os servidores listados no parâmetro
            TreeMap<Integer, String[]> temp = new TreeMap<>();
            for (int i = 0; i < servers.length; i += 2) {
                try {
                    Handler.Client node = conectar(servers[i], servers[i + 1]);
                    temp.put(node.getServerId(), new String[]{servers[i], servers[i + 1]});
                } catch (TTransportException ex) {
                    // Se houver algum erro de conexão e der esta exceção, o servidor com erro ficará fora da montagem da FT
                }
            }

            // Salvando o ID do servidor anterior
            if (temp.floorKey(id) != null) {
                predecessor = temp.floorKey(id);
            } else {
                predecessor = temp.lastKey();
            }

            // Montando tabela (FT)
            for (int i = 0; i < m; i++) {
                int ftpi = id + (int) Math.pow(2, i);// Não é usado 2 ^ i-1 porque i já começa em 0 aqui
                if (ftpi >= Math.pow(2, m)) {
                    ftpi -= Math.pow(2, m);
                }
                if (temp.ceilingKey(ftpi) != null) {
                    ft[i][0] = temp.ceilingKey(ftpi);
                } else {
                    ft[i][0] = temp.firstKey();
                }
                ft[i][1] = temp.get((int) ft[i][0]);
            }

            // Salvando o ID do servidor seguinte
            sucessor = (int) ft[0][0];

            // Descartar a lista com TODOS os servidores que ficou armazenada temporariamente
            servers = null;

            // Impressão para conferência
            System.out.println("Finger Table:");
            for (int i = 0; i < m; i++) {
                System.out.println("| " + (i + 1) + " | " + (int) ft[i][0] + " |");
            }
        }
    }

    // Método com a função Hash para K
    private int hash(int k) {
        return k % (int) Math.pow(2, m);
    }

    // Método para saber se o nó atual é sucessor de uma chave K
    private boolean isSucc(int k) {
        k = hash(k);
        if (predecessor < id) {
            return predecessor < k && k <= id;
        } else {
            return !(id < k && k <= predecessor);
        }
    }

    // Método para saber se o nó atual é sucessor de uma aresta (duas chaves)
    // Arestas invertidas ficarão no mesmo nó das arestas, Ex: 1,2 e 2,1 ficam no mesmo nó sempre
    private boolean isSucc(int a, int b) {
        return isSucc(a + b);
    }

    // Método para se conectar ao nó sucessor de uma chave K, usando somente a Finger Table
    private Handler.Client conectarSucc(int k) throws TTransportException {
        k = hash(k);
        String[] node = (String[]) ft[m - 1][1];
        if ((id < sucessor && sucessor >= k) || (id > sucessor && (id < k || k <= sucessor))) {
            System.out.println("ID " + id + " repassando requisição para ID " + (int) ft[0][0]);
            node = (String[]) ft[0][1];
        } else {
            int i;
            for (i = 0; i < m - 1; i++) {
                if ((int) ft[i][0] <= k && k <= (int) ft[i + 1][0]) {
                    System.out.println("ID " + id + " repassando requisição para ID " + (int) ft[i][0]);
                    node = (String[]) ft[i][1];
                    break;
                }
            }
            if (i == m - 1) {
                System.out.println("ID " + id + " repassando requisição para ID " + (int) ft[m - 1][0]);
            }
        }
        return conectar(node[0], node[1]);
    }

    private Handler.Client conectarSucc(int a, int b) throws TTransportException {
        return conectarSucc(a + b);
    }

    // Método para abrir conexão com um outro nó, recebe IP e Porta
    private Handler.Client conectar(String ip, String porta) throws TTransportException {
        TTransport transport = new TSocket(ip, Integer.parseInt(porta));
        transport.open();
        TProtocol protocol = new TBinaryProtocol(transport);
        return new Handler.Client(protocol);
    }

    @Override
    public boolean bloqueiaVertice(int nome) {
        try {
            if (isSucc(nome)) {
                System.out.println("Executando bloqueiaVertice(" + nome + ") aqui.");
                while (true) {
                    synchronized (g.vertices.get(nome)) {
                        if (!g.vertices.get(nome).isBloqueado()) {
                            g.vertices.get(nome).setBloqueado(true);
                            return true;
                        }
                    }
                    wait((150 + ((int) Math.random() * 1000)));
                }
            } else {
                return conectarSucc(nome).bloqueiaVertice(nome);
            }
        } catch (NullPointerException ex) {
            return false;
        } catch (Exception ex) {
            return bloqueiaVertice(nome);
        }
    }

    @Override
    public void desbloqueiaVertice(int nome) {
        try {
            if (isSucc(nome)) {
                System.out.println("Executando desbloqueiaVertice(" + nome + ") aqui.");
                synchronized (g.vertices.get(nome)) {
                    g.vertices.get(nome).setBloqueado(false);
                }
            } else {
                conectarSucc(nome).desbloqueiaVertice(nome);
            }
        } catch (NullPointerException ex) {
        } catch (Exception ex) {
            desbloqueiaVertice(nome);
        }
    }

    // Métodos do Grafo (etapa 1) revistos (etapa 2)
    // Criar vértice - Status revisão estapa 2: pronto e testado
    @Override
    public boolean createVertice(Vertice v) throws TException {
        if (v.getNome() < 0) {
            return false;
        }
        if (isSucc(v.getNome())) {
            System.out.println("Executando createVertice(" + v.getNome() + ") aqui.");
            return g.vertices.putIfAbsent(v.getNome(), v) == null;
        } else {
            return conectarSucc(v.getNome()).createVertice(v);
        }
    }

    // Criar aresta - Status revisão estapa 2: pronto e testado
    @Override
    public boolean createAresta(Aresta a) throws TException {
        if (a.getVertice1() == a.getVertice2()) {
            return false;
        }
        if (isSucc(a.getVertice1(), a.getVertice2())) {
            System.out.println("Executando createAresta(" + a.getVertice1() + "," + a.getVertice2() + ") aqui.");
            Id id = new Id(a.getVertice1(), a.getVertice2());
            Id id2 = new Id(a.getVertice2(), a.getVertice1());
            int menor = a.getVertice1() < a.getVertice2() ? a.getVertice1() : a.getVertice2();
            int maior = a.getVertice1() > a.getVertice2() ? a.getVertice1() : a.getVertice2();
            try {
                if (bloqueiaVertice(menor) && bloqueiaVertice(maior)) {
                    try {
                        synchronized (g.arestas.get(id2)) {
                            if (!g.arestas.get(id2).isDirec() || !a.isDirec()) {
                                return false;
                            } else {
                                throw new NullPointerException();
                            }
                        }
                    } catch (NullPointerException ey) {
                        return g.arestas.putIfAbsent(id, a) == null;
                    }
                } else {
                    return false;
                }
            } finally {
                desbloqueiaVertice(maior);
                desbloqueiaVertice(menor);
            }
        } else {
            return conectarSucc(a.getVertice1(), a.getVertice2()).createAresta(a);
        }
    }

    // Ler vértice - Status revisão estapa 2: pronto e testado
    @Override
    public Vertice readVertice(int nome) throws NullException, TException {
        if (isSucc(nome)) {
            System.out.println("Executando readVertice(" + nome + ") aqui.");
            try {
                if (bloqueiaVertice(nome)) {
                    return g.vertices.get(nome);
                } else {
                    throw new NullException("O vértice '" + nome + "' não existe");
                }
            } finally {
                desbloqueiaVertice(nome);
            }
        } else {
            return conectarSucc(nome).readVertice(nome);
        }
    }

    // Ler aresta - Status revisão estapa 2: pronto e testado
    @Override
    public Aresta readAresta(int nome1, int nome2) throws NullException, TException {
        if (isSucc(nome1, nome2)) {
            System.out.println("Executando readAresta(" + nome1 + "," + nome2 + ") aqui.");
            Id id = new Id(nome1, nome2);
            Id id2 = new Id(nome2, nome1);
            try {
                synchronized (g.arestas.get(id)) {
                    return g.arestas.get(id);
                }
            } catch (NullPointerException ex) {
                try {
                    synchronized (g.arestas.get(id2)) {
                        if (!g.arestas.get(id2).isDirec()) {
                            return g.arestas.get(id2);
                        } else {
                            throw new NullPointerException();
                        }
                    }
                } catch (NullPointerException ey) {
                    throw new NullException("A aresta '" + nome1 + "," + nome2 + "' não existe.");
                }
            }
        } else {
            return conectarSucc(nome1, nome2).readAresta(nome1, nome2);
        }
    }

    // Atualizar vértice - Status revisão estapa 2: pronto e testado
    @Override
    public boolean updateVertice(Vertice v) throws TException {
        if (isSucc(v.getNome())) {
            System.out.println("Executando updateVertice(" + v.getNome() + ") aqui.");
            try {
                if (bloqueiaVertice(v.getNome())) {
                    v.setBloqueado(true);
                    return g.vertices.replace(v.getNome(), g.vertices.get(v.getNome()), v);
                } else {
                    return false;
                }
            } finally {
                desbloqueiaVertice(v.getNome());
            }
        } else {
            return conectarSucc(v.getNome()).updateVertice(v);
        }
    }

    // Atualizar aresta - Status revisão estapa 2: pronto e testado
    @Override
    public boolean updateAresta(Aresta a) throws TException {
        if (isSucc(a.getVertice1(), a.getVertice2())) {
            System.out.println("Executando updateAresta(" + a.getVertice1() + "," + a.getVertice2() + ") aqui.");
            Id id = new Id(a.getVertice1(), a.getVertice2());
            Id id2 = new Id(a.getVertice2(), a.getVertice1());
            try {
                synchronized (g.arestas.get(id)) {
                    if (a.isDirec() == g.arestas.get(id).isDirec()) {
                        return g.arestas.replace(id, g.arestas.get(id), a);
                    } else {
                        return false;
                    }
                }
            } catch (NullPointerException ex) {
                try {
                    synchronized (g.arestas.get(id2)) {
                        if (a.isDirec() == g.arestas.get(id2).isDirec()) {
                            return g.arestas.replace(id2, g.arestas.get(id2), a);
                        } else {
                            return false;
                        }
                    }
                } catch (NullPointerException ey) {
                    return false;
                }
            }
        } else {
            return conectarSucc(a.getVertice1(), a.getVertice2()).updateAresta(a);
        }
    }

    // Excluir vértice - Status revisão estapa 2: pronto e testado
    @Override
    public boolean deleteVertice(int nome) throws TException {
        if (isSucc(nome)) {
            System.out.println("Executando deleteVertice(" + nome + ") aqui.");
            try {
                if (bloqueiaVertice(nome)) {
                    deleteArestasDoVertice(nome, predecessor);
                    return g.vertices.remove(nome) != null;
                } else {
                    return false;
                }
            } finally {
                desbloqueiaVertice(nome);
            }
        } else {
            return conectarSucc(nome).deleteVertice(nome);
        }
    }

    // Excluir aresta - Status revisão estapa 2: pronto e testado
    @Override
    public boolean deleteAresta(int nome1, int nome2) throws TException {
        if (isSucc(nome1, nome2)) {
            System.out.println("Executando deleteAresta(" + nome1 + "," + nome2 + ") aqui.");
            Id id = new Id(nome1, nome2);
            Id id2 = new Id(nome2, nome1);
            try {
                synchronized (g.arestas.get(id)) {
                    return g.arestas.remove(id) != null;
                }
            } catch (NullPointerException ex) {
                try {
                    synchronized (g.arestas.get(id2)) {
                        if (!g.arestas.get(id2).isDirec()) {
                            return g.arestas.remove(id2) != null;
                        } else {
                            throw new NullPointerException();
                        }
                    }
                } catch (NullPointerException ey) {
                    return false;
                }
            }
        } else {
            return conectarSucc(nome1, nome2).deleteAresta(nome1, nome2);
        }
    }

    //Excluir aresta do vértice de forma distribuída
    @Override
    public void deleteArestasDoVertice(int nome, int endId) throws TException {
        if (endId != id) {
            conectarSucc(sucessor).deleteArestasDoVertice(nome, endId);
        }
        List<Aresta> list;
        synchronized (g.arestas) {
            list = new ArrayList<>(g.arestas.values());
        }
        Iterator<Aresta> it = list.iterator();
        Aresta a;
        while (it.hasNext()) {
            a = it.next();
            if (a.getVertice1() == nome || a.getVertice2() == nome) {
                deleteAresta(a.getVertice1(), a.getVertice2());
            }
        }
    }

    // Listar todos vértices - Status revisão estapa 2: pronto e testado, (fazer uma ordenação .sort ?)
    @Override
    public List<Vertice> listVerticesDoGrafo() throws TException {
        return listVerticesDoGrafoNoAnel(predecessor);
    }

    // Listar os vértices de todos os nós do anel, parando ao dar a volta e chegar no nó que solicitou a lista - Status: pronto e testado
    @Override
    public List<Vertice> listVerticesDoGrafoNoAnel(int endId) throws TException {
        System.out.println("Listando uma parte de todos os vértices aqui.");
        List<Vertice> lista;
        synchronized (g.vertices) {
            lista = new ArrayList<>(g.vertices.values());
        }
        if (endId != id) {
            lista.addAll(conectarSucc(sucessor).listVerticesDoGrafoNoAnel(endId));
        }
        return lista;
    }

    // Listar todas arestas - Status revisão estapa 2: pronto e testado (vamos ordenar com .sort?)
    @Override
    public List<Aresta> listArestasDoGrafo() throws TException {
        return listArestasDoGrafoNoAnel(predecessor);
    }

    // Listar as arestas de todos os nós do anel, parando ao dar a volta e chegar no nó que solicitou a lista - Status: pronto e testado
    @Override
    public List<Aresta> listArestasDoGrafoNoAnel(int endId) throws TException {
        System.out.println("Listando uma parte de todas as arestas aqui.");
        List<Aresta> lista;
        synchronized (g.arestas) {
            lista = new ArrayList<>(g.arestas.values());
        }
        if (endId != id) {
            lista.addAll(conectarSucc(sucessor).listArestasDoGrafoNoAnel(endId));
        }
        return lista;

    }

    @Override
    public List<Aresta> listArestasDoVertice(int nome) throws NullException, TException {
        readVertice(nome);
        return listArestasDoVerticeNoAnel(nome, predecessor);
    }

    @Override
    public List<Aresta> listArestasDoVerticeNoAnel(int nome, int endId) throws NullException, TException {
        System.out.println("Listando uma parte das arestas do vértice " + nome + " aqui.");
        List<Aresta> lista;
        synchronized (g.arestas) {
            lista = new ArrayList<>(g.arestas.values());
        }
        Iterator<Aresta> it = lista.iterator();
        Aresta a;
        while (it.hasNext()) {
            a = it.next();
            if (a.getVertice1() != nome && a.getVertice2() != nome) {
                it.remove();
            }
        }
        if (endId != id) {
            lista.addAll(conectarSucc(sucessor).listArestasDoVerticeNoAnel(nome, endId));
        }
        return lista;
    }

    // Listar vizinhos do vértice - Status revisão estapa 2: pronto e testado
    @Override
    public List<Vertice> listVizinhosDoVertice(int nome) throws NullException, TException {
        if (isSucc(nome)) {
            System.out.println("Listando os vizinhos do vértice" + nome + " aqui.");
            List<Aresta> list = listArestasDoVertice(nome);
            List<Vertice> result = new ArrayList<>();
            for (Aresta a : list) {
                if (a.isDirec() && a.getVertice2() == nome) //significa que o vértice 1 da aresta não é vizinho do vértice 'nome'
                {
                    continue;
                }
                Vertice vt1 = readVertice(a.getVertice1());
                Vertice vt2 = readVertice(a.getVertice2());
                if (a.getVertice1() == nome && !result.contains(vt1)) {
                    result.add(vt2);
                } else if (a.getVertice2() == nome && !result.contains(vt2)) {
                    result.add(vt1);
                }
            }
            return result;
        } else {
            return conectarSucc(nome).listVizinhosDoVertice(nome);
        }
    }

    // Métodos do Grafo (etapa 2)
    // Listar menor caminho - Status revisão estapa 2: não iniciado
    @Override
    public List<Vertice> listMenorCaminho(int nome1, int nome2) throws NullException, TException {
        throw new NullException("Ainda não suportado.");
    }
    
    private int procuraMenorDistancia(Map<Integer, Double> dist, Map<Integer, Integer> visitado, List<Vertice> vertices){
        int i, menor = -1;
        boolean primeiro = true;
        
        for(Vertice v: vertices){
            if(dist.get(v.getNome()) >= 0 && visitado.get(v.getNome()) == 0){
                if(primeiro){
                    menor = v.getNome();
                    primeiro = false;
                }
                else{
                    if(dist.get(menor) > dist.get(v.getNome()))
                        menor = v.getNome();
                }
            }
        }
        return menor;
    }
    
    public List<Vertice> menorCaminho(int ini, int fim, Map<Integer, Integer> ant, Map<Integer, Double> dist)  throws NullException, TException {
        int i, cont, NV, ind, u;
                
        List<Vertice> vertices = listVerticesDoGrafo();
        HashMap<Integer, Vertice> verticesG = new HashMap<>();
        HashMap<Integer, Integer> visitado = new HashMap<>();

        cont = NV = vertices.size();

        for(Vertice v:vertices){
            verticesG.put(v.getNome(), v);
            visitado.put(v.getNome(), 0);
            ant.put(v.getNome(), -1);
            dist.put(v.getNome(), -1.0);
        }

        dist.replace(ini, 0.0);

        while(cont > 0){
            u = procuraMenorDistancia(dist, visitado, vertices);
            if(u == -1)
                break;

            visitado.replace(u, 1);
            cont--;

            List<Vertice> list = listVizinhosDoVertice(u);

            for(i = 0; i < list.size(); i++){
                ind = list.get(i).getNome();

                Aresta ar = readAresta(u, ind);
                if(dist.get(ind) < 0){                        
                    dist.replace(ind, dist.get(u) + ar.getPeso());
                    ant.replace(ind, u);
                }
                else{
                    if(dist.get(ind) > dist.get(u) + ar.getPeso()){
                        dist.replace(ind, dist.get(u) + ar.getPeso());
                        ant.replace(ind, u);
                    }
                }
            }
        }

        List<Vertice> resp = new ArrayList<>();
        int v = fim;
        while(v != ini){
            resp.add(verticesG.get(v));
            v = ant.get(v);
            if(v == ini)
                resp.add(verticesG.get(v));
        }

        return resp;       
    }
    
}
