package com.example.alex.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;

import java.util.ArrayList;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Alex on 27.10.2017.
 */

public class Sensoren implements SensorEventListener {
    private SensorManager sensorManager;
    Context mContext;
    ArrayList<Sensor> sList;
    ArrayList<String> werte = new ArrayList<>();
    ArrayList<float[]> werteFuerGraph = new ArrayList<>();
    private Sensor mLicht;
    private Sensor mAccelerometer;
    private Sensor mRotation;
    private Sensor mGravity;
    private Sensor mProximity;
    private Sensor mMagnetfeld;
    MainActivity ma = new MainActivity();
    LocationManager locMan;
    Location loc = null;
    Sensorupdate sensorupdate;

    public CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            if(!isChecked)
            {
                switch (buttonView.getId()) {
                    case R.id.cbLicht:
                        sensorUnregister(mLicht);
                        break;
                    case R.id.cbAcc:
                        sensorUnregister(mAccelerometer);
                        break;

                    case R.id.cbRotation:
                        sensorUnregister(mRotation);
                        break;

                    case R.id.cbGravity:
                        sensorUnregister(mGravity);
                        break;

                    case R.id.cbProximity:
                        sensorUnregister(mProximity);
                        break;

                    case R.id.cbLocation:
                        sensorUnregister(mMagnetfeld);
                        break;

                    default:
                        break;
                }
            }
            else
            {
                switch (buttonView.getId()){
                    case R.id.cbLicht:
                        sensorRegister(mLicht);
                        break;

                    case R.id.cbAcc:
                        sensorRegister(mAccelerometer);
                        break;

                    case R.id.cbRotation:
                        sensorRegister(mRotation);
                        break;

                    case R.id.cbGravity:
                        sensorRegister(mGravity);
                        break;

                    case R.id.cbProximity:
                        sensorRegister(mProximity);
                        break;

                    case R.id.cbLocation:
                        sensorRegister(mMagnetfeld);
                        break;

                    default:
                        break;
                }
            }
        }
    };

    public Sensoren(Context mContext){
        this.mContext = mContext;
        sensorupdate = Sensorupdate.getInstance();
        sensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        mLicht = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mRotation = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY); //auskommentieren für Motorola Moto G
        mProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mMagnetfeld = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sList = new ArrayList<>();
        sList.add(mLicht);
        sList.add(mAccelerometer);
        sList.add(mRotation);
        sList.add(mGravity);      //auskommentieren für Motorola Moto G
        sList.add(mProximity);
        sList.add(mMagnetfeld);
        werte.add("");werte.add("");werte.add("");werte.add("");werte.add("");werte.add("");

        locMan = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        initLocMan();
    }

    public void sensorRegister(Sensor s) {
        sensorManager.registerListener(this, s, 100000);
    }
    public void sensorUnregister(Sensor s)
    {
        sensorManager.unregisterListener(this, s);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        sensorupdate.addSensorWert(sensorEvent.sensor.getType(), new Sensorwert(sensorEvent.timestamp, sensorEvent.values));
        String str2 = "";
        Log.d("sensorEvent:", sensorEvent.sensor.getName());
        for(int i=0;i < sList.size();i++){
            if(sensorEvent.sensor.getType() == sList.get(i).getType()){

                for(int u=0;u < sensorEvent.values.length;u++){
                    str2 = str2 + sensorEvent.values[u]+" | ";
                }
                werte.set(i, str2);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @SuppressLint("MissingPermission")
    private void initLocMan(){
        LocationListener locList = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                loc = location;

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {}
        };

        locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locList);
    }

    public ArrayList<String> getWerte(){
        return werte;
    }

    public Location getLocation(){
        return loc;
    }

}

