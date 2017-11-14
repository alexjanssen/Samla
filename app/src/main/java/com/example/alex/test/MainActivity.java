package com.example.alex.test;

import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;

import org.w3c.dom.Text;

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
    Button bttnLicht;
    Button bttnAcc;
    Button bttnRotation;
    Button bttnGravity;
    Button bttnProximity;
    Button bttnMagnetfeld;
    CheckBox cbLicht;
    CheckBox cbAcc;
    CheckBox cbRotation;
    CheckBox cbGravity;
    CheckBox cbProximity;
    CheckBox cbMagnetfeld;
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

        sensorupdate = Sensorupdate.getInstance();

        bttnPush = (Button) findViewById(R.id.bttnPush);
        bttnStart = (Button) findViewById(R.id.bttnStart);
        bttnLicht = (Button) findViewById(R.id.bttnLicht);
        bttnLicht.setVisibility(View.INVISIBLE);
        bttnAcc = (Button) findViewById(R.id.bttnAcc);
        bttnAcc.setVisibility(View.INVISIBLE);
        bttnRotation = (Button) findViewById(R.id.bttnRotation);
        bttnRotation.setVisibility(View.INVISIBLE);
        bttnGravity = (Button) findViewById(R.id.bttnGravity);
        bttnGravity.setVisibility(View.INVISIBLE);
        bttnProximity = (Button) findViewById(R.id.bttnProximity);
        bttnProximity.setVisibility(View.INVISIBLE);
        bttnMagnetfeld = (Button) findViewById(R.id.bttnMagnetfeld);
        bttnMagnetfeld.setVisibility(View.INVISIBLE);


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

        //etDelay = (EditText) findViewById(R.id.etDelay);
        tvSamplingrate =(TextView) findViewById(R.id.tvSamplingrate);
        sbSamplingrate = (SeekBar) findViewById(R.id.sbSamplingrate);
        sbSamplingrate.setMax(120);
        sbSamplingrate.setProgress(1);

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

        //samplingrate = Integer.parseInt(etDelay.getText().toString());
        samplingrate = sbSamplingrate.getProgress();
        samplingrate = samplingrate *100;
        tvSamplingrate.setText("Samplingrate: "+samplingrate);

        bttnPush.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            sens = new Sensoren(MainActivity.this);
                                            sensorListe = sens.sList;
                                            for (int i = 0; i < cbL1.size() && i < sensorListe.size(); i++) {
                                                cbL1.get(i).setText(sensorListe.get(i).getName());
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
                                            bttnLicht.setVisibility(View.VISIBLE);
                                            bttnAcc.setVisibility(View.VISIBLE);
                                            bttnRotation.setVisibility(View.VISIBLE);
                                            bttnGravity.setVisibility(View.VISIBLE);
                                            bttnProximity.setVisibility(View.VISIBLE);
                                            bttnMagnetfeld.setVisibility(View.VISIBLE);
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
                                                 handler(samplingrate);
                                             } else {
                                                 bttnStart.setText("start");
                                                 record = false;
                                             }
                                         }
                                     }
        );

        bttnLicht.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             sensorGrafikAufrufen("Licht");
                                         }
                                     }
        );
        bttnAcc.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             sensorGrafikAufrufen("Acc");
                                         }
                                     }
        );
        bttnRotation.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             sensorGrafikAufrufen("Rotation");
                                         }
                                     }
        );
        bttnGravity.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             sensorGrafikAufrufen("Gravity");
                                         }
                                     }
        );
        bttnProximity.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             sensorGrafikAufrufen("Proximity");
                                         }
                                     }
        );
        bttnMagnetfeld.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             sensorGrafikAufrufen("Magnetfeld");
                                         }
                                     }
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
                                                          samplingrate = samplingrate *100;
                                                          tvSamplingrate.setText("Samplingrate: "+samplingrate);
                                                          handler(samplingrate);
                                                      }
                                                  }
        );

    }

    @Override
    protected void onDestroy() {
        Sensorupdate.saveSensorwerte();
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
                }
            }
        }, i);
    }

    public void sensorGrafikAufrufen(String sens)
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
    }
}
