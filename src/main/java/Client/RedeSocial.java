/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Grafo.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
public class RedeSocial {

    public static final int MAX = 3; // Número máximo de tentativas de conexão antes de dar erro de conexão
    public static final float inf = 9999;
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
            int numeroUsuario = 0; 
            int numeroPost = 0; 
            
            System.out.println("Bem vindo ao FaceBroklyn!!");
            //Menu principal
            while (true) {
                try {
                    i.printLn(
                            "\nOperação:\n\n"
                            + " 1) Criar perfil\n"
                            + " 2) Buscar perfil\n"
                            + " 3) Atualizar perfil\n"
                            + " 4) Remover perfil\n"
                            + " 5) Solicitar amizade\n"
                            + " 6) Aceitar amizade\n"
                            + " 7) Remover amigo\n"
                            + " 8) Postar texto\n"
                            + " 9) Reagir a post\n"
                            + "10) Remover post\n"
                            + "11) Ver solicitações de amizade\n"
                            + "12) Ver amigos\n"
                            + "13) Ver posts\n"
                            + "14) Ver reações de um post\n"
                            + "15) Ver sugestões de amizade\n"
                            + "16) Sair do FaceBroklyn");
                    opcao = l.lerOpcao(1, 16);

                    switch (opcao) {
                        case 1: {//1) Criar perfil                            
                            nome = numeroUsuario++;
                            try{
                                Vertice vt;
                                while(true){
                                    vt = conectar(args).readVertice(nome);
                                    nome = numeroUsuario++;
                                }
                            }
                            catch(NullException ex){                                
                            }
                                    
                            cor = 1;
                            i.print("Apelido: ");
                            desc = l.lerTexto();                            
                            peso = 1;

                            Vertice v = new Vertice(nome, cor, desc, peso);
                            if (conectar(args).createVertice(v)) {
                                i.printLn("\nO perfil '" + desc + "' foi criado com sucesso.");
                                i.printLn("\nSeu número de usuário é: " + nome);
                            } else {
                                i.printLn("\nO perfil '" + desc + "' não foi criado.");
                            }
                            break;
                        }
                        case 2: {//2) Buscar perfil
                            i.print("Número de usuário: ");
                            nome = l.lerInteiro();

                            try {
                                Vertice v = conectar(args).readVertice(nome);
                                if(v.cor != 1)
                                    throw new NullException();
                                i.printLn("Apelido: " + v.desc);                                
                            } catch (NullException ex) {
                                i.printLn("Perfil não encontrado!\n");
                            }
                            break;
                        }
                        case 3: {//3) Atualizar perfil
                            i.print("Número de usuário: ");
                            nome = l.lerInteiro();                            
                            cor = 1;
                            i.print("Apelido: ");
                            desc = l.lerTexto();                            
                            peso = 1;

                            Vertice v = new Vertice(nome, cor, desc, peso);
                            if(v.cor != 1)
                                    throw new NullException();
                            if (conectar(args).updateVertice(v)) {
                                i.printLn("\nO usuário '" + nome + "' foi atualizado com sucesso. Novo apelido: " + desc);
                            } else {
                                i.printLn("\nO usuário '" + nome + "' não existe.");
                            }
                            break;
                        }
                        case 4: {//4) Remover perfil
                            i.print("Número de usuário: ");
                            nome = l.lerInteiro();
                            
                            try {
                                Vertice v = conectar(args).readVertice(nome);
                                if(v.cor != 1)
                                    throw new NullException();
                                
                                if (conectar(args).deleteVertice(nome)) {
                                    i.printLn("\nO usuário '" + nome + "'(" + v.desc + ")" + " foi excluído com sucesso.");
                                } else {
                                    i.printLn("\nPerfil não encontrado");
                                }
                            } catch (NullException ex) {
                                i.printLn("Perfil não encontrado!\n");
                            }                        
                            
                            break;
                        }
                        case 5: {//5) Solicitar amizade
                            i.print("Perfil solicitando amizade: ");
                            nome = l.lerInteiro();
                            i.print("Perfil solicitado: ");
                            nomeB = l.lerInteiro();                            
                            peso = inf;                            
                            direc = true;
                            i.print("Deixe uma mensagem para essa pessoa: ");
                            desc = l.lerTexto();
                            if(desc.equals(""))
                                desc = "Solicitação de amizade entre usuario '" + nome + "' e usuário '" + nomeB + "'";
                            
                            try {
                                Vertice v = conectar(args).readVertice(nome);
                                if(v.cor != 1)
                                    throw new NullException();
                                
                                try {
                                    Vertice v1 = conectar(args).readVertice(nomeB);
                                    if(v1.cor != 1)
                                        throw new NullException();
                                    
                                    Aresta a = new Aresta(nome, nomeB, peso, direc, desc);
                                    if (conectar(args).createAresta(a)) {
                                        i.printLn("\nSolicitação de amizade entre '" + nome + "' e '" + nomeB + "' enviada com sucesso.");
                                    } else {
                                        i.printLn("\nImpossível enviar solicitação de amizade entre '" + nome + "' e '" + nomeB + "'.");
                                    }
                                   
                                } catch (NullException ex) {
                                    i.printLn("Perfil " + nomeB + " não encontrado!\n");
                                }
                            } catch (NullException ex) {
                                i.printLn("Perfil " + nome + " não encontrado!\n");
                            }

                            break;
                        }
                        case 6: {//6) Aceitar amizade
                            i.print("Número de usuário: ");
                            nome = l.lerInteiro();                           
                            i.printLn("Solicitações pendentes:");
                            
                            try{
                                Vertice v = conectar(args).readVertice(nome);
                                if(v.cor != 1)
                                    throw new NullException();
                                
                                try{
                                    List<Aresta> pedidos = conectar(args).listArestasDoVertice(nome);
                                    List<Integer> usuariosSolicitando = new ArrayList<>();
                                    for(Aresta a : pedidos){
                                        if(a.vertice2 == nome && a.isDirec()){
                                            i.printLn("Solicitação de '" + a.vertice1 +"': " + a.desc);
                                            usuariosSolicitando.add(a.vertice1);
                                        }
                                    }
                                    if(usuariosSolicitando.isEmpty())
                                        throw new NullException();
                                    
                                    i.print("Informe o usuário a ser adicionado ou -1 para cancelar a operação: ");
                                    nomeB = l.lerInteiro();
                                    
                                    if(nomeB != -1){
                                        if(usuariosSolicitando.contains(nomeB)){
                                            if(conectar(args).deleteAresta(nomeB, nome)){
                                                Aresta a = new Aresta(nome, nomeB, 1, false, "Amizade entre '" + nome + "' e '" + nomeB + "'");
                                                if(conectar(args).createAresta(a))
                                                    i.printLn("Amigo adicionado com sucesso!");
                                                else
                                                    i.printLn("Ocorreu um erro ao aceitar a solicitação de amizade.");
                                            }
                                            else
                                                i.printLn("Ocorreu um erro ao aceitar a solicitação de amizade.");
                                        }
                                        else{
                                            i.printLn("Informe um nome de usuário válido.");
                                        }
                                    }
                                    else{
                                        i.printLn("Operação cancelada com sucesso!\n");
                                    }
                                }
                                catch(NullException ex){
                                    i.printLn("Perfil " + nome + " não possui nenhuma solicitação de amizade pendente!\n");
                                }                                                         
                            }
                            catch(NullException ex){
                                i.printLn("Erro ao exibir solicitações pendentes: Perfil " + nome + " não encontrado!\n");
                            }
                            
                            break;
                        }
                        case 7: {//7) Remover amigo
                            i.print("Número de usuário: ");
                            nome = l.lerInteiro();
                            i.print("Remover amigo: ");
                            nomeB = l.lerInteiro();
                           
                            try {
                                Aresta a = conectar(args).readAresta(nome, nomeB);
                                if(a.direc)
                                    throw new NullException();
                                    
                                if (conectar(args).deleteAresta(nome, nomeB)) {
                                    i.printLn("\nAmigo removido com sucesso!");
                                } else {
                                    i.printLn("\nOcorreu um erro ao desfazer amizade.");
                                }
                            } catch (NullException e) {
                                i.printLn("\nO usuário '" + nome + " não possui amizade com o usuário " + nomeB + "'.");
                            }
                            break;
                        }
                        case 8: {//8) Postar texto
                            i.print("Número de usuário: ");
                            nome = l.lerInteiro();
                            i.print("Texto do post: ");
                            desc = l.lerTexto();
                            peso = 1;
                            cor = 2;
                            
                            nomeB = numeroPost++;
                            try{
                                Vertice vt;
                                while(true){
                                    vt = conectar(args).readVertice(nomeB);
                                    nomeB = numeroPost++;
                                }
                            }
                            catch(NullException ex){                                
                            }
                                                       
                            Vertice v = new Vertice(nomeB, cor, desc, peso);
                            
                            if (conectar(args).createVertice(v)) {
                                Aresta a = new Aresta(nome, nomeB, 1, true, "Post");
                                
                                if(conectar(args).createAresta(a)){
                                    i.printLn("\nPost realizado com sucesso:\n" + desc);
                                    i.printLn("O ID do post é: " + nomeB);
                                }
                                else{
                                    i.printLn("\nOcorreu um erro ao realizar o post.");
                                }
                                    
                            } else {
                                i.printLn("\nOcorreu um erro ao armazenar o post.");
                            }
                            break;
                        }
                        case 9: {//9) Reagir a post
                            i.print("Número de usuário: ");
                            nome = l.lerInteiro();
                            
                            i.print("Listando posts dos seus amigos...\n");
                            try{
                                Vertice v = conectar(args).readVertice(nome);
                                if(v.cor != 1)
                                    throw new NullException();
                                
                                try{
                                    List<Aresta> relacoes = conectar(args).listArestasDoVertice(nome);
                                    List<Integer> amigos = new ArrayList<>();
                                    List<Vertice> posts = new ArrayList<>();
                                    List<Vertice> amigosPostaram = new ArrayList<>();
                                    
                                    for(Aresta a : relacoes){
                                        Vertice v1 = conectar(args).readVertice(a.vertice1);
                                        Vertice v2 = conectar(args).readVertice(a.vertice2);
                                        if(!a.direc && a.peso == 1){
                                            amigos.add(a.vertice1 == nome ? v2.nome : v1.nome);
                                        }
                                    }
                                    if(amigos.isEmpty())
                                        throw new NullException();
                                    
                                    for(Integer amigo : amigos){                                        
                                        List<Aresta> relacoesAmigo = conectar(args).listArestasDoVertice(amigo);
                                        
                                        for(Aresta a : relacoesAmigo){
                                            Vertice usuario = conectar(args).readVertice(a.vertice1);
                                            Vertice post = conectar(args).readVertice(a.vertice2);
                                            if(a.vertice1 == amigo && a.direc && post.cor == 2){
                                                posts.add(post);
                                                amigosPostaram.add(usuario);
                                            }
                                        }                                        
                                    }
                                    ArrayList<Integer> idPosts = new ArrayList<>();
                                    for(Vertice post : posts){
                                        i.printLn("Usuario: " + amigosPostaram.get(posts.indexOf(post)).desc + " - ID Post: " + post.nome + "\n " + post.desc);
                                        idPosts.add(post.nome);
                                    }
                                    if(idPosts.isEmpty())
                                        throw new NullException();
                                    
                                    i.print("Informe o post que deseja reagir: ");
                                    nomeB = l.lerInteiro();
                                    
                                    Aresta a;
                                    boolean atualizar;
                                    try{
                                        a = conectar(args).readAresta(nome, nomeB);
                                        atualizar = true;
                                    }
                                    catch(NullException ex){
                                        a = new Aresta(nome, nomeB, 1, true, "Curtida");
                                        atualizar = false;
                                    }
                                    
                                    if(idPosts.contains(nomeB)){
                                        int op1 = -1;
                                        while(op1 == -1){
                                            i.print("\nReação:\n"
                                            + " 1) Normal\n"
                                            + " 2) Amei\n"
                                            + " 3) Odiei\n"
                                            + " 4) Engraçado\n"
                                            + " 5) Triste\n"
                                            + " 6) Remover reação\n");
                                            op1 = l.lerOpcao(1, 6);
                                        }
                                        switch(op1){
                                            case 1:
                                                a.setPeso(2);
                                                a.setDesc("Reação normal");
                                                break;
                                            
                                            case 2:
                                                a.setPeso(3);
                                                a.setDesc("Reação amei");
                                                break;
                                            
                                            case 3:
                                                a.setPeso(4);
                                                a.setDesc("Reação odiei");
                                                break;
                                            
                                            case 4:
                                                a.setPeso(5);
                                                a.setDesc("Reação engraçada");
                                                break;
                                            
                                            case 5:
                                                a.setPeso(6);
                                                a.setDesc("Reação triste");
                                                break;
                                        }
                                        if(op1 != 6){
                                            if(!atualizar){
                                                if(conectar(args).createAresta(a)){
                                                    i.printLn("Reação realizada com sucesso!");
                                                }
                                                else{
                                                    i.printLn("Não foi possível reagir a esse post!");
                                                }
                                            }
                                            else{
                                                if(conectar(args).updateAresta(a)){
                                                    i.printLn("Reação atualizada com sucesso!");
                                                }
                                                else{
                                                    i.printLn("Não foi possível atualizar a reação desse post!");
                                                }
                                            }
                                        }
                                        else{
                                            if(conectar(args).deleteAresta(nome, nomeB)){
                                                i.printLn("Reação desfeita com sucesso!");
                                            }
                                            else{
                                                i.printLn("Não foi possível remover a reação deste post!");
                                            }
                                        }
                                    }
                                    else{
                                        i.printLn("Informe o ID de um post válido.");
                                    }
                                }
                                catch(NullException ex){
                                    i.printLn("Sem posts para exibir no momento!\n");
                                }                                                         
                            }
                            catch(NullException ex){
                                i.printLn("Erro ao exibir posts de amigos: Perfil " + nome + " não encontrado!\n");
                            }
                            break;
                        }
                        case 10: {//10) Remover post
                            i.print("ID do post: ");
                            nome = l.lerInteiro();
                            
                            try{
                                Vertice v = conectar(args).readVertice(nome);                                
                                
                                if(conectar(args).deleteVertice(nome))
                                    i.print("Post removido com sucesso!");
                                else
                                    i.print("Falha ao remover post!");
                            }
                            catch(NullException ex){
                                i.print("O post com ID '" + nome + "' não existe!");
                            }
                            break;
                        }
                        case 11: {//11) Ver solicitações de amizade
                            i.print("Número de usuário: ");
                            nome = l.lerInteiro();                           
                            i.printLn("Solicitações pendentes:");
                            
                            try{
                                Vertice v = conectar(args).readVertice(nome);
                                if(v.cor != 1)
                                    throw new NullException();
                                
                                try{
                                    List<Aresta> pedidos = conectar(args).listArestasDoVertice(nome);
                                    List<Integer> usuariosSolicitando = new ArrayList<>();
                                    for(Aresta a : pedidos){
                                        if(a.vertice2 == nome && a.isDirec()){
                                            i.printLn("Solicitação de '" + a.vertice1 +"': " + a.desc);
                                            usuariosSolicitando.add(a.vertice1);
                                        }
                                    }
                                    if(usuariosSolicitando.isEmpty())
                                        throw new NullException();
                                }
                                catch(NullException ex){
                                    i.printLn("Perfil " + nome + " não possui nenhuma solicitação de amizade pendente!\n");
                                }                                                         
                            }
                            catch(NullException ex){
                                i.printLn("Erro ao exibir solicitações pendentes: Perfil " + nome + " não encontrado!\n");
                            }
                            break;
                        }
                        case 12: {//12) Ver amigos
                            i.print("Número de usuário: ");
                            nome = l.lerInteiro();                           
                            i.printLn("Lista de amigos:");
                            
                            try{
                                Vertice v = conectar(args).readVertice(nome);
                                if(v.cor != 1)
                                    throw new NullException();
                                
                                try{
                                    List<Aresta> pedidos = conectar(args).listArestasDoVertice(nome);
                                    List<Integer> amigos = new ArrayList<>();
                                    for(Aresta a : pedidos){
                                        Vertice v1 = conectar(args).readVertice(a.vertice1);
                                        Vertice v2 = conectar(args).readVertice(a.vertice2);
                                        if(!a.direc && a.peso == 1){
                                            i.printLn(a.vertice1 == nome ? v2.desc : v1.desc);
                                            amigos.add(a.vertice1 == nome ? v2.nome : v1.nome);
                                        }
                                    }
                                    if(amigos.isEmpty())
                                        throw new NullException();
                                }
                                catch(NullException ex){
                                    i.printLn("Perfil " + nome + " não possui nenhum amigo ainda!\n");
                                }                                                         
                            }
                            catch(NullException ex){
                                i.printLn("Erro ao exibir lista de amigos: Perfil " + nome + " não encontrado!\n");
                            }
                            break;
                        }
                        case 13: {//13) Ver posts
                            i.print("Número de usuário (Informe -1 para ver todos os posts): ");
                            nome = l.lerInteiro();

                            try{
                                Vertice v = conectar(args).readVertice(nome);
                                if(v.cor != 1)
                                    throw new NullException();
                                
                                try{
                                    List<Aresta> relacoes = conectar(args).listArestasDoVertice(nome);                                    
                                    List<Vertice> posts = new ArrayList<>();
                                    
                                    for(Aresta a : relacoes){
                                        Vertice v2 = conectar(args).readVertice(a.vertice2);
                                        if(a.direc && v2.cor == 2 && a.peso == 1){
                                            posts.add(v2);
                                        }
                                    }
                                    if(posts.isEmpty())
                                        throw new NullException();                                    
                                    
                                    for(Vertice post : posts){
                                        i.printLn("ID Post: " + post.nome + "\n " + post.desc);
                                    }
                                }
                                catch(NullException ex){
                                    i.printLn("Sem posts para exibir no momento!\n");
                                }                                                         
                            }
                            catch(NullException ex){
                                if(nome != -1)
                                    i.printLn("Erro ao exibir posts: Perfil " + nome + " não encontrado!\n");
                                else{
                                    try{
                                        List<Aresta> relacoes = conectar(args).listArestasDoGrafo();
                                        List<Vertice> posts = new ArrayList<>();
                                        List<Vertice> usuariosPostaram = new ArrayList<>();

                                        for(Aresta a : relacoes){
                                            Vertice v1 = conectar(args).readVertice(a.vertice1);
                                            Vertice v2 = conectar(args).readVertice(a.vertice2);
                                            if(a.direc && v2.cor == 2 && a.peso == 1){
                                                usuariosPostaram.add(v1);
                                                posts.add(v2);
                                            }
                                        }
                                        if(posts.isEmpty())
                                            throw new NullException();                                        

                                        for(Vertice post : posts){
                                            i.printLn("Usuario: " + usuariosPostaram.get(posts.indexOf(post)).desc + " - ID Post: " + post.nome + "\n " + post.desc);
                                        }
                                    }
                                    catch(NullException ex1){
                                        i.printLn("Sem posts para exibir no momento!\n");
                                    }     
                                }
                            }

                            break;
                        }
                        case 14: {//14) Ver reações de um post
                            i.print("ID do post: ");
                            nome = l.lerInteiro();
                                                        
                            List<Aresta> reacoes = conectar(args).listArestasDoVertice(nome);
                            
                            ArrayList<Integer> qtdsReacoes = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0));
                            for(Aresta reacao : reacoes){                                
                                if(reacao.peso == 2)
                                    qtdsReacoes.set(0, qtdsReacoes.get(0) + 1);
                                if(reacao.peso == 3)
                                    qtdsReacoes.set(1, qtdsReacoes.get(1) + 1);
                                if(reacao.peso == 4)
                                    qtdsReacoes.set(2, qtdsReacoes.get(2) + 1);
                                if(reacao.peso == 5)
                                    qtdsReacoes.set(3, qtdsReacoes.get(3) + 1);  
                                if(reacao.peso == 6)
                                    qtdsReacoes.set(4, qtdsReacoes.get(4) + 1);                                
                            }
                            String tiposReacao[] = {"Normal","Amei","Odiei","Engraçado","Triste"};
                            for(int ii = 0; ii < qtdsReacoes.size(); ii++){
                                if(qtdsReacoes.get(ii) != 0){
                                    i.printLn(tiposReacao[ii] + ": " + qtdsReacoes.get(ii));
                                }
                            }
                            
                            break;
                        }
                        case 15: {//15) Ver sugestões de amizade
                            i.print("Nome de usuário: ");
                            nome = l.lerInteiro();
                            
                            try{
                                Vertice v = conectar(args).readVertice(nome);
                                if(v.cor != 1)
                                    throw new NullException();
                                
                                i.printLn("\nPrincipais sugestões...");
                                try{
                                    List<Vertice> todosUsuarios = conectar(args).listVerticesDoGrafo();
                                    List<Integer> amigos = new ArrayList<>();
                                    
                                    for(Vertice v1: conectar(args).listVizinhosDoVertice(nome)){
                                        if(!amigos.contains(v1.nome))
                                            amigos.add(v1.nome);
                                    }
                                    amigos.add(nome);
                                    
                                    ArrayList<Vertice> remover = new ArrayList<>();
                                    for(Vertice v1: todosUsuarios){
                                        if(amigos.contains(v1.nome)){
                                            remover.add(v1);
                                        }
                                    }
                                    for(Vertice ii:remover)
                                        todosUsuarios.remove(ii);
                                    
                                    List<List<Vertice>> sugestoes = new ArrayList<>();
                                    
                                    for(Vertice v1 : todosUsuarios){
                                        try{
                                            List<Vertice> caminho = conectar(args).listMenorCaminho(nome, v1.nome);
                                            if(!caminho.isEmpty()){                                                
                                                sugestoes.add(caminho);
                                            }
                                        }
                                        catch(NullException ex1){
                                            
                                        }                                        
                                    }

                                    Collections.sort(sugestoes,
                                        new Comparator<List<Vertice>>(){
                                            @Override
                                            public int compare(List<Vertice> e1, List<Vertice> e2 ){
                                                if(e1.size() > e2.size())
                                                    return 1;
                                                if(e1.size() < e2.size())
                                                    return -1;
                                                return 0;
                                            }
                                        }
                                    ); 
                                    
                                    for(List<Vertice> sugestao : sugestoes){
                                        Vertice aux = sugestao.get(sugestao.size() - 1);
                                        i.printLn("Distância: " + (sugestao.size() - 1) + ", Usuário: '" + aux.nome + "' (" + aux.desc + ");");                                        
                                    }
                                    
                                    if(sugestoes.isEmpty()){
                                        i.printLn("Usuário '" + nome + "' não possui nenhuma sugestão próxima. Listando possibilidades...");
                                        for(Vertice v2:todosUsuarios){
                                            i.printLn("Usuário: " + v2.nome + ", Perfil: " + v2.desc);
                                        }
                                    }
                                }
                                catch(NullException ex){
                                    i.printLn("Ocorreu uma falha ao listar sugestões de amizade.");
                                }
                            }
                            catch(NullException ex){
                                i.print("Perfil não encontrado!");
                            }                            
                            break;
                        }
                        case 16: {//16) Sair do FaceBroklyn
                            i.printLn("Obrigado por utilizar o FaceBroklyn. Visite-nos mais vezes!!");
                            l.close();
                            System.exit(0);
                            break;
                        }
                    }
                } catch (TTransportException ex) {
                    if (ex.getMessage().equals("MAX_atingido")) {
                        System.out.println("Número máximo de tentativas de conexão atingido, operação cancelada.");
                    } else {
                        throw ex;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            System.out.println("Erro nos parâmetros da linha de comando, o sistema não será iniciado. Mensagem de erro: " + ex);
        } catch (TTransportException ex) {
            System.out.println("Houve um erro de comunicação com o servidor, o sistema será finalizado. Mensagem de erro: " + ex);
            ex.printStackTrace();
        } catch (TException ex) {
            System.out.println("Houve um erro inesperado ao executar esta operação, o sistema será finalizado. Mensagem de erro: " + ex);
            ex.printStackTrace();
        }
    }

    // Este método retorna uma conexão ativa para o cliente
    public static Handler.Client conectar(String[] servers) throws ArrayIndexOutOfBoundsException, NumberFormatException, TTransportException, TException {

        int counter = 0;
        TTransport transport;
        TProtocol protocol;
        Handler.Client client = null;

        while (client == null && counter < MAX) {
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
            counter++;
        }
        if (client == null) {
            throw new TTransportException("MAX_atingido");
        } else {
            return client;
        }
    }
}