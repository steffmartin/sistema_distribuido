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
public class listArestasDoGrafoQuery implements Query<List<Aresta>>{
    public int endId;
    
    public listArestasDoGrafoQuery(int endId){
        this.endId = endId;
    }
}
