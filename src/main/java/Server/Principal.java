/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Grafo.*;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public static List<Address> addresses = new LinkedList<>();
    public static CopycatServer copycatServer;
    public static TServer thriftServer;

    public static void main(String[] args) {

        //args deve passar ip_deste_servidor + Porta_deste_servidor + ip_clone_1 + porta_clone_1 + ip_clone_2 + porta_clone_2 + boolean + M + ip_1 + porta_1 + ip_1_clone_1 + porta_1_clone_1 + ip_1_clone_2 + porta_1_clone_2 + ... + ip_n + porta_n + ip_n_clone_1 + porta_n_clone_1 + ip_n_clone_2 + porta_n_clone_2
        //Sendo n < 2^M -1, e sendo boolean: indicar true para o primeiro servidor (que irá abrir o cluster) e false par os demais (que irão fazer join)
        //Ex: localhost 7070 localhost 7071 localhost 7072 true 5 localhost 8080 localhost 8081 localhost 8082 localhost 9090 localhost 9091 localhost 9092
        try {
            //Parte do Thrift
            System.out.println("Ativando servidor...");
            handler = new Servidor(args);
            processor = new Handler.Processor(handler);
            TServerTransport serverTransport = new TServerSocket(Integer.parseInt(args[1]));
            thriftServer = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
            System.out.println("Servidor ativo em " + args[0] + "/" + args[1] + " com o ID " + handler.getServerId() + ".");

            //Parte do Raft, conforme exemplo
            addresses.add(new Address(args[0], Integer.parseInt(args[2])));
            addresses.add(new Address(args[4], Integer.parseInt(args[6])));
            addresses.add(new Address(args[7], Integer.parseInt(args[9])));
            CopycatServer.Builder builder = CopycatServer.builder(addresses.get(0))//No código exemplo original aqui viria .builder(addresses.get(0)), mas tive que alterar esta parte do exeplo original porque dava erro falando que o endereço já era usado
                    .withStateMachine(() -> {
                        return handler;
                    })// No código exemplo original em vez do lambda aqui viria Servidor::new, mas já criamos o servidor lá em cima, estou recuperando ele
                    .withTransport(NettyTransport.builder()
                            .withThreads(4)
                            .build())
                    .withStorage(Storage.builder()
                            .withDirectory(new File("LOG_" + args[0] + "_" + args[1])) //Must be unique
                            .withStorageLevel(StorageLevel.DISK)
                            .build());
            copycatServer = builder.build();

            Thread t1 = new Thread() {
                @Override
                public void run() {
                    if (Boolean.parseBoolean(args[3])) {
                        copycatServer.bootstrap().join();
                    } else {
                        copycatServer.join(addresses).join();
                    }
                }
            };

            Thread t2 = new Thread() {
                @Override
                public void run() {  
                    
                    // Ativando servidor do Thrift, tem que ser última instrução pois após ele nada mais é executado
                    thriftServer.serve();
                }
            };

            t1.start();
            t2.start();

            t1.join();
            t2.join();

        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            System.out.println("Erro nos parâmetros da linha de comando.");
        } catch (TException ex) {
            System.out.println("O servidor não pôde ser iniciado.");
        } catch (InterruptedException ex) {
            System.out.println("O servidor foi interrompido.");
        }
    }
}
