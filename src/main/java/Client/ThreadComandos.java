/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Grafo.Aresta;
import Grafo.Vertice;
import java.util.ArrayList;
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
public class ThreadComandos extends Thread {

    TTransport transport;
    TProtocol protocol;
    String ip;
    int porta;
    String nome;
    Grafo.Handler.Client client;

    public ThreadComandos(String ip, int porta, String nome) throws TTransportException, TException {
        this.transport = new TSocket(ip, porta);
        this.transport.open();
        this.protocol = new TBinaryProtocol(this.transport);
        this.ip = ip;
        this.porta = porta;
        this.client = new Grafo.Handler.Client(protocol);
        this.nome = nome;
    }

    public void TestarVertices() throws TException {
        for (int i = 1; i <= 50; i++) {
            Vertice vet = new Vertice(i, i, "v" + i, i);
            System.out.println("Thread " + this.nome + ": Tentando adicionar vértice " + vet.getNome());
            if (client.createVertice(vet)) {
                System.out.println("Thread " + this.nome + ": Vertice " + vet.getNome() + " adicionado!");
            } else {
                System.out.println("Thread " + this.nome + ": Vertice " + vet.getNome() + " não pode ser adicionado!");
            }
        }
    }

    public void TestarArestas() throws TException {
        Aresta ar[] = new Aresta[10];
        ar[0] = new Aresta(0, 1, 2, false, "0,1");
        ar[1] = new Aresta(1, 2, 3, true, "1,2");
        ar[2] = new Aresta(2, 3, 4, true, "2,3");
        ar[3] = new Aresta(1, 0, 3, true, "1,0");
        ar[4] = new Aresta(4, 5, 2, false, "4,5");
        ar[5] = new Aresta(6, 8, 7, true, "6,8");
        ar[6] = new Aresta(7, 3, 6, false, "7,3");
        ar[7] = new Aresta(8, 9, 7, true, "8,9");
        ar[8] = new Aresta(3, 6, 2, false, "3,6");
        ar[9] = new Aresta(9, 7, 1, false, "9,7");

        for (int i = 0; i < 7; i++) {
            System.out.println("Thread " + this.nome + ": Tentando adicionar aresta " + ar[i].getDesc());
            if (client.createAresta(ar[i])) {
                System.out.println("Thread " + this.nome + ": Aresta " + ar[i].getDesc() + " adicionada");
            } else {
                System.out.println("Thread " + this.nome + ": Aresta " + ar[i].getDesc() + " não pode ser adicionada!");
            }
        }
    }

    public void TestarExclusaoArestas() throws TException {
        for (int i = 1; i < 10; i++) {
            for (int j = 1; j < 10; j++) {
                System.out.println("Thread " + this.nome + ": Tentando remover aresta " + i + "," + j + " (caso existir)");
                if (client.deleteAresta(i, j)) {
                    System.out.println("Thread " + this.nome + ": Aresta " + i + "," + j + " removida com sucesso!");
                } else {
                    System.out.println("Thread " + this.nome + ": Aresta " + i + "," + j + " não pode ser removida!");
                }
            }
        }

    }

    public void TestarExclusaoVertices() throws TException {
        for (int i = 1; i < 60; i++) {
            System.out.println("Thread " + this.nome + ": Tentando remover vértice " + i);
            if (client.deleteVertice(i)) {
                System.out.println("Thread " + this.nome + ": Vértice " + i + " removido com sucesso!");
            } else {
                System.out.println("Thread " + this.nome + ": Vértice " + i + " não pode ser removido!");
            }
        }
    }

    public void TestarBuscaArestasExclusao() throws TException {
        System.out.println("Thread " + this.nome + ": Tentando listar arestas");
        ArrayList<Aresta> ars = (ArrayList<Aresta>) client.listArestasDoGrafo();
        System.out.println("Thread " + this.nome + ": Recuperou todas as arestas!");

        for (Aresta a : ars) {
            System.out.println("Thread " + this.nome + ": " + a.toString());
            System.out.println("Thread " + this.nome + ": Tentando remover aresta 1,2");
            if (!client.deleteAresta(1, 2)) {
                System.out.println("Thread " + this.nome + ": Aresta 1,2 não pode ser removida");
            } else {
                System.out.println("Thread " + this.nome + ": Aresta 1,2 removida com sucesso!");
            }

            System.out.println("Thread " + this.nome + ": Tentando remover aresta 2,3");
            if (!client.deleteAresta(2, 3)) {
                System.out.println("Thread " + this.nome + ": Aresta 2,3 não pode ser removida");
            } else {
                System.out.println("Thread " + this.nome + ": Aresta 2,3 removida com sucesso!");
            }
        }
    }

    @Override
    public void run() {
        try {
            this.TestarVertices();
            System.out.println("####################### THREAD " + this.nome + " TERMINOU DE TESTAR VÉRTICES #######################");
        } catch (TException ex) {
            System.out.println("Thread " + this.nome + ": Erro desconhecido ao adicionar vértices");
            System.out.println(ex.getCause() + " -> " + ex.getMessage());
        }

        try {
            this.TestarArestas();
            System.out.println("####################### THREAD " + this.nome + " TERMINOU DE TESTAR ARESTAS #######################");
        } catch (TException ex) {
            System.out.println("Thread " + this.nome + ": Erro desconhecido ao adicionar arestas");
            System.out.println(ex.getCause() + " -> " + ex.getMessage());
        }

        try {
            this.TestarBuscaArestasExclusao();
            System.out.println("####################### THREAD " + this.nome + " TERMINOU DE TESTAR BUSCA DE ARESTAS #######################");
        } catch (TException ex) {
            System.out.println("Thread " + this.nome + ": Erro desconhecido ao tentar remover arestas enquanto é feita a listagem");
            System.out.println(ex.getCause() + " -> " + ex.getMessage());
        }

        try {
            this.TestarExclusaoArestas();
            System.out.println("####################### THREAD " + this.nome + " TERMINOU DE TESTAR EXCLUSÃO DE ARESTAS #######################");
        } catch (TException ex) {
            System.out.println("Thread " + this.nome + ": Erro desconhecido ao excluir arestas");
            System.out.println(ex.getCause() + " -> " + ex.getMessage());
        }

        try {
            this.TestarExclusaoVertices();
            System.out.println("####################### THREAD " + this.nome + " TERMINOU DE TESTAR EXCLUSÃO DE VÉRTICES #######################");
        } catch (TException ex) {
            System.out.println("Thread " + this.nome + ": Erro desconhecido ao excluir vértices");
            System.out.println(ex.getCause() + " -> " + ex.getMessage());
        }
    }
}
