/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Grafo.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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

    //Grafo
    private final Grafo g = new Grafo(new ConcurrentHashMap<>(), new ConcurrentHashMap<>());

    //DHT
    private int m; // M será = 5 no projeto, mas este valor é passado por parâmetro
    private String[] servers; // Será a lista com todos servidores (IPs e Portas) passadas no parâmetro
    private int serverId, anteriorId; // O ID deste servidor e do servidor anterior a ele
    private Object[][] ft; // Será a Finger Table, tamanho máximo = M e terá até M nós indexados

    private TTransport transport;
    private TProtocol protocol;
    private Handler.Client node;
    private boolean last = true; // Flag para que o último nó a se conectar comece a montagem da FT

    public Servidor() {
        super();
    }

    public Servidor(String[] args) throws ArrayIndexOutOfBoundsException, NumberFormatException, TException {
        super();

        m = Integer.parseInt(args[1]);
        if (args.length > Math.pow(2, m) || args.length < 2) {
            throw new ArrayIndexOutOfBoundsException(); //Previne que o M não seja informado ou que tenha mais servidores do que 2^m -1
        }
        servers = new String[args.length - 2];
        System.arraycopy(args, 2, servers, 0, args.length - 2);

        //Escolhendo um ID não repetido
        serverId = (int) (Math.random() * Math.pow(2, m));
        System.out.println("Tentando usar o ID: " + serverId);
        for (int i = 0; i < servers.length; i += 2) {
            try {
                conectar(servers[i], servers[i + 1]);
                System.out.println("O servidor " + servers[i] + "/" + servers[i + 1] + " está usando o ID " + node.getServerId() + ".");
                if (serverId == node.getServerId()) {
                    serverId = (int) (Math.random() * Math.pow(2, m));
                    System.out.println("ID indisponível. Tentando usar novo ID: " + serverId);
                    i = -2;
                }
            } catch (TTransportException ex) {
                last = false;
                System.out.println("O servidor " + servers[i] + "/" + servers[i + 1] + " ainda não está online.");
            }
        }

        //O último servidor a ficar online avisa os outros para montarem a Finger Table
        if (last) {
            for (int i = 0; i < servers.length; i += 2) {
                try {
                    conectar(servers[i], servers[i + 1]);
                    node.setFt();
                } catch (TTransportException ex) {
                    throw new TException();
                }
            }
            setFt();
        }
    }

    private void conectar(String ip, String porta) throws TTransportException {
        if (transport != null && transport.isOpen()) {
            transport.close();
        }
        transport = new TSocket(ip, Integer.parseInt(porta));
        transport.open();
        protocol = new TBinaryProtocol(transport);
        node = new Handler.Client(protocol);
    }

    //Métodos
    @Override
    public boolean createVertice(Vertice v) throws TException {
        if (v.getNome() < 0) {
            return false;
        }
        return g.vertices.putIfAbsent(v.getNome(), v) == null;
    }

    @Override
    public boolean createAresta(Aresta a) throws TException {
        ArestaId id = new ArestaId(a.getVertice1(), a.getVertice2());
        ArestaId id2 = new ArestaId(a.getVertice2(), a.getVertice1());
        if (id.getNome1() == id.getNome2()) {
            return false;
        }
        try {
            if (id.getNome1() < id.getNome2()) {
                synchronized (g.vertices.get(id.getNome1())) {
                    synchronized (g.vertices.get(id.getNome2())) {
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
                    }
                }
            } else {
                synchronized (g.vertices.get(id.getNome2())) {
                    synchronized (g.vertices.get(id.getNome1())) {
                        try {
                            synchronized (g.arestas.get(id2)) {
                                if (!g.arestas.get(id2).isDirec() || !a.isDirec()) {
                                    return false;
                                } else {
                                    throw new NullPointerException();
                                }
                            }
                        } catch (NullPointerException ez) {
                            return g.arestas.putIfAbsent(id, a) == null;
                        }
                    }
                }
            }
        } catch (NullPointerException ex) {
            return false;
        }
    }

    @Override
    public Vertice readVertice(int nome) throws NullException, TException {
        try {
            synchronized (g.vertices.get(nome)) {
                return g.vertices.get(nome);
            }
        } catch (NullPointerException ex) {
            throw new NullException("O vértice '" + nome + "' não existe");
        }
    }

    @Override
    public Aresta readAresta(int nome1, int nome2) throws NullException, TException {
        ArestaId id = new ArestaId(nome1, nome2);
        ArestaId id2 = new ArestaId(nome2, nome1);
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
    }

    @Override
    public boolean updateVertice(Vertice v) throws TException {
        try {
            synchronized (g.vertices.get(v.getNome())) {
                return g.vertices.replace(v.getNome(), g.vertices.get(v.getNome()), v);
            }
        } catch (NullPointerException ex) {
            return false;
        }

    }

    @Override
    public boolean updateAresta(Aresta a) throws TException {
        ArestaId id = new ArestaId(a.getVertice1(), a.getVertice2());
        ArestaId id2 = new ArestaId(a.getVertice2(), a.getVertice1());
        try {
            synchronized (g.arestas.get(id)) {
                if (!g.arestas.containsKey(id2) || a.isDirec()) {
                    if (id.getNome1() < id.getNome2()) {
                        synchronized (g.vertices.get(id.getNome1())) {
                            synchronized (g.vertices.get(id.getNome2())) {
                                return g.arestas.replace(id, g.arestas.get(id), a);
                            }
                        }
                    } else {
                        synchronized (g.vertices.get(id.getNome2())) {
                            synchronized (g.vertices.get(id.getNome1())) {
                                return g.arestas.replace(id, g.arestas.get(id), a);
                            }
                        }
                    }
                } else {
                    return false;
                }
            }
        } catch (NullPointerException ex) {
            try {
                synchronized (g.arestas.get(id2)) {
                    if (!g.arestas.get(id2).isDirec() && !a.isDirec()) {
                        if (id2.getNome1() < id2.getNome2()) {
                            synchronized (g.vertices.get(id2.getNome1())) {
                                synchronized (g.vertices.get(id2.getNome2())) {
                                    return g.arestas.replace(id2, g.arestas.get(id2), a);
                                }
                            }
                        } else {
                            synchronized (g.vertices.get(id2.getNome2())) {
                                synchronized (g.vertices.get(id2.getNome1())) {
                                    return g.arestas.replace(id2, g.arestas.get(id2), a);
                                }
                            }
                        }
                    } else {
                        throw new NullPointerException();
                    }
                }
            } catch (NullPointerException ey) {
                return false;
            }
        }
    }

    @Override
    public boolean deleteVertice(int nome) throws TException {
        try {
            synchronized (g.vertices.get(nome)) {
                Set<ArestaId> chaves = g.arestas.keySet();
                for (ArestaId id : chaves) {
                    if (id.getNome1() == nome || id.getNome2() == nome) {
                        this.deleteAresta(id.getNome1(), id.getNome2());
                    }
                }
                return g.vertices.remove(nome) != null;
            }
        } catch (NullPointerException ex) {
            return false;
        }
    }

    @Override
    public boolean deleteAresta(int nome1, int nome2) throws TException {
        ArestaId id = new ArestaId(nome1, nome2);
        ArestaId id2 = new ArestaId(nome2, nome1);
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
    }

    @Override
    public List<Vertice> listVerticesDoGrafo() throws TException {
        synchronized (g.vertices) {
            return new ArrayList<>(g.vertices.values());
        }
    }

    @Override
    public List<Aresta> listArestasDoGrafo() throws TException {
        synchronized (g.arestas) {
            return new ArrayList<>(g.arestas.values());
        }
    }

    @Override
    public List<Aresta> listArestasDoVertice(int nome) throws NullException, TException {
        if (!g.vertices.containsKey(nome)) {
            throw new NullException("O vértice '" + nome + "' não existe");
        }
        List<Aresta> list = this.listArestasDoGrafo();
        Iterator<Aresta> it = list.iterator();
        Aresta a;
        while (it.hasNext()) {
            a = it.next();
            if (a.getVertice1() != nome && a.getVertice2() != nome) {
                it.remove();
            }
        }
        return list;
    }

    @Override
    public List<Vertice> listVizinhosDoVertice(int nome) throws NullException, TException {
        List<Aresta> list = this.listArestasDoVertice(nome);
        List<Vertice> result = new ArrayList<>();
        for (Aresta a : list) {
            try {
                synchronized (g.arestas.get(a)) {
                    if (a.getVertice1() == nome && !result.contains(a.getVertice2())) {
                        synchronized (g.vertices.get(a.getVertice2())) {
                            result.add(g.vertices.get(a.getVertice2()));
                        }
                    } else if (a.getVertice2() == nome && !result.contains(a.getVertice1())) {
                        synchronized (g.vertices.get(a.getVertice1())) {
                            result.add(g.vertices.get(a.getVertice1()));
                        }
                    }
                }
            } catch (NullPointerException ex) {;
            }
        }
        return result;
    }

    @Override
    public List<Vertice> listMenorCaminho(int nome1, int nome2) throws NullException, TException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getServerId() throws TException {
        return this.serverId;
    }

    @Override
    public void setFt() throws TException {
        if (ft == null) {
            ft = new Object[m][2]; //M linhas e 2 colunas (ID, Socket)

            //Obtendo IDs de todos os servidores
            TreeMap<Integer, TTransport> temp = new TreeMap<>();
            for (int i = 0; i < servers.length; i += 2) {
                conectar(servers[i], servers[i + 1]);
                temp.put(node.getServerId(), transport);
            }

            //Salvando o servidor anterior
            if (temp.floorKey(serverId) != null) {
                anteriorId = temp.floorKey(serverId);
            } else {
                anteriorId = temp.lastKey();
            }

            //Monta tabela
            for (int i = 0; i < m; i++) {
                int ftpi = serverId + (int) Math.pow(2, i);// Não é usado 2 ^ i-1 porque i começa em 0
                if (ftpi >= Math.pow(2, m)) {
                    ftpi -= Math.pow(2, m);
                }
                ft[i][0] = temp.ceilingKey(ftpi);
                if (ft[i][0] == null) {
                    ft[i][0] = temp.firstKey();
                }
                ft[i][1] = temp.get((int) ft[i][0]);
            }

            //Descartar a lista com TODOS os servidores que ficou armazenada temporariamente
            servers = null;
            System.out.println("Finger Table:");
            for (int i = 0; i < m; i++) {
                System.out.println("|" + (i + 1) + "|" + (int) ft[i][0] + " |");
            }
        }
    }

}
