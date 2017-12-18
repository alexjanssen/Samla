package com.example.alex.test;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by Blackilli on 17.12.2017.
 */

public class Strecke {
    public int Id;
    public String Bezeichnung;
    public LinkedList<Wegpunkt> wegpunkte;
    public LinkedList<LinkedList<Sensorwert>> aufgezeichneteWerte;

    public Strecke(int id, String bezeichnung){
        this.Id = id;
        this.Bezeichnung = bezeichnung;
        this.wegpunkte = new LinkedList<>();
        this.aufgezeichneteWerte = new LinkedList<>();
        for (Wegpunkt w : wegpunkte)
            aufgezeichneteWerte.add(new LinkedList<>());
    }

    @Override
    public String toString() {
        return Bezeichnung;
    }

    public void addWegpunkt(Wegpunkt w){
        wegpunkte.add(w);
        aufgezeichneteWerte.add(new LinkedList<>());
    }

    public void saveStrecke(String bezeichnung){
        Sensorupdate sensorupdate = Sensorupdate.getInstance();
        RequestQueue requestQueue = sensorupdate.requestQueue;

        JSONObject jsonStrecke = new JSONObject();
        try {
            jsonStrecke.put("Streckenid", Id);
            jsonStrecke.put("Sessionbezeichnung", bezeichnung);
            jsonStrecke.put("User", sensorupdate.android_id);
            jsonStrecke.put("Sensor", sensorupdate.TYPE_GPS);
            JSONArray jsonTeilstrecken = new JSONArray();
            for (int i = 0; i < aufgezeichneteWerte.size(); i++){
                if (aufgezeichneteWerte.get(i).size() != 0) {
                    JSONObject jsonTeilstrecke = new JSONObject();
                    jsonTeilstrecke.put("Startposition", i + 1);
                    jsonTeilstrecke.put("Endposition", i + 2);
                    jsonTeilstrecke.put("Startzeit", aufgezeichneteWerte.get(i).getFirst().getTimestamp());
                    jsonTeilstrecke.put("Endzeit", aufgezeichneteWerte.get(i).getLast().getTimestamp());
                    jsonTeilstrecken.put(jsonTeilstrecke);
                }
            }
            jsonStrecke.put("Teilstrecken", jsonTeilstrecken);

            requestQueue.add(new JsonObjectRequest(Request.Method.POST, "http://blackilli.de/gpssession", jsonStrecke, response -> {}, error -> {}));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (LinkedList list : aufgezeichneteWerte)
            list.clear();
    }

    public int countAufgezeichneteWerte(){
        int count = 0;
        for (LinkedList list : aufgezeichneteWerte)
            count += list.size();
        return count;
    }
}
