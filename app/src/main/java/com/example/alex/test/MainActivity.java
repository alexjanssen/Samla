package com.example.alex.test;

import android.content.res.Resources;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;

public class MainActivity extends AppCompatActivity {
    ArrayList<CheckBox> cbL1 = new ArrayList();
    ArrayList<TextView> tvL2 = new ArrayList();

    TextView tvLichtWert;
    TextView tvAccWert;
    TextView tvRotationWert;
    TextView tvGravityWert;
    TextView tvProximityWert;
    TextView tvMagnetfeldWert;
    Button bttnPush;
    Button bttnStart;
    CheckBox cbLicht;
    CheckBox cbAcc;
    CheckBox cbRotation;
    CheckBox cbGravity;
    CheckBox cbProximity;
    CheckBox cbMagnetfeld;
    EditText etFLicht;
    EditText etFAcc;
    EditText etFRotation;
    EditText etFGravity;
    EditText etFProximity;
    EditText etFMagnetfeld;
    Sensoren sens;
    boolean record = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bttnPush = (Button) findViewById(R.id.bttnPush);
        bttnStart = (Button) findViewById(R.id.bttnStart);

        cbLicht = (CheckBox) findViewById(R.id.cbLicht);
        cbAcc = (CheckBox) findViewById(R.id.cbAcc);
        cbRotation = (CheckBox) findViewById(R.id.cbRotation);
        cbGravity = (CheckBox) findViewById(R.id.cbGravity);
        cbProximity = (CheckBox) findViewById(R.id.cbProximity);
        cbMagnetfeld = (CheckBox) findViewById(R.id.cbMagnetfeld);

        tvLichtWert = (TextView) findViewById(R.id.tvLichtWert);
        tvAccWert = (TextView) findViewById(R.id.tvAccWert);
        tvRotationWert = (TextView) findViewById(R.id.tvRotationWert);
        tvGravityWert = (TextView) findViewById(R.id.tvGravityWert);
        tvProximityWert = (TextView) findViewById(R.id.tvProximityWert);
        tvMagnetfeldWert = (TextView) findViewById(R.id.tvMagnetfeldWert);

        etFLicht = (EditText) findViewById(R.id.etFLicht);
        etFAcc = (EditText) findViewById(R.id.etFAcc);
        etFRotation = (EditText) findViewById(R.id.etFRotation);
        etFGravity = (EditText) findViewById(R.id.etFGravity);
        etFProximity = (EditText) findViewById(R.id.etFProximity);
        etFMagnetfeld = (EditText) findViewById(R.id.etFMagnetfeld);

        cbL1.add(cbLicht);
        cbL1.add(cbAcc);
        cbL1.add(cbRotation);
        cbL1.add(cbGravity);
        cbL1.add(cbProximity);
        cbL1.add(cbMagnetfeld);

        tvL2.add(tvLichtWert);
        tvL2.add(tvAccWert);
        tvL2.add(tvRotationWert);
        tvL2.add(tvGravityWert);
        tvL2.add(tvProximityWert);
        tvL2.add(tvMagnetfeldWert);


        bttnPush.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            sens = new Sensoren(MainActivity.this);
                                            List<String> sl = sens.getAllSensorNames();
                                            for (int i = 0; i < cbL1.size() && i < sl.size(); i++) {
                                                cbL1.get(i).setText(sens.sList.get(i).getName());
                                            }

                                            cbLicht.setOnCheckedChangeListener(sens.checkedChangeListener);
                                            cbAcc.setOnCheckedChangeListener(sens.checkedChangeListener);
                                            cbRotation.setOnCheckedChangeListener(sens.checkedChangeListener);
                                            cbGravity.setOnCheckedChangeListener(sens.checkedChangeListener);
                                            cbProximity.setOnCheckedChangeListener(sens.checkedChangeListener);
                                            cbMagnetfeld.setOnCheckedChangeListener(sens.checkedChangeListener);
                                            cbLicht.setChecked(true);
                                            cbAcc.setChecked(true);
                                            cbRotation.setChecked(true);
                                            cbGravity.setChecked(true);
                                            cbProximity.setChecked(true);
                                            cbMagnetfeld.setChecked(true);


                                        }
                                    }
        );

        bttnStart.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             if (bttnStart.getText() == "start") {
                                                 bttnStart.setText("stop");
                                                 bttnStart.invalidate();
                                                 record = true;
                                                 handler(50);
                                             } else {
                                                 bttnStart.setText("start");
                                                 record = false;
                                             }
                                         }
                                     }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handler(final int i) {
        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //toDo
                Log.d("Handler:", "running...");
                ha.postDelayed(this, i);
                if (record) {
                    ArrayList<String> sl = sens.getWerte();
                    for (int i = 0; i < sl.size(); i++) {
                        tvL2.get(i).setText(sl.get(i));
                    }
                }
            }
        }, i);
    }

    public int samplingRate(Sensor s)
    {
        int eingabe = 0;
        switch(s.getType())
        {
            case Sensor.TYPE_LIGHT:
                eingabe = Integer.parseInt(etFLicht.getText().toString());
                break;

            case Sensor.TYPE_LINEAR_ACCELERATION:
                eingabe = Integer.parseInt(etFAcc.getText().toString());
                break;

            case Sensor.TYPE_GYROSCOPE:
                eingabe = Integer.parseInt(etFRotation.getText().toString());
                break;

            case Sensor.TYPE_GRAVITY:
                eingabe = Integer.parseInt(etFGravity.getText().toString());
                break;

            case Sensor.TYPE_PROXIMITY:
                eingabe = Integer.parseInt(etFProximity.getText().toString());
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                eingabe = Integer.parseInt(etFMagnetfeld.getText().toString());
                break;

            default:
                break;
        }

        return 1000000 / eingabe;
    }
}
