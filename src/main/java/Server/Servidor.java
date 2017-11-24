/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Grafo.*;
import Comandos.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.client.CopycatClient;
import io.atomix.copycat.client.RecoveryStrategies;
import io.atomix.copycat.server.Commit;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.server.StateMachine;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author heitor, marcelo, rhaniel, steffan
 */
public class Servidor extends StateMachine implements Handler.Iface {

    // Variáveis Comuns
    private final Grafo g = new Grafo(new ConcurrentHashMap<>(), new ConcurrentHashMap<>());

    // Variáveis para funcionamento P2P
    private int m;                                  // A quantidade máxima de nós é de 2^m e os IDs vão de 0 a 2^m -1
    private int id, predecessor, sucessor;          // O ID deste servidor e do servidor anterior a ele e posterior a ele
    private String[] servers;                       // Será a lista com todos servidores (IPs e Portas) passadas no parâmetro, temporário até montar a FT
    private Object[][] ft;                          // Será a Finger Table, terá M nós indexados

    // Variáveis para funcionamento da replicação
    List<Address> members = new LinkedList<>();     // Os endereços dos membros do cluster ao qual este nó pertence
    CopycatClient cluster;                          // Uma conexão com o cluster

// Construtor e métodos auto-executados
    public Servidor(String[] args) throws ArrayIndexOutOfBoundsException, NumberFormatException, TException {
        super();

        // Salvando M e validando o tamanho do args (minimo 11, maximo 2^(m+1) + 11)
        m = Integer.parseInt(args[10]);
        if (args.length < 11 || args.length > (Math.pow(2, m) - 1) * 6 + 11) {
            throw new ArrayIndexOutOfBoundsException();
        }

        // Salvando o cluster
        members.add(new Address(args[0], Integer.parseInt(args[2])));
        members.add(new Address(args[4], Integer.parseInt(args[6])));
        members.add(new Address(args[7], Integer.parseInt(args[9])));
        CopycatClient.Builder builder = CopycatClient.builder()
                .withTransport(NettyTransport.builder()
                        .withThreads(4)
                        .build());
        cluster = builder.build();

        // Deixando uma lista com todos os servidores temporariamente no nó, será descartada após montar a Finger Table
        servers = new String[args.length - 11];
        System.arraycopy(args, 11, servers, 0, args.length - 11);
        boolean last = true; // Flag para que o último nó a se conectar comece a montagem da Finger Table

        // Escolhendo um ID aleatório (e verificando nos outros servidores para não repetir)
        if (Boolean.parseBoolean(args[3])) {
            id = (int) (Math.random() * Math.pow(2, m));

            System.out.println("Tentando usar o ID: " + id);
            for (int i = 0; i < servers.length; i += 6) {
                try {
                    Handler.Client node = conectar(new String[]{servers[i], servers[i + 1], servers[i + 2], servers[i + 3], servers[i + 4], servers[i + 5]});
                    System.out.println("O servidor " + servers[i] + ":" + servers[i + 1] + "/" + servers[i + 3] + "/" + servers[i + 5] + " está usando o ID " + node.getServerId() + ".");
                    if (id == node.getServerId()) {
                        id = (int) (Math.random() * Math.pow(2, m));
                        i = -2;
                        System.out.println("ID indisponível. Tentando usar novo ID: " + id);
                    }
                } catch (TTransportException ex) {
                    System.out.println("O servidor " + servers[i] + ":" + servers[i + 1] + "/" + servers[i + 3] + "/" + servers[i + 5] + " ainda não está online.");
                    last = false;
                }
            }

            //O último servidor a ficar online avisa ao primeiro para montar a sua FT
            if (last) {
                conectar(servers).setFt();
            }
        } else {
            id = conectar(new String[]{args[4], args[5], args[7], args[8]}).getServerId();
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
            if (cluster.state() != CopycatClient.State.CONNECTED) {
                cluster.connect(members).join();
            }
            cluster.submit(new setFtCommand()).join();
            conectarSucc(sucessor).setFt();
        }
    }

