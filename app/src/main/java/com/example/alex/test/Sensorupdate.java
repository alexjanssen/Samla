package com.example.alex.test;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.provider.Settings.Secure;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Created by Blackilli on 14.11.17.
 */

public class Sensorupdate {
    private static Sensorupdate instance;
    private SparseArray<LinkedList<Sensorwert>> sensoren;
    private static SparseArray<LinkedList<Sensorwert>> newSensorData;
    private int newWerteCount = 0;
    private int updateThreshold = 200;
    public final int TYPE_GPS = 1337;
    private String android_id = null;
    private RequestQueue requestQueue;
    private Response.ErrorListener errorListener;

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
            Log.d("Sensorupdate;",wert+";;"+sensor);
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

    public static Sensorupdate getInstance(final Context context){
        Sensorupdate sensorupdate = getInstance();
        if (sensorupdate.android_id == null){
            sensorupdate.android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID) != null ? Secure.getString(context.getContentResolver(), Secure.ANDROID_ID).substring(0, 15) : "1";
            sensorupdate.requestQueue = Volley.newRequestQueue(context);
            sensorupdate.errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ErrorListener", error.toString());
                    error.printStackTrace();
                    Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            };
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
        Log.d("Sensorwerte", "Ich Speichere jetzt die Sensorwerte");
        try {
            JSONObject[] jsonObjects = newDataToJSONObjectArray();
            for (int i = 0; i < jsonObjects.length; i++)
            {
                requestQueue.add(new JsonObjectRequest(Request.Method.POST,
                        "http://blackilli.de/sensor",
                        jsonObjects[i],
                        response -> Log.d("Sensowerte", "Sensorwerte erfolgreich gespeichtert"),
                        errorListener));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        newSensorData.clear();
        newWerteCount = 0;
    }

    public void loadWerte(int sensor){
        requestQueue.add(new JsonObjectRequest(Request.Method.GET,
                String.format(Locale.getDefault(),"http://blackilli.de/sensor?user=%s&sensor=%d", android_id, sensor),
                null,
                (JSONObject response) -> {
                    try {
                        LinkedList<Sensorwert> tmp = new LinkedList<>();
                        Log.d("Sensorwerte", String.format("Werte für sensor %d geladen", sensor));
                        JSONArray sensorvalues = response.getJSONArray("Sensorvalues");
                        for (int i = 0; i < sensorvalues.length(); i++){
                            long timestamp = sensorvalues.getJSONObject(i).getLong("Timestamp");
                            float[] values = new float[3];
                            for (int j = 0; j < 3; j++){
                                values[j] = (float)sensorvalues.getJSONObject(i).getJSONArray("Values").getDouble(j);
                            }
                            tmp.add(new Sensorwert(timestamp, values));
                        }
                        sensoren.get(sensor).addAll(0, tmp);
                        tmp.clear();
                    } catch (JSONException e) {
                        errorListener.onErrorResponse(new VolleyError(e));
                    }
                },
                errorListener));
    }

    public void loadWerte(int sensor, long since, long untill){

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

    public JSONObject[] newDataToJSONObjectArray() throws JSONException {
        JSONObject[] jsonObjects = new JSONObject[newSensorData.size()];
        for (int i = 0; i < newSensorData.size(); i++){
            jsonObjects[i] = new JSONObject();
            jsonObjects[i].put("User", android_id);
            jsonObjects[i].put("Sensortype", newSensorData.keyAt(i));
            JSONArray array = new JSONArray();
            for (Sensorwert sensorwert : newSensorData.valueAt(i)){
                array.put(new JSONObject()
                        .put("Timestamp", sensorwert.getTimestamp())
                        .put("Values", new JSONArray()
                        .put(sensorwert.getValues()[0])
                        .put(sensorwert.getValues()[1])
                        .put(sensorwert.getValues()[2])));
            }
            jsonObjects[i].put("Sensorvalues", array);
        }
        return jsonObjects;
    }
}
