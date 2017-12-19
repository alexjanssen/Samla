package com.example.alex.test;

import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
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
            //eigentlich müsste man auf nummer sicher gehen und hier nach timestamp sorten. Das niedrige API level lässt dies aber leider nicht so einfach zu
            //aufgezeichneteWerte.get(i).sort((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));
            if (aufgezeichneteWerte.get(i).size() == 0)
                continue;
            Log.e("Werte Interpolieren", "aufgezeichneteWerte.get(" + i + ").size(): " + aufgezeichneteWerte.get(i).size());

            start = aufgezeichneteWerte.get(i).getFirst();
            end = aufgezeichneteWerte.get(i).getLast();
            double latdif = wegpunkte.get(i+1).Latitude - wegpunkte.get(i).Latitude;
            Log.e("Interpolieren", "Latdif: " + latdif);
            double londif = wegpunkte.get(i+1).Longitude - wegpunkte.get(i).Longitude;
            Log.e("Interpolieren", "Londif: " + londif);
            long timedif = end.getTimestamp() - start.getTimestamp();
            Log.e("Interpolieren", "Timedif: " + timedif);
            for (int j = 0; j < aufgezeichneteWerte.get(i).size(); j++)
            {
                Log.e("Interpolieren", "Timestamp von aufgezeichneteWerte.get(" + i + ").get(" + j + "): " + aufgezeichneteWerte.get(i).get(j).getTimestamp());
                long timestamp = aufgezeichneteWerte.get(i).get(j).getTimestamp();
                double mp = 1 / (double)timedif * ((double)timestamp - (double)start.getTimestamp());
                Log.e("Interpolieren", "MP: " + mp);
                double[] values = new double[]{wegpunkte.get(i).Latitude + latdif * mp, wegpunkte.get(i).Longitude + londif * mp, 0.0};
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
                tmp.get(i).add(results[0]);
            }
        }
        return tmp;
    }

    public LinkedList<Float> getAllDiffs(){
        LinkedList<Float> tmp = new LinkedList<Float>();
        LinkedList<LinkedList<Float>> aufgezeichneteWerteDif = aufgezeichneteWerteDif();
        for (int i = 0; i < aufgezeichneteWerteDif.size(); i++)
            for (int j = 0; j < aufgezeichneteWerteDif.get(i).size(); j++)
                tmp.add(aufgezeichneteWerteDif.get(i).get(j));



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tmp.sort((a, b) -> Float.compare(a,b));
        }  else Collections.sort(tmp);

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
