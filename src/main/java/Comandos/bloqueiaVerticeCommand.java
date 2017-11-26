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
public class bloqueiaVerticeCommand implements Command<Boolean> {

    int nome;

    public bloqueiaVerticeCommand(int nome) {
        this.nome = nome;
    }

}
