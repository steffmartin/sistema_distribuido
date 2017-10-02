/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Grafo.*;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

/**
 *
 * @author steff
 */
public class Principal {

    public static Servidor handler;
    public static Handler.Processor processor;

    public static void main(String[] args) {

        try {
            System.out.println("Ativando servidor (Porta: " + args[0] + ")");
            handler = new Servidor();
            processor = new Handler.Processor(handler);
            //Esta porta tem que ser passada por parâmetro ou arquivo, alterar!
            TServerTransport serverTransport = new TServerSocket(Integer.parseInt(args[0]));
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
            System.out.println("Servidor ativo.");
            server.serve();
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            System.out.println("Erro nos parâmetros da linha de comando.");
        } catch (TTransportException ex) {
            System.out.println("O servidor não pôde ser iniciado.");
        }
    }

}
