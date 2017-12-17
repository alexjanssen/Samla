package com.example.alex.test;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Blackilli on 17.12.2017.
 */

public class Gpssession {

    public String bezeichnung;
    public long startzeit, endzeit;
    public int sensor;

    public Gpssession(String bezeichnung, long startzeit, int sensor){
        this.bezeichnung = bezeichnung;
        this.startzeit = startzeit;
        this.sensor = sensor;
        this.endzeit = Long.MAX_VALUE;
    }

    public void sendSession(){
        Sensorupdate sensorupdate = Sensorupdate.getInstance();
        RequestQueue requestQueue = sensorupdate.requestQueue;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Bezeichnung", bezeichnung);
            jsonObject.put("Startzeit", startzeit);
            jsonObject.put("Endzeit", endzeit);
            jsonObject.put("Sensor", sensor);
            jsonObject.put("User", sensorupdate.android_id);
        } catch (JSONException e) {
            logError(e);
        }
        requestQueue.add(new JsonObjectRequest(Request.Method.POST, "http://blackilli.de/gpssession", jsonObject, response -> {}, error -> logError(error)));
    }

    private void logError(Exception ex){
        Log.e("GpsSessionSpeichern", "Fehler Beim übertragen des GpsSession Objekts", ex);
    }
}
