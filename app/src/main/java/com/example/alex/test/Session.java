package com.example.alex.test;

/**
 * Created by Blackilli on 17.12.2017.
 */

public class Session {
    public String bezeichnung;
    public int sessionid;

    public Session(int sessionid, String bezeichnung){
        this.sessionid = sessionid;
        this.bezeichnung = bezeichnung;
    }

    @Override
    public String toString() {
        return "Nr. " + sessionid + ": " + bezeichnung;
    }
}
