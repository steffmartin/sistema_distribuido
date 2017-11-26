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
public class deleteArestasDoVerticeCommand implements Command<Void> {

    int nome, endId;

    public deleteArestasDoVerticeCommand(int nome, int endId) {
        this.nome = nome;
        this.endId = endId;
    }

}
