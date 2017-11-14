package com.example.alex.test;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.GraphViewXML;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GrafikActivity extends AppCompatActivity {

    TextView tvSensor;
    TextView tvWert;
    GraphView graph;
    String sensor;
    ArrayList<String> sensorNamen = new ArrayList<>();
    LinkedList<Sensorwert> werteList = new LinkedList<Sensorwert>();
    Sensoren sens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafik);
        Intent intent = getIntent();
        tvSensor = (TextView) findViewById(R.id.tvSensor);
        tvWert = (TextView) findViewById(R.id.tvWert);
        graph = (GraphView) findViewById(R.id.graph);

        for (int i = 0; i < 400; i++) {
            werteList.add(new Sensorwert(i, new float[]{new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()}));
        }

        sensor = intent.getStringExtra("Sensor");
        sensorNamen = intent.getStringArrayListExtra("Sensornamen");
        switch (sensor) {
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
        LineGraphSeries<DataPoint> s1 = new LineGraphSeries<>();
        LineGraphSeries<DataPoint> s2 = new LineGraphSeries<>();
        LineGraphSeries<DataPoint> s3 = new LineGraphSeries<>();

        s1.setColor(Color.RED);
        s2.setColor(Color.YELLOW);
        s3.setColor(Color.GREEN);

        if(sensor.equals("Licht") || sensor.equals("Proximity")) {
            for (int i = 0; i < werteList.size(); i++) {
                DataPoint dp = new DataPoint(werteList.get(i).getTimestamp(), werteList.get(i).getValues()[0]);
                s1.appendData(dp, false, 10000);
            }
            graph.addSeries(s1);
        }
        else {
            for (int i = 0; i < werteList.size(); i++) {
                DataPoint dp = new DataPoint(werteList.get(i).getTimestamp(), werteList.get(i).getValues()[0]);
                s1.appendData(dp, false, 10000);
                DataPoint dp2 = new DataPoint(werteList.get(i).getTimestamp(), werteList.get(i).getValues()[1]);
                s2.appendData(dp2, false, 10000);
                DataPoint dp3 = new DataPoint(werteList.get(i).getTimestamp(), werteList.get(i).getValues()[2]);
                s3.appendData(dp3, false, 10000);
            }
            graph.addSeries(s1);
            graph.addSeries(s2);
            graph.addSeries(s3);
        }

    }
}
