/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Comandos;

import io.atomix.copycat.Command;
import Grafo.Vertice;

/**
 *
 * @author steff
 */
public class createVerticeCommand implements Command<Boolean> {

    public Vertice v;

    public createVerticeCommand(Vertice v) {
        this.v = v;
    }
}
