package de.hs_bochum.gruppe.die.gpssamla;

import android.content.Context;
import android.location.Location;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Blackilli on 16.02.2018.
 */

public class RESTClient {
    private String serverAdresse = "http://blackilli.de/locations";
    private RequestQueue requestQueue;
    private Logger logger;
    private String androidId;
    private static RESTClient instance;


    public RESTClient(Context context, Logger logger, String androidId){
        requestQueue = Volley.newRequestQueue(context);
        this.logger = logger;
        this.androidId = androidId;
        instance = this;
    }

    public static RESTClient getInstance(){
        return instance;
    }

    public void sendUpdate(Location location) {
        logger.locationSend(location);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user", androidId);
            jsonObject.put("latitude", location.getLatitude());
            jsonObject.put("longitude", location.getLongitude());
            jsonObject.put("timestamp", location.getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestQueue.add(new JsonObjectRequest(Request.Method.POST, serverAdresse, jsonObject,
                response -> {
                    try {
                        if (response.get("status").equals("success"))
                            return;
                        else
                            Log.e("RESTClient", "JSON wurde nicht angenommen: " + response.toString());
                    } catch (JSONException e) {
                        Log.e("RESTClient", "JSON wurde nicht angenommen: " + response.toString());
                        e.printStackTrace();
                    }
                },
                error -> {
                    logger.connectionError(error);
                    try {
                        Thread.sleep(5000);
                        sendUpdate(location);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        ));
    }
}
