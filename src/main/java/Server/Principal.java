/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Grafo.*;
import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

/**
 *
 * @author heitor, marcelo, rhaniel, steffan
 */
public class Principal {

    public static Servidor handler;
    public static Handler.Processor processor;

    public static void main(String[] args) {

        //args deve passar Porta_deste_servidor + M + ip_1 + porta_1 + ... + ip_n + porta_n, sendo n < 2^M -1
        //Ex: 8080 5 localhost 8001 124.0.0.1 8082 localhost 8003 ip_3 porta_3 ... ip_31 porta_31
        
        try {
            System.out.println("Ativando servidor...");
            handler = new Servidor(args);
            processor = new Handler.Processor(handler);
            TServerTransport serverTransport = new TServerSocket(Integer.parseInt(args[0]));
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
            System.out.println("Servidor ativo na porta " + args[0] + " com o ID " + handler.getServerId() + ".");
            server.serve();
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            System.out.println("Erro nos parâmetros da linha de comando.");
        } catch (TException ex) {
            System.out.println("O servidor não pôde ser iniciado.");
        }
    }

}
