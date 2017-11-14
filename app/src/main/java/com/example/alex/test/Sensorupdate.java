package com.example.alex.test;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.provider.Settings.Secure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Blackilli on 14.11.17.
 */

public class Sensorupdate {
    private static Sensorupdate instance;
    private SparseArray<LinkedList<Sensorwert>> sensoren;
    private static SparseArray<LinkedList<Sensorwert>> newSensorData;
    private int newWerteCount = 0;
    private int updateThreshold = 50;
    public final int TYPE_GPS = 1337;
    private static String android_id = null;

    private Sensorupdate(){
        sensoren = new SparseArray<>();
        newSensorData = new SparseArray<>();
    }

    public LinkedList<Sensorwert> getSensorWerte(int sensor){
        return sensoren.get(sensor);
    }

    public void addSensorWert(int sensor, Sensorwert wert){
        try {
            sensoren.get(sensor).add(wert);
        } catch (NullPointerException ex) {
            sensoren.put(sensor, new LinkedList<Sensorwert>());
            sensoren.get(sensor).add(wert);
        }
        try {
            newSensorData.get(sensor).add(wert);
        } catch (NullPointerException ex) {
            newSensorData.put(sensor, new LinkedList<Sensorwert>());
            newSensorData.get(sensor).add(wert);
        }
        newWerteCount++;
        if (newWerteCount >= updateThreshold){
            saveSensorwerte();
        }
    }

    public static Sensorupdate getInstance(Context context){
        if (android_id == null){
            android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID) != null ? Secure.getString(context.getContentResolver(), Secure.ANDROID_ID).substring(0, 15) : "1";
        }
        return getInstance();
    }

    public static Sensorupdate getInstance(){
        if (instance == null)
            return instance = new Sensorupdate();
        else
            return instance;
    }

    public void saveSensorwerte(){

        newSensorData.clear();
        newWerteCount = 0;

        Log.d("Sensorwerte", "Ich Speichere jetzt die Sensorwerte");
    }

    public void loadWerte(int sensor){

    }

    public void loadWerte(int sensor, long since){

    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("User", android_id);
        JSONArray array = new JSONArray();
        for (int i = 0; i < sensoren.size(); i++){
            for (Sensorwert sensorwert : sensoren.valueAt(i)){
                array.put(new JSONObject()
                        .put("type", sensoren.keyAt(i))
                        .put("timestamp", sensorwert.getTimestamp())
                        .put("x", sensorwert.getValues()[0])
                        .put("y", sensorwert.getValues()[1])
                        .put("z", sensorwert.getValues()[2]));
            }
        }
        jsonObject.put("Werte", array);
        return jsonObject;
    }

    public JSONObject newDataToJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("User", android_id);
        JSONArray array = new JSONArray();
        for (int i = 0; i < newSensorData.size(); i++){
            for (Sensorwert sensorwert : newSensorData.valueAt(i)){
                array.put(new JSONObject()
                        .put("type", newSensorData.keyAt(i))
                        .put("timestamp", sensorwert.getTimestamp())
                        .put("x", sensorwert.getValues()[0])
                        .put("y", sensorwert.getValues()[1])
                        .put("z", sensorwert.getValues()[2]));
            }
        }
        jsonObject.put("Werte", array);
        return jsonObject;
    }
}
