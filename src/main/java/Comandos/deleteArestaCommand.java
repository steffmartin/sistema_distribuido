/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Comandos;

import io.atomix.copycat.Command;

/**
 *
 * @author steff
 */
public class deleteArestaCommand implements Command<Boolean> {

    public int nome, nome2;

    deleteArestaCommand(int nome, int nome2) {
        this.nome = nome;
        this.nome2 = nome2;
    }

}
