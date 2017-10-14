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
import java.util.concurrent.ConcurrentHashMap;
import org.apache.thrift.TException;

/**
 *
 * @author heito
 */
public class Servidor implements Handler.Iface {

    private final Grafo g = new Grafo(new ConcurrentHashMap<>(), new ConcurrentHashMap<>());

    @Override
    public boolean createVertice(Vertice v) throws TException {
        if (v.getNome() < 0) {
            return false;
        }
        return g.vertices.putIfAbsent(v.getNome(), v) == null;
    }

    @Override
    public boolean createAresta(Aresta a) throws TException {
        IntInt id = new IntInt(a.getVertice1(), a.getVertice2());
        if (id.getNome1() == id.getNome2()) {
            return false;
        }
        try {
            if (id.getNome1() < id.getNome2()) {
                synchronized (g.vertices.get(id.getNome1())) {
                    synchronized (g.vertices.get(id.getNome2())) {
                        return g.arestas.putIfAbsent(id, a) == null;
                    }
                }
            } else {
                synchronized (g.vertices.get(id.getNome2())) {
                    synchronized (g.vertices.get(id.getNome1())) {
                        return g.arestas.putIfAbsent(id, a) == null;
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
        IntInt id = new IntInt(nome1, nome2);
        try {
            synchronized (g.arestas.get(id)) {
                return g.arestas.get(id);
            }
        } catch (NullPointerException ex) {
            throw new NullException("A aresta '" + nome1 + "," + nome2 + "' não existe.");
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
        IntInt id = new IntInt(a.getVertice1(), a.getVertice2());
        try {
            synchronized (g.arestas.get(id)) {
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
            }
        } catch (NullPointerException ex) {
            return false;
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
        IntInt id = new IntInt(nome1, nome2);
        try {
            synchronized (g.arestas.get(id)) {
                return g.arestas.remove(id) != null;
            }
        } catch (NullPointerException ex) {
            return false;
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

}

//Classe para facilitar indexação no HashMap de arestas
class IntInt extends ArestaId {

    public IntInt(int nome1, int nome2) {
        super(nome1, nome2);
    }

    @Override
    public int hashCode() {
        return ((this.nome1 * 2 + this.nome2 % 3) * this.nome1) + this.nome2;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof IntInt && this.nome1 == ((IntInt) obj).nome1 && this.nome2 == ((IntInt) obj).nome2;

    }
}
