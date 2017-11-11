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
                        + "13) LIST   - Menor Caminho de A até B\n"
                        + "14) DEMO   - Demonstração da Concorrência\n"
                        + "15) TESTE  - Bloquear Vértice\n"
                        + "16) TESTE  - Desbloquear Vértice\n"
                        + "17) Fechar Cliente");
                opcao = l.lerOpcao(1, 17);
                
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
                    case 13: {//13) LIST   - Menor Caminho de A até B
                        i.print("Nome (Vértice A): ");
                        nome = l.lerInteiro();
                        i.print("Nome (Vértice B): ");
                        nomeB = l.lerInteiro();
                        
                        try {
                            List<Vertice> lista = client.listMenorCaminho(nome, nomeB);
                            if (lista.isEmpty()) {
                                i.printLn("\nNão há um caminho.");
                            } else {
                                i.printLn(lista);
                            }
                        } catch (NullException ex) {
                            i.printLn("\n" + ex.mensagem);
                        }
                        
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
                    case 15: {//TESTE  - Bloquear Vértice
                        i.print("Nome: ");
                        nome = l.lerInteiro();
                        if (client.bloqueiaVertice(nome)) {
                            i.printLn("\nVértice " + nome + " bloqueado.");
                        } else {
                            i.printLn("\nO vértice " + nome + " não existe.");
                        }
                        break;
                    }
                    case 16: {//TESTE  - Desbloquear Vértice
                        i.print("Nome: ");
                        nome = l.lerInteiro();
                        client.desbloqueiaVertice(nome);
                        i.printLn("\nComando para desbloquear o vértice" + nome + " enviado.");
                        break;
                    }
                    case 17: {//15) Sair                                                
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
