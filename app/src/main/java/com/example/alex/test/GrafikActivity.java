package com.example.alex.test;

import android.content.Intent;
import android.hardware.Sensor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GrafikActivity extends AppCompatActivity {

    MainActivity ma;
    TextView tvSensor;
    String sensor;
    ArrayList<String> sensorNamen = new ArrayList<>();
    Sensoren sens;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafik);
        Intent intent = getIntent();
        tvSensor = (TextView) findViewById(R.id.tvSensor);
        sensor = intent.getStringExtra("Sensor");
        sensorNamen = intent.getStringArrayListExtra("Sensornamen");
        switch(sensor)
        {
            case "Licht":
                tvSensor.setText(sensorNamen.get(0));
                break;
            case "Acc":
                tvSensor.setText(sensorNamen.get(1));
                break;
            case "Rotation":
                tvSensor.setText(sensorNamen.get(2));
                break;
            case "Gravity":
                tvSensor.setText(sensorNamen.get(3));
                break;
            case "Proximity":
                tvSensor.setText(sensorNamen.get(4));
                break;
            case "Megnetfeld":
                tvSensor.setText(sensorNamen.get(5));
                break;
            default:
                break;
        }
    }
}