    public void setFt(Commit<setFtCommand> commit) throws TException {
        try {
            id = getServerId(); //Salvando o ID novamente para ficar no LOG do cluster
            ft = new Object[m][2]; //M linhas e 2 colunas (ID, Endereço)

            // Obtendo IDs de todos os servidores listados no parâmetro
            TreeMap<Integer, String[]> temp = new TreeMap<>();
            for (int i = 0; i < servers.length; i += 6) {
                try {
                    temp.put(conectar(servers[i], servers[i + 1]).getServerId(), new String[]{servers[i], servers[i + 1], servers[i + 2], servers[i + 3], servers[i + 4], servers[i + 5]});
                } catch (TTransportException ex) {
                    // Se houver algum erro de conexão e der esta exceção, o servidor com erro ficará fora da montagem da FT
                }
            }

            // Descartar a lista com TODOS os servidores que ficou armazenada temporariamente
            servers = null;

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

            // Impressão para conferência
            System.out.println("Finger Table:");
            for (int i = 0; i < m; i++) {
                System.out.println("| " + (i + 1) + " | " + (int) ft[i][0] + " |");
            }
            System.out.println("\nLOG DE OPERAÇÕES DO SERVIDOR " + id + ":\n");
        } finally {
            commit.close();
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
    private boolean isSucc(int a, int b) {
        return isSucc(a + b); // Arestas invertidas ficarão no mesmo nó das arestas, Ex: 1,2 e 2,1 ficam no mesmo nó sempre

    }

    // Método para abrir conexão com um outro nó, recebe IP e Porta
    private Handler.Client conectar(String ip, String porta) throws TTransportException {
        TTransport transport = new TSocket(ip, Integer.parseInt(porta));
        transport.open();
        TProtocol protocol = new TBinaryProtocol(transport);
        return new Handler.Client(protocol);
    }

    // Método que tentará se conectar a qualquer nó que responda de uma lista
    private Handler.Client conectar(String[] servers) throws TTransportException {
        for (int i = 0; i < servers.length; i += 2) {
            try {
                return conectar(servers[i], servers[i + 1]);
            } catch (TTransportException ex) {
            }
        }
        throw new TTransportException();
    }

    // Método para se conectar ao nó sucessor de uma aresta, usando somente a Finger Table
    private Handler.Client conectarSucc(int a, int b) throws TTransportException {
        return conectarSucc(a + b);
    }

    // Método para se conectar ao nó sucessor de uma chave K, usando somente a Finger Table
    private Handler.Client conectarSucc(int k) throws TTransportException {
        k = hash(k);
        String[] node = (String[]) ft[m - 1][1]; // Por garantia já escolhe o último índice da FT. Não havendo nenhuma substituição, se conectará ao último nó da FT
        if ((id < sucessor && sucessor >= k) || (id > sucessor && (id < k || k <= sucessor))) {
            System.out.println(LocalDateTime.now().toLocalTime().toString() + " ID " + id + " repassando requisição para ID " + (int) ft[0][0]);
            node = (String[]) ft[0][1]; // Troca para o primeiro nó da FT se atender à condição do IF
        } else {
            int i;
            for (i = 0; i < m - 1; i++) {
                //repassa para o nó i se id(i) <=k && k <= id(i+1) ou, se caso id(i+1) < id(i), o que significa que deu a volta no anel, então repassa se id(i) <= k && k <= id(i+1) + 2^m
                if (((int) ft[i][0] <= k && k <= (int) ft[i + 1][0]) || (((int) ft[i + 1][0] < (int) ft[i][0]) && (int) ft[i][0] <= k && k <= ((int) Math.pow(2, m) + (int) ft[i + 1][0]))) {
                    System.out.println(LocalDateTime.now().toLocalTime().toString() + " ID " + id + " repassando requisição para ID " + (int) ft[i][0]);
                    node = (String[]) ft[i][1]; // Troca para o índice i caso em algum momento atenda à condição do algoritimo de repasse da FT: succ <= k <= succ+1
                    break;
                }
            }
            if (i == m - 1) {
                System.out.println(LocalDateTime.now().toLocalTime().toString() + " ID " + id + " repassando requisição para ID " + (int) ft[m - 1][0]);
            }
        }
        return conectar(node); // Se conecta ao nó correto, ou qualquer cópia deste nó que esteja online
    }

    // Método para bloquear vértice independente do servidor que ele esteja, substitui o Syncronized. True quando bloquear, False se não existir tal vértice
    @Override
    public boolean bloqueiaVertice(int nome) throws TException {
        try {
            if (isSucc(nome)) {
                System.out.println(LocalDateTime.now().toLocalTime().toString() + " Executando bloqueiaVertice(" + nome + ")");
                while (true) {
                    synchronized (g.vertices.get(nome)) {
                        if (!g.vertices.get(nome).isBloqueado()) {
                            g.vertices.get(nome).setBloqueado(true);
                            return true;
                        }
                    }
                }
            } else {
                return conectarSucc(nome).bloqueiaVertice(nome);
            }
        } catch (NullPointerException ex) {
            return false;
        }
    }

    // Método para desbloquear um vértice que foi bloqueado em qualquer servidor
    @Override
    public void desbloqueiaVertice(int nome) throws TException {
        try {
            if (isSucc(nome)) {
                System.out.println(LocalDateTime.now().toLocalTime().toString() + " Executando desbloqueiaVertice(" + nome + ")");
                synchronized (g.vertices.get(nome)) {
                    g.vertices.get(nome).setBloqueado(false);
                }
            } else {
                conectarSucc(nome).desbloqueiaVertice(nome);
            }
        } catch (NullPointerException ex) {
        }
    }

    // Métodos do Grafo
    // Criar vértice
    @Override
    public boolean createVertice(Vertice v) throws TException {
        if (v.getNome() < 0) {
            return false;
        }
        if (isSucc(v.getNome())) {
            System.out.println(LocalDateTime.now().toLocalTime().toString() + " Executando createVertice(" + v.getNome() + ")");
            return g.vertices.putIfAbsent(v.getNome(), v) == null;
        } else {
            return conectarSucc(v.getNome()).createVertice(v);
        }
    }

    // Criar aresta
    @Override
    public boolean createAresta(Aresta a) throws TException {
        if (a.getVertice1() == a.getVertice2()) {
            return false;
        }
        if (isSucc(a.getVertice1(), a.getVertice2())) {
            System.out.println(LocalDateTime.now().toLocalTime().toString() + " Executando createAresta(" + a.getVertice1() + "," + a.getVertice2() + ")");
            Id id1 = new Id(a.getVertice1(), a.getVertice2());
            Id id2 = new Id(a.getVertice2(), a.getVertice1());
            int menor = a.getVertice1() < a.getVertice2() ? a.getVertice1() : a.getVertice2();
            int maior = a.getVertice1() > a.getVertice2() ? a.getVertice1() : a.getVertice2();
            try {
                if (bloqueiaVertice(menor) & bloqueiaVertice(maior)) { // Somente um '&' para obrigar que os dois testes sejam feitos. Com '&&' ele não testa o segundo se o primeiro for FALSE
                    try {
                        synchronized (g.arestas.get(id2)) {
                            if (!g.arestas.get(id2).isDirec() || !a.isDirec()) {
                                return false;
                            } else {
                                throw new NullPointerException();
                            }
                        }
                    } catch (NullPointerException ey) {
                        return g.arestas.putIfAbsent(id1, a) == null;
                    }
                } else {
                    return false;
                }
            } finally {
                desbloqueiaVertice(maior); // Se os dois testes não forem feitos acima, há risco de desbloquear indevidamente o vértice maior se ele não foi testado e bloqueado acima
                desbloqueiaVertice(menor);
            }
        } else {
            return conectarSucc(a.getVertice1(), a.getVertice2()).createAresta(a);
        }
    }

    // Ler vértice
    @Override
    public Vertice readVertice(int nome) throws NullException, TException {
        if (isSucc(nome)) {
            System.out.println(LocalDateTime.now().toLocalTime().toString() + " Executando readVertice(" + nome + ")");
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

    // Ler aresta
    @Override
    public Aresta readAresta(int nome1, int nome2) throws NullException, TException {
        if (isSucc(nome1, nome2)) {
            System.out.println(LocalDateTime.now().toLocalTime().toString() + " Executando readAresta(" + nome1 + "," + nome2 + ")");
            Id id1 = new Id(nome1, nome2);
            Id id2 = new Id(nome2, nome1);
            try {
                synchronized (g.arestas.get(id1)) {
                    return g.arestas.get(id1);
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

    // Atualizar vértice
    @Override
    public boolean updateVertice(Vertice v) throws TException {
        if (isSucc(v.getNome())) {
            System.out.println(LocalDateTime.now().toLocalTime().toString() + " Executando updateVertice(" + v.getNome() + ")");
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

    // Atualizar aresta
    @Override
    public boolean updateAresta(Aresta a) throws TException {
        if (isSucc(a.getVertice1(), a.getVertice2())) {
            System.out.println(LocalDateTime.now().toLocalTime().toString() + " Executando updateAresta(" + a.getVertice1() + "," + a.getVertice2() + ")");
            Id id1 = new Id(a.getVertice1(), a.getVertice2());
            Id id2 = new Id(a.getVertice2(), a.getVertice1());
            try {
                synchronized (g.arestas.get(id1)) {
                    if (a.isDirec() == g.arestas.get(id1).isDirec()) { // Para consistência não permitimos alterar o direcionamento da aresta.
                        return g.arestas.replace(id1, g.arestas.get(id1), a);
                    } else {
                        return false;
                    }
                }
            } catch (NullPointerException ex) {
                try {
                    synchronized (g.arestas.get(id2)) {
                        if (a.isDirec() == g.arestas.get(id2).isDirec()) { // Para consistência não permitimos alterar o direcionamento da aresta.
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

    // Excluir vértice
    @Override
    public boolean deleteVertice(int nome) throws TException {
        if (isSucc(nome)) {
            System.out.println(LocalDateTime.now().toLocalTime().toString() + " Executando deleteVertice(" + nome + ")");
            try {
                if (bloqueiaVertice(nome)) {
                    deleteArestasDoVertice(nome, predecessor); // Não aguardo o resultado, a operação VAI ser executada
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

    // Excluir aresta
    @Override
    public boolean deleteAresta(int nome1, int nome2) throws TException {
        if (isSucc(nome1, nome2)) {
            System.out.println(LocalDateTime.now().toLocalTime().toString() + " Executando deleteAresta(" + nome1 + "," + nome2 + ")");
            Id id1 = new Id(nome1, nome2);
            Id id2 = new Id(nome2, nome1);
            try {
                synchronized (g.arestas.get(id1)) {
                    return g.arestas.remove(id1) != null;
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

    // Excluir aresta do vértice de forma distribuída (usado pelo método deleteVertice)
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

    // Listar todos vértices 
    @Override
    public List<Vertice> listVerticesDoGrafo() throws TException {
        List<Vertice> lista = listVerticesDoGrafoNoAnel(predecessor);
        lista.sort(new Comparator<Vertice>() { // Ordenação apenas
            @Override
            public int compare(final Vertice t, Vertice t1) {
                return t.getNome() - t1.getNome();
            }
        });
        return lista;
    }

    // Listar os vértices de todos os nós do anel, parando ao dar a volta e chegar no nó que solicitou a lista
    @Override
    public List<Vertice> listVerticesDoGrafoNoAnel(int endId) throws TException {
        System.out.println(LocalDateTime.now().toLocalTime().toString() + " Executando listVerticesDoGrafo()");
        List<Vertice> lista;
        synchronized (g.vertices) {
            lista = new ArrayList<>(g.vertices.values());
        }
        if (endId != id) {
            lista.addAll(conectarSucc(sucessor).listVerticesDoGrafoNoAnel(endId));
        }
        return lista;
    }

    // Listar todas arestas
    @Override
    public List<Aresta> listArestasDoGrafo() throws TException {
        List<Aresta> lista = listArestasDoGrafoNoAnel(predecessor);
        lista.sort(new Comparator<Aresta>() { // Ordenação apenas
            @Override
            public int compare(Aresta t, Aresta t1) {
                if (t.getVertice1() - t1.getVertice1() != 0) {
                    return t.getVertice1() - t1.getVertice1();
                } else {
                    return t.getVertice2() - t1.getVertice2();
                }
            }
        });
        return lista;
    }

    // Listar as arestas de todos os nós do anel, parando ao dar a volta e chegar no nó que solicitou a lista
    @Override
    public List<Aresta> listArestasDoGrafoNoAnel(int endId) throws TException {
        System.out.println(LocalDateTime.now().toLocalTime().toString() + " Executando listArestasDoGrafo()");
        List<Aresta> lista;
        synchronized (g.arestas) {
            lista = new ArrayList<>(g.arestas.values());
        }
        if (endId != id) {
            lista.addAll(conectarSucc(sucessor).listArestasDoGrafoNoAnel(endId));
        }
        return lista;

    }

    // Listar as arestas de um determinado vértice
    @Override
    public List<Aresta> listArestasDoVertice(int nome) throws NullException, TException {
        readVertice(nome);
        List<Aresta> lista = listArestasDoVerticeNoAnel(nome, predecessor);
        lista.sort(new Comparator<Aresta>() { // Ordenação apenas
            @Override
            public int compare(Aresta t, Aresta t1) {
                if (t.getVertice1() - t1.getVertice1() != 0) {
                    return t.getVertice1() - t1.getVertice1();
                } else {
                    return t.getVertice2() - t1.getVertice2();
                }
            }
        });
        return lista;
    }

    // Listar as arestas de um determinado vértice, procurando-as em todos os servidores
    @Override
    public List<Aresta> listArestasDoVerticeNoAnel(int nome, int endId) throws NullException, TException {
        System.out.println(LocalDateTime.now().toLocalTime().toString() + " Executando listArestasDoVertice(" + nome + ")");
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

    // Listar vizinhos do vértice
    @Override
    public List<Vertice> listVizinhosDoVertice(int nome) throws NullException, TException {
        if (isSucc(nome)) {
            System.out.println(LocalDateTime.now().toLocalTime().toString() + " Executando listVizinhosDoVertice(" + nome + ")");
            List<Vertice> result = new ArrayList<>();
            for (Aresta a : listArestasDoVertice(nome)) {
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

    // Listar menor caminho de A até B
    @Override
    public List<Vertice> listMenorCaminho(int origem, int destino) throws NullException, TException {
        if (readVertice(origem).getNome() == readVertice(destino).getNome()) {
            List<Vertice> lista = new ArrayList<>();
            lista.add(readVertice(origem));
            return lista;
        } else {
            return menorCaminhoDistribuido(origem, destino, new ArrayList<>());
        }
    }

    // Listar menor caminho de A até B, busca por profundidade
    @Override
    public List<Vertice> menorCaminhoDistribuido(int origem, int destino, List<Vertice> visitados) throws NullException, TException {
        if (isSucc(origem)) {
            System.out.println(LocalDateTime.now().toLocalTime().toString() + " Executando listMenorCaminho(" + origem + "," + destino + ")");
            List<Vertice> menorCaminho = new ArrayList<>();
            List<Vertice> caminhoAtual = new ArrayList<>();
            caminhoAtual.addAll(visitados);
            caminhoAtual.add(readVertice(origem));
            for (Vertice v : listVizinhosDoVertice(origem)) {
                if (!visitados.contains(v)) {
                    List<Vertice> caminho;
                    if (v.getNome() == destino) {
                        caminho = caminhoAtual;
                        caminho.add(v);
                    } else {
                        caminho = menorCaminhoDistribuido(v.getNome(), destino, caminhoAtual);
                    }
                    try {
                        if (!caminho.isEmpty() && ((peso(caminho) < peso(menorCaminho)) || menorCaminho.isEmpty())) {
                            menorCaminho = caminho;
                        }
                    } catch (NullException ne) {
                        //Se der esta exceção, significa que um vértice ou aresta foi excluído enquanto o caminho era calculado. Assim o caminho todo já é inválido
                    }
                }
            }
            return menorCaminho;
        } else {
            return conectarSucc(origem).menorCaminhoDistribuido(origem, destino, visitados);
        }
    }

    // Método auxiliar para calcular o peso do caminho
    private int peso(List<Vertice> caminho) throws NullException, TException {
        int peso = 0;
        for (int i = 0; i < caminho.size() - 1; i++) {
            peso += readAresta(caminho.get(i).getNome(), caminho.get(i + 1).getNome()).getPeso();
        }
        return peso;
    }
}
