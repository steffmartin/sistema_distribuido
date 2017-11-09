/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Grafo.*;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
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
public class Principal {

    public static void main(String[] args) {

        try {
            //Configuração cliente-servidor
            System.out.println("Estabelecendo conexão com o servidor (IP e Porta: " + args[0] + ":" + args[1] + ")");
            TTransport transport = new TSocket(args[0], Integer.parseInt(args[1]));
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            Handler.Client client = new Handler.Client(protocol);
            System.out.println("Conexão estabelecida.");

            //Variáveis I/O
            Leitor l = new Leitor();
            Impressora i = new Impressora();
            int nome, nomeB, cor;
            String desc;
            float peso;
            boolean direc;
            int opcao;

            //Menu principal
            while (true) {
                i.printLn(
                        "\nOperação:\n\n"
                        + " 1) CREATE - Vértice\n"
                        + " 2) READ   - Vértice\n"
                        + " 3) UPDATE - Vértice\n"
                        + " 4) DELETE - Vértice\n"
                        + " 5) CREATE - Aresta\n"
                        + " 6) READ   - Aresta\n"
                        + " 7) UPDATE - Aresta\n"
                        + " 8) DELETE - Aresta\n"
                        + " 9) LIST   - Todos os Vértices\n"
                        + "10) LIST   - Todas as Arestas\n"
                        + "11) LIST   - Arestas de um Vértice\n"
                        + "12) LIST   - Vértices Vizinhos de um Vértice\n"
                        + "13) LIST   - Busca pelo menor caminho\n"
                        + "14) DEMO   - Demonstração da Concorrência\n"
                        + "15) Sair");
                opcao = l.lerOpcao(1, 15);

                switch (opcao) {
                    case 1: {//1) CREATE - Vértice
                        i.print("Nome: ");
                        nome = l.lerInteiro();
                        i.print("Cor: ");
                        cor = l.lerInteiro();
                        i.print("Descrição: ");
                        desc = l.lerTexto();
                        i.print("Peso: ");
                        peso = l.lerReal();

                        Vertice v = new Vertice(nome, cor, desc, peso);
                        if (client.createVertice(v)) {
                            i.printLn("\nO vértice '" + nome + "' foi criado com sucesso.");
                        } else {
                            i.printLn("\nO vértice '" + nome + "' não foi criado.");
                        }
                        break;
                    }
                    case 2: {//2) READ   - Vértice
                        i.print("Nome: ");
                        nome = l.lerInteiro();

                        try {
                            Vertice v = client.readVertice(nome);
                            i.printLn(v);
                        } catch (NullException ex) {
                            i.printLn("\n" + ex.mensagem);
                        }
                        break;
                    }
                    case 3: {//3) UPDATE - Vértice
                        i.print("Nome: ");
                        nome = l.lerInteiro();
                        i.print("Cor: ");
                        cor = l.lerInteiro();
                        i.print("Descrição: ");
                        desc = l.lerTexto();
                        i.print("Peso: ");
                        peso = l.lerReal();

                        Vertice v = new Vertice(nome, cor, desc, peso);
                        if (client.updateVertice(v)) {
                            i.printLn("\nO vértice '" + nome + "' foi atualizado com sucesso.");
                        } else {
                            i.printLn("\nO vértice '" + nome + "' não existe.");
                        }
                        break;
                    }
                    case 4: {//4) DELETE - Vértice
                        i.print("Nome: ");
                        nome = l.lerInteiro();

                        if (client.deleteVertice(nome)) {
                            i.printLn("\nO vértice '" + nome + "' foi excluído com sucesso.");
                        } else {
                            i.printLn("\nO vértice '" + nome + "' não existe.");
                        }
                        break;
                    }
                    case 5: {//5) CREATE - Aresta
                        i.print("Nome (Vértice A): ");
                        nome = l.lerInteiro();
                        i.print("Nome (Vértice B): ");
                        nomeB = l.lerInteiro();
                        i.print("Peso: ");
                        peso = l.lerReal();
                        i.print("Direcionado: ");
                        direc = l.lerSimNao();
                        i.print("Descrição: ");
                        desc = l.lerTexto();

                        Aresta a = new Aresta(nome, nomeB, peso, direc, desc);
                        if (client.createAresta(a)) {
                            i.printLn("\nA aresta '" + nome + "," + nomeB + "' foi criada com sucesso.");
                        } else {
                            i.printLn("\nA aresta '" + nome + "," + nomeB + "' não foi criada.");
                        }

                        break;
                    }
                    case 6: {//6) READ   -  Aresta
                        i.print("Nome (Vértice A): ");
                        nome = l.lerInteiro();
                        i.print("Nome (Vértice B): ");
                        nomeB = l.lerInteiro();

                        try {
                            Aresta a = client.readAresta(nome, nomeB);
                            i.printLn(a);
                        } catch (NullException ex) {
                            i.printLn("\n" + ex.mensagem);
                        }
                        break;
                    }
                    case 7: {//7) UPDATE - Aresta
                        i.print("Nome (Vértice A): ");
                        nome = l.lerInteiro();
                        i.print("Nome (Vértice B): ");
                        nomeB = l.lerInteiro();
                        i.print("Peso: ");
                        peso = l.lerReal();
                        i.print("Descrição: ");
                        desc = l.lerTexto();

                        try {
                            Aresta a = client.readAresta(nome, nomeB);
                            a.setDesc(desc);
                            a.setPeso(peso);

                            if (client.updateAresta(a)) {
                                i.printLn("\nA aresta '" + nome + "," + nomeB + "' foi atualizada com sucesso.");
                            } else {
                                i.printLn("\nA aresta '" + nome + "," + nomeB + "'não pode ser alterada.");
                            }
                        } catch (NullException e) {
                            i.printLn("\nA aresta '" + nome + "," + nomeB + "' não existe.");
                        }
                        break;
                    }
                    case 8: {//8) DELETE - Aresta
                        i.print("Nome (Vértice A): ");
                        nome = l.lerInteiro();
                        i.print("Nome (Vértice B): ");
                        nomeB = l.lerInteiro();

                        if (client.deleteAresta(nome, nomeB)) {
                            i.printLn("\nA aresta '" + nome + "," + nomeB + "' foi excluída com sucesso.");
                        } else {
                            i.printLn("\nA aresta '" + nome + "," + nomeB + "' não existe.");
                        }
                        break;
                    }
                    case 9: {//9) LIST   - Todos os Vértices
                        List<Vertice> lista = client.listVerticesDoGrafo();
                        if (lista.isEmpty()) {
                            i.printLn("\nNão há vértices.");
                        } else {
                            i.printLn(lista);
                        }
                        break;
                    }
                    case 10: {//10) LIST   - Todas as Arestas
                        List<Aresta> lista = client.listArestasDoGrafo();
                        if (lista.isEmpty()) {
                            i.printLn("\nNão há arestas.");
                        } else {
                            i.printLn(lista);
                        }
                        break;
                    }
                    case 11: {//11) LIST   - Arestas de um Vértice
                        i.print("Nome: ");
                        nome = l.lerInteiro();

                        try {
                            List<Aresta> lista = client.listArestasDoVertice(nome);
                            if (lista.isEmpty()) {
                                i.printLn("\nNão há arestas.");
                            } else {
                                i.printLn(lista);
                            }
                        } catch (NullException ex) {
                            i.printLn("\n" + ex.mensagem);
                        }
                        break;
                    }
                    case 12: {//12) LIST   - Vértices Vizinhos de um Vértice
                        i.print("Nome: ");
                        nome = l.lerInteiro();

                        try {
                            List<Vertice> lista = client.listVizinhosDoVertice(nome);
                            if (lista.isEmpty()) {
                                i.printLn("\nNão há vizinhos.");
                            } else {
                                i.printLn(lista);
                            }
                        } catch (NullException ex) {
                            i.printLn("\n" + ex.mensagem);
                        }
                        break;
                    }
                    case 13: {//13) DEMO   - Demonstração dos Requisitos
                        
                        //Para fins de testes, copie o "//criando grafo" e cole aqui, na proxima linha.                        
                        i.print("Nome (Vertice A): ");
                        nome = l.lerInteiro();
                        
                        i.print("Nome (Vertice B): ");
                        nomeB = l.lerInteiro();

                        try {
                            List<Vertice> lista = client.menorCaminho(nome, nomeB, new HashMap<>(), new HashMap<>());
                            
                            i.print("\nCaminho: ");
                            for(int it = lista.size() - 1; it >= 0; it--){
                                System.out.print(lista.get(it).getNome());                            
                                if(it != 0)
                                    i.print(" - ");

                            }
                            i.printLn("\n");  
                        } catch (NullException ex) {
                            i.printLn("\n" + ex.mensagem);
                        }
                        
                        
                        //criando grafo
                        /*
                        Vertice v1 = new Vertice(1, 1, "1", 1.0);
                        Vertice v2 = new Vertice(2, 2, "2", 1.0);
                        Vertice v3 = new Vertice(3, 3, "3", 1.0);
                        Vertice v4 = new Vertice(4, 4, "4", 1.0);
                        Vertice v5 = new Vertice(5, 5, "5", 1.0);
                        
                        Aresta ar1 = new Aresta(1, 2, 1, true, "ar1");
                        Aresta ar2 = new Aresta(2, 3, 4, true, "ar2");
                        Aresta ar3 = new Aresta(3, 5, 2, true, "ar3");
                        Aresta ar4 = new Aresta(2, 4, 5, true, "ar4");
                        Aresta ar5 = new Aresta(5, 2, 11, true, "ar5");
                        Aresta ar6 = new Aresta(4, 1, 7, true, "ar6");
                        Aresta ar7 = new Aresta(4, 5, 3, true, "ar7");
                        
                        client.createVertice(v1);
                        client.createVertice(v2);
                        client.createVertice(v3);
                        client.createVertice(v4);
                        client.createVertice(v5);
                        
                        client.createAresta(ar1);
                        client.createAresta(ar2);
                        client.createAresta(ar3);
                        client.createAresta(ar4);
                        client.createAresta(ar5);
                        client.createAresta(ar6);
                        client.createAresta(ar7);
                        
                        //fim grafo
                        
                        //busca menor caminho
                        HashMap<Integer, Integer> ant = new HashMap<>();
                        HashMap<Integer, Double> dist = new HashMap<>();
                        
                        List<Vertice> vts = client.menorCaminho(1, 5, ant, dist);
                        
                        System.out.print("Caminho: ");
                        for(int it = vts.size() - 1; it >= 0; it--){
                            System.out.print(vts.get(it).getNome());                            
                            if(it != 0)
                                System.out.print(" - ");
                            
                        }
                        System.out.println("\n");  */
                        break;
                    }
                    case 14: {//DEMO   - Demonstração da Concorrência                           

                        ThreadComandos t1 = new ThreadComandos(args[0], Integer.parseInt(args[1]), "Um");
                        ThreadComandos t2 = new ThreadComandos(args[0], Integer.parseInt(args[1]), "Dois");
                        ThreadComandos t3 = new ThreadComandos(args[0], Integer.parseInt(args[1]), "Três");
                        ThreadComandos t4 = new ThreadComandos(args[0], Integer.parseInt(args[1]), "Quatro");
                        ThreadComandos t5 = new ThreadComandos(args[0], Integer.parseInt(args[1]), "Cinco");

                        try {
                            t1.start();
                            t2.start();
                            t3.start();
                            t4.start();
                            t5.start();
                            t1.join();
                            t2.join();
                            t3.join();
                            t4.join();
                            t5.join();
                        } catch (InterruptedException ex) {
                            System.out.println("Erro no Cliente: Falha ao rodar as threads");
                        }

                        break;
                    }
                    case 15: {//15) Sair                                                
                        i.printLn("Fechando conexão com o servidor...");
                        transport.close();
                        i.printLn("Conexão encerrada, saindo...");
                        l.close();
                        System.exit(0);
                        break;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            System.out.println("Erro nos parâmetros da linha de comando. Mensagem de erro: " + ex);
        } catch (TTransportException ex) {
            System.out.println("Houve um erro de comunicação com o servidor. Mensagem de erro: " + ex);
        } catch (TException ex) {
            System.out.println("Houve um erro inesperado ao executar esta operação. Mensagem de erro: ");
            ex.printStackTrace();
        }

    }

}

//Classe com métodos para impressão de objetos
class Impressora {

    public void printLn(String texto) {
        System.out.println(texto);
    }

    public void print(String texto) {
        System.out.print(texto);
    }

    public void print(Vertice v) {
        System.out.print("\n" + v.toString().replace(", bloqueado:true", "").replace(", bloqueado:false", ""));
    }

    public void print(Aresta a) {
        System.out.print("\n" + a.toString());
    }

    public void printLn(Vertice v) {
        print(v);
        System.out.println("");
    }

    public void printLn(Aresta a) {
        print(a);
        System.out.println("");
    }

    public void printLn(List<?> lista) {
        if (lista.size() > 0) {
            if (lista.get(0) instanceof Vertice) {
                for (Vertice v : (List<Vertice>) lista) {
                    print(v);
                }
                printLn("");
            } else if (lista.get(0) instanceof Aresta) {
                for (Aresta a : (List<Aresta>) lista) {
                    print(a);
                }
                printLn("");
            } else {
                System.out.println("\n" + lista.toString());
            }
        }
    }
}

//Classe com métodos para leitura facilidada de entradas de usuário
class Leitor {

    private final Scanner ler = new Scanner(System.in);

    public int lerOpcao(int min, int max) {
        System.out.print("\nOpção: ");
        int opcao;

        try {
            opcao = lerInteiro();
            if (opcao < min || opcao > max) {
                throw new Exception();
            }
        } catch (Exception e) {
            System.out.print("Opção não reconhecida, digite um número de " + min + " a " + max + ".");
            opcao = lerOpcao(min, max);
        }

        return opcao;
    }

    public int lerInteiro() {
        int opcao;

        try {
            opcao = ler.nextInt();
            ler.nextLine();
        } catch (RuntimeException e) {
            System.out.print("Digite um número: ");
            ler.nextLine();
            opcao = lerInteiro();
        }

        return opcao;
    }

    public float lerReal() {
        float opcao;

        try {
            opcao = ler.nextFloat();
            ler.nextLine();
        } catch (RuntimeException e) {
            System.out.print("Digite um número real: ");
            ler.nextLine();
            opcao = lerReal();
        }
        return opcao;
    }

    public String lerTexto() {
        return ler.nextLine();
    }

    public boolean lerSimNao() {
        boolean opcao;

        try {
            opcao = ler.nextBoolean();
            ler.nextLine();
        } catch (RuntimeException e) {
            System.out.print("Digite 'true' ou 'false': ");
            ler.nextLine();
            opcao = lerSimNao();
        }
        return opcao;
    }

    public void close() {
        ler.close();
    }
}
