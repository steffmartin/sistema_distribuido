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
    public static TServer thriftServer;
    public static List<Address> members = new LinkedList<>();
    public static CopycatServer cluster;

    public static void main(String[] args) {

        try {
            System.out.println("Ativando servidores...");

            //Parte do Thrift
            handler = new Servidor(args);
            thriftServer = new TThreadPoolServer(
                    new TThreadPoolServer.Args(new TServerSocket(Integer.parseInt(args[1])))
                            .processor(new Handler.Processor(handler)));

            //Parte do Raft
            members.add(new Address(args[0], Integer.parseInt(args[2])));
            members.add(new Address(args[4], Integer.parseInt(args[6])));
            members.add(new Address(args[7], Integer.parseInt(args[9])));
            cluster = CopycatServer.builder(members.get(0))
                    .withStateMachine(() -> {
                        return handler;
                    })
                    .withTransport(new NettyTransport())
                    .withStorage(Storage.builder()
                            .withDirectory(new File("LOG_" + args[0] + "_" + args[2]))
                            .withStorageLevel(StorageLevel.DISK)
                            .build()).build();

            Thread t1 = new Thread() {
                @Override
                public void run() {
                    System.out.println("Servidor RAFT ativo em " + args[0] + "/" + args[2] + ". Líder: " + args[3] + ".");
                    if (Boolean.parseBoolean(args[3])) {
                        cluster.bootstrap().join();
                    } else {
                        cluster.join(members).join();
                    }
                }
            };

            Thread t2 = new Thread() {
                @Override
                public void run() {
                    try {
                        System.out.println("Servidor THRIFT ativo em " + args[0] + "/" + args[1] + " com o ID " + handler.getServerId() + ".");
                    } catch (TException ex) {
                        System.out.println("Servidor THRIFT não pôde ser iniciado.");
                    }
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
