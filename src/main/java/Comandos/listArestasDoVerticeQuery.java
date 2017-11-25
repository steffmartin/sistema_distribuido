/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Comandos;

import Grafo.Aresta;
import io.atomix.copycat.Query;
import java.util.List;

/**
 *
 * @author MarceloPrado
 */
public class listArestasDoVerticeQuery implements Query<List<Aresta>>{
    public int nome, endId;
    
    public listArestasDoVerticeQuery(int nome, int endId){
        this.nome = nome;
        this.endId = endId;
    }
}
