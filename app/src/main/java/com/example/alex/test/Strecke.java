package com.example.alex.test;

import java.util.LinkedList;

/**
 * Created by Blackilli on 17.12.2017.
 */

public class Strecke {
    public int Id;
    public String Bezeichnung;
    public LinkedList<Wegpunkt> wegpunkte;

    public Strecke(int id, String bezeichnung){
        this.Id = id;
        this.Bezeichnung = bezeichnung;
        this.wegpunkte = new LinkedList<>();
    }

    @Override
    public String toString() {
        return Bezeichnung;
    }
}
