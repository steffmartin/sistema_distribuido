/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Comandos;

import io.atomix.copycat.Query;
import Grafo.Vertice;

/**
 *
 * @author steff
 */
public class readVerticeQuery implements Query<Vertice> {

    public Vertice v;

    readVerticeQuery(Vertice v) {
        this.v = v;
    }
}
