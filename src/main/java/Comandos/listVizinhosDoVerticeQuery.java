/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Comandos;

import Grafo.Vertice;
import io.atomix.copycat.Query;
import java.util.List;

/**
 *
 * @author MarceloPrado
 */
public class listVizinhosDoVerticeQuery implements Query<List<Vertice>>{
    
    public int nome;
    
    public listVizinhosDoVerticeQuery(int nome){
        this.nome = nome;
    }
}
