package com.example.alex.test;

import android.location.Location;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

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

    public LinkedList<LinkedList<Sensorwert>> interpolierteWerte(){
        LinkedList<LinkedList<Sensorwert>> tmp = new LinkedList<>();
        Sensorwert start, end;
        for (int i = 0; i < aufgezeichneteWerte.size(); i++) {
            tmp.add(new LinkedList<>());
            start = aufgezeichneteWerte.get(i).getFirst();
            end = aufgezeichneteWerte.get(i).getLast();
            double latdif = end.getValues()[0] - start.getValues()[0];
            double londif = end.getValues()[1] - start.getValues()[1];
            long timedif = end.getTimestamp() - start.getTimestamp();
            for (int j = 0; j < aufgezeichneteWerte.get(i).size(); j++)
            {
                long timestamp = aufgezeichneteWerte.get(i).get(j).getTimestamp();
                double mp = 1 / timedif * (timestamp - start.getTimestamp());
                double[] values = new double[]{start.getValues()[0] + latdif * mp, start.getValues()[1] + londif * mp, 0.0};
                tmp.get(i).add(new Sensorwert(timestamp, values));
            }
        }
        return tmp;
    }

    public LinkedList<LinkedList<Float>> aufgezeichneteWerteDif(){
        LinkedList<LinkedList<Float>> tmp = new LinkedList<>();
        LinkedList<LinkedList<Sensorwert>> interpolierteWerte = interpolierteWerte();
        for (int i = 0; i < aufgezeichneteWerte.size(); i++) {
            tmp.add(new LinkedList<>());
            for (int j = 0; j < aufgezeichneteWerte.get(i).size(); j++){
                float[] results = new float[6];
                double startlat, startlon, endlat, endlon;
                startlat = interpolierteWerte.get(i).get(j).getValues()[0];
                startlon = interpolierteWerte.get(i).get(j).getValues()[1];
                endlat = aufgezeichneteWerte.get(i).get(j).getValues()[0];
                endlon = aufgezeichneteWerte.get(i).get(j).getValues()[1];
                Location.distanceBetween(startlat, startlon, endlat, endlon, results);
                tmp.get(i).set(j, results[0]);
            }
        }
        return tmp;
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
