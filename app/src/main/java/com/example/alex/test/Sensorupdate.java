package com.example.alex.test;

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
    private HashMap<Integer, LinkedList<Sensorwert>> sensoren;
    private int newWerteCount = 0;
    private final int updateThreshold = 50;
    public final int TYPE_GPS = 1337;

    private Sensorupdate(){
        sensoren = new HashMap<Integer, LinkedList<Sensorwert>>();

    }

    public void addSensor(int sensor){
        sensoren.put(sensor, new LinkedList<Sensorwert>());
    }

    public LinkedList<Sensorwert> getSensorWerte(int sensor){
        return sensoren.get(sensor);
    }

    public void addSensorWert(int sensor, Sensorwert wert){
        sensoren.get(sensor).add(wert);
        newWerteCount++;
        if (newWerteCount == updateThreshold){
            saveSensorwerte();
            newWerteCount = 0;
        }
    }

    public static Sensorupdate getInstance(){
        if (instance == null)
            return instance = new Sensorupdate();
        else
            return instance;
    }

    public static void saveSensorwerte(){

    }

    public void loadWerte(int sensor){

    }

    public void loadWerte(int sensor, long since){

    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("User", 1l);
        JSONArray array = new JSONArray();
        for (int sensor : sensoren.keySet()){
            for (Sensorwert sensorwert : sensoren.get(sensor)){
                array.put(new JSONObject()
                        .put("type", sensor)
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
