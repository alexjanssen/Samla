package com.example.alex.test;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Alex on 27.10.2017.
 */

public class Sensoren implements SensorEventListener {
    private SensorManager sensorManager;
    Context mContext;
    List<Sensor> sList;
    ArrayList<String> werte = new ArrayList<>();
    private Sensor mAccelerometer;

    public Sensoren(Context mContext){
        this.mContext = mContext;
        sensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        sList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        sensorRegister();
        //sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        //mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        werte.add("");werte.add("");werte.add("");werte.add("");werte.add("");werte.add("");werte.add("");werte.add("");werte.add("");werte.add("");
    }


    private void sensorRegister() {
        for (int i = 0; i < sList.size(); i++){
            sensorManager.registerListener(this, sList.get(i), SensorManager.SENSOR_DELAY_NORMAL);
            Log.d("Sensor registered:", sList.get(i).getName());
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        String str = "";
        String str2 = "";
        //Log.d("sensorEvent:", sensorEvent.sensor.getName());
        for(int i=0;i < sList.size();i++){
            if(sensorEvent.sensor.getType() == sList.get(i).getType()){
                for(int u=0;u < sensorEvent.values.length;u++){
                    str2 = str2 + sensorEvent.values[u]+" | ";
                    //str2 = str2.substring(0,5);
                    //str = str + str2 + "|";
                }
                werte.set(i, str2);
            }
        }
    }




        @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public ArrayList<String> getAllSensorNames(){
        ArrayList<String> sen = new ArrayList<>();
        for(int i=0;i < sList.size();i++){
            sen.add(sList.get(i).getName());
        }
        return sen;
    }

    public ArrayList<String> getWerte(){
        return werte;
    }

}
