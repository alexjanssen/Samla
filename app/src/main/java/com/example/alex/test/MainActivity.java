package com.example.alex.test;

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
        sbSamplingrate.setMax(1000);
        sbSamplingrate.setProgress(50);

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
        tvSamplingrate.setText("Samplingrate: "+samplingrate);

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
                                                 handler(samplingrate);
                                             } else {
                                                 bttnStart.setText("start");
                                                 record = false;
                                             }
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
}
