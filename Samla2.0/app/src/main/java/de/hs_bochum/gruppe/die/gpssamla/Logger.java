package de.hs_bochum.gruppe.die.gpssamla;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.VolleyError;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;

/**
 * Created by Blackilli on 17.02.2018.
 */

public class Logger {
    private LinkedList<Location> receivedList = new LinkedList<>(), sendList = new LinkedList<>();
    private File locationLog, connectionLog;
    private static Logger instance;

    private Logger(Context context){
        locationLog = new File(context.getFilesDir(), "locations.log");
        connectionLog = new File(context.getFilesDir(), "connection.log");
        instance = this;
    }

    public void connectionError(VolleyError error){
        try (FileWriter fw = new FileWriter(connectionLog, true)) {
            fw.write(System.currentTimeMillis() + ": " + error.getLocalizedMessage());
            fw.close();
        } catch (Exception ex) {
            Log.e("Logger", ex.getMessage());
        }
    }

    public void locationReceived(Location location){
        receivedList.add(location);
    }

    public void locationSend(Location location){
        sendList.add(location);
    }

    public static Logger getInstance(){
        return instance;
    }

    public static Logger getInstance(Context context){
        return instance == null ? instance = new Logger(context) : instance;
    }

    public LinkedList<Location> receivedLocations(){
        return receivedList;
    }

    public LinkedList<Location> sendLocations(){
        return sendList;
    }

    public Location getLastSendLocation(){
        return sendList.isEmpty() ? null : sendList.getLast();
    }
}
