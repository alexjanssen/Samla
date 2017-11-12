package com.example.alex.test;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Alex on 27.10.2017.
 */

public class Sensoren implements SensorEventListener {
    private SensorManager sensorManager;
    Context mContext;
    List<Sensor> sList;
    ArrayList<String> werte = new ArrayList<>();
    private Sensor mLicht;
    private Sensor mAccelerometer;
    private Sensor mRotation;
    private Sensor mGravity;
    private Sensor mProximity;
    private Sensor mMagnetfeld;
    MainActivity ma = new MainActivity();

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

                    case R.id.cbMagnetfeld:
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

                    case R.id.cbMagnetfeld:
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
        sensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        mLicht = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mRotation = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mMagnetfeld = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sList = new ArrayList<>();
        sList.add(mLicht);sList.add(mAccelerometer);sList.add(mRotation);sList.add(mGravity);
        sList.add(mProximity);sList.add(mMagnetfeld);
        werte.add("");werte.add("");werte.add("");werte.add("");werte.add("");werte.add("");
    }

    public void sensorRegister(Sensor s) {
        sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
    }
    public void sensorUnregister(Sensor s)
    {
        sensorManager.unregisterListener(this, s);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
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

