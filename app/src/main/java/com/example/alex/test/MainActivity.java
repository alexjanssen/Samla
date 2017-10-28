package com.example.alex.test;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;

public class MainActivity extends AppCompatActivity {
    ArrayList<TextView> tvL1 = new ArrayList();
    ArrayList<TextView> tvL2 = new ArrayList();
    TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,tv10;
    TextView tv11,tv12,tv13,tv14,tv15,tv16,tv17,tv18,tv19,tv20;
    Button btn1;
    Button btn2;
    Sensoren sens;
    boolean record = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);
        tv6 = (TextView) findViewById(R.id.tv6);
        tv7 = (TextView) findViewById(R.id.tv7);
        tv8 = (TextView) findViewById(R.id.tv8);
        tv9 = (TextView) findViewById(R.id.tv9);
        tv10 = (TextView) findViewById(R.id.tv10);

        tv11 = (TextView) findViewById(R.id.tv11);
        tv12 = (TextView) findViewById(R.id.tv12);
        tv13 = (TextView) findViewById(R.id.tv13);
        tv14 = (TextView) findViewById(R.id.tv14);
        tv15 = (TextView) findViewById(R.id.tv15);
        tv16 = (TextView) findViewById(R.id.tv16);
        tv17 = (TextView) findViewById(R.id.tv17);
        tv18 = (TextView) findViewById(R.id.tv18);
        tv19 = (TextView) findViewById(R.id.tv19);
        tv20 = (TextView) findViewById(R.id.tv20);

        tvL1.add(tv1);tvL1.add(tv2);tvL1.add(tv3);tvL1.add(tv4);tvL1.add(tv5);
        tvL1.add(tv6);tvL1.add(tv7);tvL1.add(tv8);tvL1.add(tv9);tvL1.add(tv10);

        tvL2.add(tv11);tvL2.add(tv12);tvL2.add(tv13);tvL2.add(tv14);tvL2.add(tv15);
        tvL2.add(tv16);tvL2.add(tv17);tvL2.add(tv18);tvL2.add(tv19);tvL2.add(tv20);




        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);

        btn1.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             sens = new Sensoren(MainActivity.this);
                                             List<String> sl = sens.getAllSensorNames();
                                             for(int i =0;i<tvL1.size()&&i<sl.size();i++){
                                                 tvL1.get(i).setText(sl.get(i));
                                             }



                                         }
                                     }
        );

        btn2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (btn2.getText() == "start")
                                        {
                                            btn2.setText("stop");
                                            btn2.invalidate();
                                            record = true;
                                            handler(2000);
                                        }
                                        else
                                        {
                                            btn2.setText("start");
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

    private void handler(final int i){
        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //toDo
                //Log.d("Handler:", "running...");
                ha.postDelayed(this, i);
                if(record) {
                    ArrayList<String> sl = sens.getWerte();
                    for (int i = 0; i < sl.size(); i++) {
                        tvL2.get(i).setText(sl.get(i));
                    }


                }

            }
        },i);
    }
}
