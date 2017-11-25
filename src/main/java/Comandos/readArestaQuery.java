/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Comandos;

import io.atomix.copycat.Query;
import Grafo.Aresta;

/**
 *
 * @author steff
 */
public class readArestaQuery implements Query<Aresta> {

    public int nome1, nome2;

    public readArestaQuery(int nome1, int nome2) {
        this.nome1 = nome1;
        this.nome2 = nome2;
    }
}
