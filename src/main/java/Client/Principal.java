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
 * @author steff
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
                i.imprimirLn(
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
                        + "13) DEMO   - Demonstração dos Requisitos\n"
                        + "14) Sair");
                opcao = l.lerOpcao(1, 14);

                switch (opcao) {
                    case 1: {//1) CREATE - Vértice
                        i.imprimir("Nome: ");
                        nome = l.lerInteiro();
                        i.imprimir("Cor: ");
                        cor = l.lerInteiro();
                        i.imprimir("Descrição: ");
                        desc = l.lerTexto();
                        i.imprimir("Peso: ");
                        peso = l.lerReal();

                        Vertice v = new Vertice(nome, cor, desc, peso);
                        if (client.createVertice(v)) {
                            i.imprimirLn("\nO vértice '" + nome + "' foi criado com sucesso.");
                        } else {
                            i.imprimirLn("\nO vértice '" + nome + "' não foi criado.");
                        }
                        break;
                    }
                    case 2: {//2) READ   - Vértice
                        i.imprimir("Nome: ");
                        nome = l.lerInteiro();

                        try {
                            Vertice v = client.readVertice(nome);
                            i.imprimirLn(v);
                        } catch (NullException ex) {
                            i.imprimirLn("\n" + ex.mensagem);
                        }
                        break;
                    }
                    case 3: {//3) UPDATE - Vértice
                        i.imprimir("Nome: ");
                        nome = l.lerInteiro();
                        i.imprimir("Cor: ");
                        cor = l.lerInteiro();
                        i.imprimir("Descrição: ");
                        desc = l.lerTexto();
                        i.imprimir("Peso: ");
                        peso = l.lerReal();

                        Vertice v = new Vertice(nome, cor, desc, peso);
                        if (client.updateVertice(v)) {
                            i.imprimirLn("\nO vértice '" + nome + "' foi atualizado com sucesso.");
                        } else {
                            i.imprimirLn("\nO vértice '" + nome + "' não existe.");
                        }
                        break;
                    }
                    case 4: {//4) DELETE - Vértice
                        i.imprimir("Nome: ");
                        nome = l.lerInteiro();

                        if (client.deleteVertice(nome)) {
                            i.imprimirLn("\nO vértice '" + nome + "' foi excluído com sucesso.");
                        } else {
                            i.imprimirLn("\nO vértice '" + nome + "' não existe.");
                        }
                        break;
                    }
                    case 5: {//5) CREATE - Aresta
                        i.imprimir("Nome (Vértice A): ");
                        nome = l.lerInteiro();
                        i.imprimir("Nome (Vértice B): ");
                        nomeB = l.lerInteiro();
                        i.imprimir("Peso: ");
                        peso = l.lerReal();
                        i.imprimir("Direcionado: ");
                        direc = l.lerSimNao();
                        i.imprimir("Descrição: ");
                        desc = l.lerTexto();

                        Vertice v1 = new Vertice();
                        v1.setNome(nome);
                        Vertice v2 = new Vertice();
                        v2.setNome(nomeB);
                        Aresta a = new Aresta(v1, v2, peso, direc, desc);
                        if (client.createAresta(a)) {
                            i.imprimirLn("\nA aresta '" + nome + "," + nomeB + "' foi criada com sucesso.");
                        } else {
                            i.imprimirLn("\nA aresta '" + nome + "," + nomeB + "' não foi criada.");
                        }

                        break;
                    }
                    case 6: {//6) READ   -  Aresta
                        i.imprimir("Nome (Vértice A): ");
                        nome = l.lerInteiro();
                        i.imprimir("Nome (Vértice B): ");
                        nomeB = l.lerInteiro();

                        try {
                            Aresta a = client.readAresta(nome, nomeB);
                            i.imprimirLn(a);
                        } catch (NullException ex) {
                            i.imprimirLn("\n" + ex.mensagem);
                        }
                        break;
                    }
                    case 7: {//7) UPDATE - Aresta
                        i.imprimir("Nome (Vértice A): ");
                        nome = l.lerInteiro();
                        i.imprimir("Nome (Vértice B): ");
                        nomeB = l.lerInteiro();
                        i.imprimir("Peso: ");
                        peso = l.lerReal();
                        i.imprimir("Direcionado: ");
                        direc = l.lerSimNao();
                        i.imprimir("Descrição: ");
                        desc = l.lerTexto();

                        Vertice v1 = new Vertice();
                        v1.setNome(nome);
                        Vertice v2 = new Vertice();
                        v2.setNome(nomeB);
                        Aresta a = new Aresta(v1, v2, peso, direc, desc);
                        if (client.updateAresta(a)) {
                            i.imprimirLn("\nA aresta '" + nome + "," + nomeB + "' foi atualizada com sucesso.");
                        } else {
                            i.imprimirLn("\nA aresta '" + nome + "," + nomeB + "' não existe.");
                        }
                        break;
                    }
                    case 8: {//8) DELETE - Aresta
                        i.imprimir("Nome (Vértice A): ");
                        nome = l.lerInteiro();
                        i.imprimir("Nome (Vértice B): ");
                        nomeB = l.lerInteiro();

                        if (client.deleteAresta(nome, nomeB)) {
                            i.imprimirLn("\nA aresta '" + nome + "," + nomeB + "' foi excluída com sucesso.");
                        } else {
                            i.imprimirLn("\nA aresta '" + nome + "," + nomeB + "' não existe.");
                        }
                        break;
                    }
                    case 9: {//9) LIST   - Todos os Vértices
                        List<Vertice> lista = client.listVerticesDoGrafo();
                        if (lista.isEmpty()) {
                            i.imprimirLn("\nNão há vértices.");
                        } else {
                            i.imprimirLn(lista);
                        }
                        break;
                    }
                    case 10: {//10) LIST   - Todas as Arestas
                        List<Aresta> lista = client.listArestasDoGrafo();
                        if (lista.isEmpty()) {
                            i.imprimirLn("\nNão há arestas.");
                        } else {
                            i.imprimirLn(lista);
                        }
                        break;
                    }
                    case 11: {//11) LIST   - Arestas de um Vértice
                        i.imprimir("Nome: ");
                        nome = l.lerInteiro();

                        try {
                            List<Aresta> lista = client.listArestasDoVertice(nome);
                            if (lista.isEmpty()) {
                                i.imprimirLn("\nNão há arestas.");
                            } else {
                                i.imprimirLn(lista);
                            }
                        } catch (NullException ex) {
                            i.imprimirLn("\n" + ex.mensagem);
                        }
                        break;
                    }
                    case 12: {//12) LIST   - Vértices Vizinhos de um Vértice
                        i.imprimir("Nome: ");
                        nome = l.lerInteiro();

                        try {
                            List<Vertice> lista = client.listVizinhosDoVertice(nome);
                            if (lista.isEmpty()) {
                                i.imprimirLn("\nNão há vizinhos.");
                            } else {
                                i.imprimirLn(lista);
                            }
                        } catch (NullException ex) {
                            i.imprimirLn("\n" + ex.mensagem);
                        }
                        break;
                    }
                    case 13: {//13) DEMO   - Demonstração dos Requisitos

                        int j;
                        for (j = -1; j <= 1; j++) {

                            i.imprimirLn("Criar vértice " + j + ": " + client.createVertice(new Vertice(j, j, String.valueOf(j), (double) j)));
                            i.imprimirLn("Criar vértice repetido " + j + ": " + client.createVertice(new Vertice(j, j, String.valueOf(j), (double) j)));
                            try {
                                i.imprimirLn("Ler vértice " + j + ": " + client.readVertice(j).toString());
                            } catch (NullException ex) {
                                i.imprimirLn("Ler vértice " + j + ": " + ex.mensagem);
                            }
                            i.imprimirLn("Alterar vértice " + j + ": " + client.updateVertice(new Vertice(j, j + 1, String.valueOf(j + 1), (double) j + 1)));
                            try {
                                i.imprimirLn("Ler vértice atualizado " + j + ": " + client.readVertice(j).toString());
                            } catch (NullException ex) {
                                i.imprimirLn("Ler vértice atualizado " + j + ": " + ex.mensagem);
                            }
                            i.imprimirLn("Excluir vértice " + j + ": " + client.deleteVertice(j));

                        }
                        for (j = 1; j <= 5; j++) {
                            i.imprimirLn("Criar vértice " + j + ": " + client.createVertice(new Vertice(j, j, String.valueOf(j), (double) j)));
                        }
                        for (j = -1; j <= 5; j++) {
                            try {
                                i.imprimirLn("Criar aresta " + j + "," + (j + 1) + ": " + client.createAresta(new Aresta(client.readVertice(j), client.readVertice(j + 1), (double) j, false, String.valueOf(j))));
                            } catch (NullException ex) {
                                i.imprimirLn("Criar aresta " + j + "," + (j + 1) + ": " + ex.mensagem);
                            }
                            try {
                                i.imprimirLn("Criar aresta repetida " + j + "," + (j + 1) + ": " + client.createAresta(new Aresta(client.readVertice(j), client.readVertice(j + 1), (double) j, false, String.valueOf(j))));
                            } catch (NullException ex) {
                                i.imprimirLn("Criar aresta repetida " + j + "," + (j + 1) + ": " + ex.mensagem);
                            }
                            try {
                                i.imprimirLn("Criar aresta inválida " + j + "," + j + ": " + client.createAresta(new Aresta(client.readVertice(j), client.readVertice(j), (double) j, false, String.valueOf(j))));
                            } catch (NullException ex) {
                                i.imprimirLn("Criar aresta inválida " + j + "," + j + ": " + ex.mensagem);
                            }
                            try {
                                i.imprimirLn("Ler aresta " + j + "," + (j + 1) + ": ");
                                i.imprimirLn(client.readAresta(j, j + 1));
                            } catch (NullException ex) {
                                i.imprimirLn(ex.mensagem);
                            }
                            try {
                                i.imprimirLn("Alterar aresta " + j + "," + (j + 1) + ": " + client.updateAresta(new Aresta(client.readVertice(j), client.readVertice(j + 1), (double) j + 1, true, String.valueOf(j + 1))));
                            } catch (NullException ex) {
                                i.imprimirLn("Alterar aresta " + j + "," + (j + 1) + ": " + ex.mensagem);
                            }
                            try {
                                i.imprimirLn("Ler aresta atualizada " + j + "," + (j + 1) + ": ");
                                i.imprimirLn(client.readAresta(j, j + 1));
                            } catch (NullException ex) {
                                i.imprimirLn(ex.mensagem);
                            }
                            i.imprimirLn("Excluir aresta " + j + "," + (j + 1) + ": " + client.deleteAresta(j, j + 1));
                        }
                        for (j = 1; j < 5; j++) {
                            i.imprimirLn("Criar aresta " + j + "," + (j + 1) + ": " + client.createAresta(new Aresta(client.readVertice(j), client.readVertice(j + 1), (double) j, false, String.valueOf(j))));
                        }
                        i.imprimirLn("Listas todos os vértices: ");
                        i.imprimirLn(client.listVerticesDoGrafo());
                        i.imprimirLn("Listas todas as arestas: ");
                        i.imprimirLn(client.listArestasDoGrafo());
                        for (j = 1; j <= 5; j++) {
                            i.imprimirLn("Listar todas as arestas de " + j + ": ");
                            i.imprimirLn(client.listArestasDoVertice(j));
                            i.imprimirLn("Listar todos os vizinhos de " + j + ": ");
                            i.imprimirLn(client.listVizinhosDoVertice(j));
                        }
                        for (j = 1; j <= 2; j++) {
                            i.imprimirLn("Excluir vértice " + j + ": " + client.deleteVertice(j));
                        }
                        i.imprimirLn("Listas todos os vértices: ");
                        i.imprimirLn(client.listVerticesDoGrafo());
                        i.imprimirLn("Listas todas as arestas: ");
                        i.imprimirLn(client.listArestasDoGrafo());
                        for (j = 3; j < 4; j++) {
                            i.imprimirLn("Excluir aresta " + j + "," + (j + 1) + ": " + client.deleteAresta(j, j + 1));
                        }
                        i.imprimirLn("Listas todos os vértices: ");
                        i.imprimirLn(client.listVerticesDoGrafo());
                        i.imprimirLn("Listas todas as arestas: ");
                        i.imprimirLn(client.listArestasDoGrafo());
                        break;
                    }
                    case 14: {//14) Sair
                        i.imprimirLn("Fechando conexão com o servidor...");
                        transport.close();
                        i.imprimirLn("Conexão encerrada, saindo...");
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
            System.out.println("Houve um erro inesperado ao executar esta operação. Mensagem de erro: " + ex);
        }

    }

}

//Classe com métodos para impressão de objetos
class Impressora {

    public void imprimirLn(String texto) {
        System.out.println(texto);
    }

    public void imprimir(String texto) {
        System.out.print(texto);
    }

    public void imprimir(Vertice v) {
        System.out.print("\n" + v.toString());
    }

    public void imprimir(Aresta a) {
        System.out.print("\nAresta(vertice1:" + a.getVertice1().getNome() + ", vertice2:" + a.getVertice2().getNome() + ", peso:" + a.getPeso() + ", direc:" + String.valueOf(a.isDirec()) + ", desc:" + a.getDesc() + ")");
    }

    public void imprimirLn(Vertice v) {
        imprimir(v);
        System.out.println("");
    }

    public void imprimirLn(Aresta a) {
        imprimir(a);
        System.out.println("");
    }

    public void imprimirLn(List<?> lista) {
        if (lista.size() > 0) {
            if (lista.get(0) instanceof Vertice) {
                for (Vertice v : (List<Vertice>) lista) {
                    imprimir(v);
                }
                imprimirLn("");
            } else if (lista.get(0) instanceof Aresta) {
                for (Aresta a : (List<Aresta>) lista) {
                    imprimir(a);
                }
                imprimirLn("");
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
