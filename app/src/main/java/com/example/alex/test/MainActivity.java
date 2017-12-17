package com.example.alex.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import android.os.Handler;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    ArrayList<CheckBox> cbL1 = new ArrayList();
    ArrayList<TextView> tvL2 = new ArrayList();

    TextView tvLichtWert;
    TextView tvAccWert;
    TextView tvRotationWert;
    TextView tvGravityWert;
    TextView tvProximityWert;
    TextView tvMagnetfeldWert;
    TextView tvLocationWert;
    Button bttnPush;
    Button bttnStart;
    Button bttnLicht;
    Button bttnAcc;
    Button bttnRotation;
    Button bttnGravity;
    Button bttnProximity;
    Button bttnMagnetfeld;
    Button bttnLocation;
    CheckBox cbLicht;
    CheckBox cbAcc;
    CheckBox cbRotation;
    CheckBox cbGravity;
    CheckBox cbProximity;
    CheckBox cbMagnetfeld;
    CheckBox cbLocation;
    //EditText etDelay;
    SeekBar sbSamplingrate;
    TextView tvSamplingrate;
    Sensoren sens;
    ArrayList<Sensor> sensorListe;
    Sensorupdate sensorupdate;
    int samplingrate;
    boolean record = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        sensorupdate = Sensorupdate.getInstance(getApplicationContext());

        bttnPush = (Button) findViewById(R.id.bttnPush);
        bttnStart = (Button) findViewById(R.id.bttnStart);
        bttnLicht = (Button) findViewById(R.id.bttnLicht);
        bttnLicht.setVisibility(View.INVISIBLE);
        bttnAcc = (Button) findViewById(R.id.bttnAcc);
        bttnAcc.setVisibility(View.INVISIBLE);
        bttnRotation = (Button) findViewById(R.id.bttnRotation);
        bttnRotation.setVisibility(View.INVISIBLE);
        //bttnGravity = (Button) findViewById(R.id.bttnGravity);
        //bttnGravity.setVisibility(View.INVISIBLE);
        bttnProximity = (Button) findViewById(R.id.bttnProximity);
        bttnProximity.setVisibility(View.INVISIBLE);
        bttnMagnetfeld = (Button) findViewById(R.id.bttnMagnetfeld);
        bttnMagnetfeld.setVisibility(View.INVISIBLE);
        bttnLocation = (Button) findViewById(R.id.bttnLocation);
        // bttnLocation.setVisibility(View.INVISIBLE);


        cbLicht = (CheckBox) findViewById(R.id.cbLicht);
        cbAcc = (CheckBox) findViewById(R.id.cbAcc);
        cbRotation = (CheckBox) findViewById(R.id.cbRotation);
        //cbGravity = (CheckBox) findViewById(R.id.cbGravity);
        cbProximity = (CheckBox) findViewById(R.id.cbProximity);
        cbMagnetfeld = (CheckBox) findViewById(R.id.cbMagnetfeld);
        cbLocation = (CheckBox) findViewById(R.id.cbLocation);


        tvLichtWert = (TextView) findViewById(R.id.tvLichtWert);
        tvAccWert = (TextView) findViewById(R.id.tvAccWert);
        tvRotationWert = (TextView) findViewById(R.id.tvRotationWert);
        //tvGravityWert = (TextView) findViewById(R.id.tvGravityWert);
        tvProximityWert = (TextView) findViewById(R.id.tvProximityWert);
        tvMagnetfeldWert = (TextView) findViewById(R.id.tvMagnetfeldWert);
        tvLocationWert = (TextView) findViewById(R.id.tvLocationWert);

        //etDelay = (EditText) findViewById(R.id.etDelay);
        tvSamplingrate = (TextView) findViewById(R.id.tvSamplingrate);
        sbSamplingrate = (SeekBar) findViewById(R.id.sbSamplingrate);
        sbSamplingrate.setMax(120);
        sbSamplingrate.setProgress(1);

        cbL1.add(cbLicht);
        cbL1.add(cbAcc);
        cbL1.add(cbRotation);
        //cbL1.add(cbGravity);    //auskommentieren für Motorola Moto G
        cbL1.add(cbProximity);
        cbL1.add(cbMagnetfeld);
        cbL1.add(cbLocation);

        tvL2.add(tvLichtWert);
        tvL2.add(tvAccWert);
        tvL2.add(tvRotationWert);
        //tvL2.add(tvGravityWert);  //auskommentieren für Motorola Moto G
        tvL2.add(tvProximityWert);
        tvL2.add(tvMagnetfeldWert);
        tvL2.add(tvLocationWert);

        //samplingrate = Integer.parseInt(etDelay.getText().toString());
        samplingrate = sbSamplingrate.getProgress();
        samplingrate = samplingrate * 100;
        tvSamplingrate.setText("Samplingrate: " + samplingrate);

        bttnPush.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            sens = new Sensoren(MainActivity.this);
                                            sensorListe = sens.sList;
                                            for (int i = 0; i < cbL1.size() && i < sensorListe.size(); i++) {
                                                Log.d("bla", sensorListe.get(i).getName());
                                                cbL1.get(i).setText(sensorListe.get(i).getName());
                                            }
                                            cbLicht.setOnCheckedChangeListener(sens.checkedChangeListener);
                                            cbAcc.setOnCheckedChangeListener(sens.checkedChangeListener);
                                            cbRotation.setOnCheckedChangeListener(sens.checkedChangeListener);
                                            //cbGravity.setOnCheckedChangeListener(sens.checkedChangeListener);
                                            cbProximity.setOnCheckedChangeListener(sens.checkedChangeListener);
                                            cbMagnetfeld.setOnCheckedChangeListener(sens.checkedChangeListener);
                                            cbLocation.setOnCheckedChangeListener(sens.checkedChangeListener);
                                            cbLicht.setChecked(true);
                                            cbAcc.setChecked(true);
                                            cbRotation.setChecked(true);
                                            //cbGravity.setChecked(true);
                                            cbProximity.setChecked(true);
                                            cbMagnetfeld.setChecked(true);
                                            cbLocation.setChecked(true);
                                            bttnLicht.setVisibility(View.VISIBLE);
                                            bttnAcc.setVisibility(View.VISIBLE);
                                            bttnRotation.setVisibility(View.VISIBLE);
                                            //bttnGravity.setVisibility(View.VISIBLE);
                                            bttnProximity.setVisibility(View.VISIBLE);
                                            bttnMagnetfeld.setVisibility(View.VISIBLE);
                                            bttnLocation.setVisibility(View.VISIBLE);
                                        }
                                    }
        );

        bttnStart.setOnClickListener(v -> {
                    if (bttnStart.getText() == "start") {
                        bttnStart.setText("stop");
                        bttnStart.invalidate();
                        record = true;
                        handler(samplingrate);
                    } else {
                        bttnStart.setText("start");
                        record = false;
                    }
                }
        );

        bttnLicht.setOnClickListener(v -> {
                    sensorGrafikAufrufen(Sensor.TYPE_LIGHT);
                    //sensorGrafikAufrufen("Licht");
                }
        );
        bttnAcc.setOnClickListener(v -> {
                    sensorGrafikAufrufen(Sensor.TYPE_ACCELEROMETER);
                    //sensorGrafikAufrufen("Acc");
                }
        );
        bttnRotation.setOnClickListener(v -> {
                    sensorGrafikAufrufen(Sensor.TYPE_ORIENTATION);
                    //sensorGrafikAufrufen("Rotation");
                }
        );
        /*bttnGravity.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             sensorGrafikAufrufen(Sensor.TYPE_GRAVITY);
                                             //sensorGrafikAufrufen("Gravity");
                                         }
                                     }
        );*/
        bttnProximity.setOnClickListener(v -> {
                    sensorGrafikAufrufen(Sensor.TYPE_PROXIMITY);
                    //sensorGrafikAufrufen(8);
                    //sensorGrafikAufrufen("Proximity");
                }
        );
        bttnMagnetfeld.setOnClickListener(v -> {
                    sensorGrafikAufrufen(Sensor.TYPE_MAGNETIC_FIELD);
                    //sensorGrafikAufrufen(2);
                    //sensorGrafikAufrufen("Magnetfeld");
                }
        );
        bttnLocation.setOnClickListener(v -> mapAufrufen()
        );

        sbSamplingrate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                      @Override
                                                      public void onStopTrackingTouch(SeekBar seekBar) {
                                                          // TODO Auto-generated method stub
                                                      }

                                                      @Override
                                                      public void onStartTrackingTouch(SeekBar seekBar) {
                                                          // TODO Auto-generated method stub
                                                      }

                                                      @Override
                                                      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                          // TODO Auto-generated method stub
                                                          samplingrate = progress;
                                                          samplingrate = samplingrate * 100;
                                                          tvSamplingrate.setText("Samplingrate: " + samplingrate);
                                                          handler(samplingrate);
                                                      }
                                                  }
        );

    }

    @Override
    protected void onDestroy() {
        sensorupdate.saveSensorwerte();
        super.onDestroy();
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
                //Log.d("Handler:", "running...");
                ha.postDelayed(this, i);
                if (record) {
                    ArrayList<String> sl = sens.getWerte();
                    for (int i = 0; i < sl.size(); i++) {
                        tvL2.get(i).setText(sl.get(i));
                    }
                    //############################################################
                    if (sens.getLocation() != null) {
                        tvL2.get(tvL2.size() - 1).setText(sens.getLocation().getLatitude() + " | " + sens.getLocation().getLongitude() + " | " + sens.getLocation().getAltitude());
                    }
                }
            }
        }, i);
    }

    /*public void sensorGrafikAufrufen(String sens)
    {
        Intent intent = new Intent(this, GrafikActivity.class);
        intent.putExtra("Sensor", sens);
        ArrayList<String> sa = new ArrayList<>();
        for(int i = 0; i < sensorListe.size(); i++)
        {
            sa.add(sensorListe.get(i).getName().toString());
        }
        intent.putStringArrayListExtra("Sensornamen", sa);
        startActivity(intent);
    }*/

    public void sensorGrafikAufrufen(int sens) {
        Intent intent = new Intent(this, GrafikActivity.class);
        intent.putExtra("Sensor", sens);
        ArrayList<String> sa = new ArrayList<>();
        for (int i = 0; i < sensorListe.size(); i++) {
            sa.add(sensorListe.get(i).getName());
        }
        intent.putStringArrayListExtra("Sensornamen", sa);
        startActivity(intent);
    }

    public void mapAufrufen() {
        Intent intent = new Intent(this, NewMapsActivity.class);
        startActivity(intent);
    }

}
