/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Comandos;

import io.atomix.copycat.Command;
import Grafo.Aresta;

/**
 *
 * @author steff
 */
public class updateArestaCommand implements Command<Boolean> {

    public Aresta a;

    public updateArestaCommand(Aresta a) {
        this.a = a;
    }
}
