/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Grafo.*;
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
                try {
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
                            + "13) LIST   - Menor Caminho de A até B\n");
                          /*+ "14) TESTE  - Criar um grafo inicial\n"
                            + "15) TESTE  - Bloquear Vértice\n"
                            + "16) TESTE  - Desbloquear Vértice\n"
                            + "17) TESTE  - Distribuição de Vértices\n"
                            + "18) Fechar Cliente");*/
                    opcao = l.lerOpcao(1, 13);

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
                            if (conectar(args).createVertice(v)) {
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
                                Vertice v = conectar(args).readVertice(nome);
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
                            if (conectar(args).updateVertice(v)) {
                                i.printLn("\nO vértice '" + nome + "' foi atualizado com sucesso.");
                            } else {
                                i.printLn("\nO vértice '" + nome + "' não existe.");
                            }
                            break;
                        }
                        case 4: {//4) DELETE - Vértice
                            i.print("Nome: ");
                            nome = l.lerInteiro();

                            if (conectar(args).deleteVertice(nome)) {
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
                            if (conectar(args).createAresta(a)) {
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
                                Aresta a = conectar(args).readAresta(nome, nomeB);
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
                                Aresta a = conectar(args).readAresta(nome, nomeB);
                                a.setDesc(desc);
                                a.setPeso(peso);

                                if (conectar(args).updateAresta(a)) {
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

                            if (conectar(args).deleteAresta(nome, nomeB)) {
                                i.printLn("\nA aresta '" + nome + "," + nomeB + "' foi excluída com sucesso.");
                            } else {
                                i.printLn("\nA aresta '" + nome + "," + nomeB + "' não existe.");
                            }
                            break;
                        }
                        case 9: {//9) LIST   - Todos os Vértices
                            List<Vertice> lista = conectar(args).listVerticesDoGrafo();
                            if (lista.isEmpty()) {
                                i.printLn("\nNão há vértices.");
                            } else {
                                i.printLn(lista);
                            }
                            break;
                        }
                        case 10: {//10) LIST   - Todas as Arestas
                            List<Aresta> lista = conectar(args).listArestasDoGrafo();
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
                                List<Aresta> lista = conectar(args).listArestasDoVertice(nome);
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
                                List<Vertice> lista = conectar(args).listVizinhosDoVertice(nome);
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
                        case 13: {//13) LIST   - Menor Caminho de A até B
                            i.print("Nome (Vértice A): ");
                            nome = l.lerInteiro();
                            i.print("Nome (Vértice B): ");
                            nomeB = l.lerInteiro();

                            try {
                                List<Vertice> lista = conectar(args).listMenorCaminho(nome, nomeB);
                                if (lista.isEmpty()) {
                                    i.printLn("\nNão há um caminho.");
                                } else {
                                    peso = 0;
                                    for (int j = 0; j < lista.size() - 1; j++) {
                                        peso += conectar(args).readAresta(lista.get(j).getNome(), lista.get(j + 1).getNome()).getPeso();
                                    }
                                    i.printLn(lista);
                                    i.printLn("Peso do caminho: " + peso);
                                }
                            } catch (NullException ex) {
                                i.printLn("\n" + ex.mensagem);
                            }

                            break;
                        }/*
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
                        case 15: {//TESTE  - Bloquear Vértice
                            i.print("Nome: ");
                            nome = l.lerInteiro();
                            if (conectar(args).bloqueiaVertice(nome)) {
                                i.printLn("\nVértice " + nome + " bloqueado.");
                            } else {
                                i.printLn("\nO vértice " + nome + " não existe.");
                            }
                            break;
                        }
                        case 16: {//TESTE  - Desbloquear Vértice
                            i.print("Nome: ");
                            nome = l.lerInteiro();
                            conectar(args).desbloqueiaVertice(nome);
                            i.printLn("\nComando para desbloquear o vértice " + nome + " enviado.");
                            break;
                        }
                        case 17: {//17) TESTE  - Distribuição de Vértices
                            i.printLn("Criando 64 vértices.");
                            for (int j = 0; j < 64; j++) {
                                conectar(args).createVertice(new Vertice(j, j, "" + j, j));
                            }
                            break;
                        }
                        case 18: {//15) Sair                                                
                            i.printLn("Saindo...");
                            l.close();
                            System.exit(0);
                            break;
                        }*/
                    }
                } catch (Exception ex) {
                    System.out.println("Houve um erro no servidor, ele pode estar offline ou corrompido.");
                }
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            System.out.println("Erro nos parâmetros da linha de comando, o sistema não será iniciado. Mensagem de erro: " + ex);
        }
    }

    // Este método retorna uma conexão ativa para o cliente
    public static Handler.Client conectar(String[] servers) throws Exception {

        TTransport transport;
        TProtocol protocol;
        Handler.Client client = null;

        for (int i = 0; i < servers.length; i += 2) {
            try {
                transport = new TSocket(servers[i], Integer.parseInt(servers[i + 1]));
                transport.open();
                protocol = new TBinaryProtocol(transport);
                client = new Handler.Client(protocol);
                break;
            } catch (TTransportException ex) {
            }
        }

        if (client == null) {
            throw new Exception();
        } else {
            return client;
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
