package de.hs_bochum.gruppe.die.gpssamla;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    RadioButton rbPeriodisch, rbDistanzbasiert;
    CheckBox cbEnergiersparend, cbStillstandserkennung, cbGeschwindigkeitsbasiert, cbGeschwindigkeitErkennen;
    LinearLayout llPeriodisch, llDistanzbasiert, llEnergiesparend, llGeschwindigkeitsbaiert;
    EditText etPeriodischAbstand, etDistanzbasiertAbstand, etMaximalgeschwindigkeit;
    Button bttnMap;
    String androidId;
    ToggleButton tbSammeln;
    PositionProvider positionProvider;
    RESTClient restClient;
    static Logger logger;
    boolean activateTB = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        llPeriodisch = findViewById(R.id.llPeriodisch);
        llDistanzbasiert = findViewById(R.id.llDistanzbasiert);
        llEnergiesparend = findViewById(R.id.llEnergiesparend);
        llGeschwindigkeitsbaiert = findViewById(R.id.llGeschwindigkeitsbasiert);


        rbPeriodisch = findViewById(R.id.rbPeriodisch);
        rbDistanzbasiert = findViewById(R.id.rbDistanzbasiert);


        cbEnergiersparend = findViewById(R.id.cbEnergiesparend);
        cbStillstandserkennung = findViewById(R.id.cbStillstandserkennung);
        cbGeschwindigkeitsbasiert = findViewById(R.id.cbGeschwindigkeitsbasiert);
        cbGeschwindigkeitErkennen = findViewById(R.id.cbGeschwindigkeitErkennen);


        bttnMap = findViewById(R.id.bttnMap);


        etPeriodischAbstand = findViewById(R.id.etPeriodischAbstand);
        etDistanzbasiertAbstand = findViewById(R.id.etDistanzbasiertAbstand);
        etMaximalgeschwindigkeit = findViewById(R.id.etMaximalgeschwindigkeit);


        tbSammeln = findViewById(R.id.tbSammeln);


        rbPeriodisch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            llPeriodisch.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            llDistanzbasiert.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            if (PositionProvider.running) stopPositionProvider();
        });

        rbDistanzbasiert.setOnCheckedChangeListener((buttonView, isChecked) -> {
            llDistanzbasiert.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            llPeriodisch.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            if (PositionProvider.running) stopPositionProvider();
        });

        cbEnergiersparend.setOnCheckedChangeListener((buttonView, isChecked) -> {
            llEnergiesparend.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (PositionProvider.running) stopPositionProvider();
        });

        cbStillstandserkennung.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (PositionProvider.running) stopPositionProvider();
        });

        cbGeschwindigkeitsbasiert.setOnCheckedChangeListener((buttonView, isChecked) -> {
            llGeschwindigkeitsbaiert.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (PositionProvider.running) stopPositionProvider();
        });

        cbGeschwindigkeitErkennen.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (PositionProvider.running) stopPositionProvider();
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (PositionProvider.running) stopPositionProvider();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        etPeriodischAbstand.addTextChangedListener(textWatcher);
        etMaximalgeschwindigkeit.addTextChangedListener(textWatcher);
        etDistanzbasiertAbstand.addTextChangedListener(textWatcher);

        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
            if(!hasFocus)
            {
                EditText et = (EditText)v;
                if (et.getText().toString().equals(""))
                    et.setText("0.01");
                if (Double.parseDouble(et.getText().toString()) < 0.0)
                    et.setText("0.01");
            }
        };

        etPeriodischAbstand.setOnFocusChangeListener(focusChangeListener);
        etMaximalgeschwindigkeit.setOnFocusChangeListener(focusChangeListener);
        etDistanzbasiertAbstand.setOnFocusChangeListener(focusChangeListener);


        tbSammeln.setChecked(PositionProvider.running);
        tbSammeln.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (activateTB) {
                if (isChecked) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                        }
                    } else {
                        startPositionProvider();
                    }
                } else
                    stopPositionProvider();
            }
        });

        llGeschwindigkeitsbaiert.setVisibility(View.GONE);
        llEnergiesparend.setVisibility(View.GONE);
        llDistanzbasiert.setVisibility(View.GONE);
        rbPeriodisch.setChecked(true);

        logger = Logger.getInstance(getApplicationContext());
        restClient = new RESTClient(getApplicationContext(), logger, androidId);

        bttnMap.setOnClickListener(v -> startActivity(new Intent(this, MapsActivity.class)));
    }

    private void stopPositionProvider(){
        stopService(new Intent(this, PositionProvider.class));
        activateTB = false;
        tbSammeln.setChecked(false);
        activateTB = true;
    }

    private void startPositionProvider(){
        Intent intent = new Intent(this, PositionProvider.class);
        Bundle extras = new Bundle();
        if (rbPeriodisch.isChecked()){
            extras.putString("strategie", PositionProvider.Strategie.Periodisch.name());
            extras.putDouble("abstandSekunden", Double.parseDouble(etPeriodischAbstand.getText().toString().replace(',', '.')));
        }
        if (rbDistanzbasiert.isChecked()) {
            extras.putString("strategie", PositionProvider.Strategie.Distanzbasiert.name());
            extras.putDouble("abstandMeter", Double.parseDouble(etDistanzbasiertAbstand.getText().toString().replace(',', '.')));
            extras.putBoolean("stillstandserkennung", cbStillstandserkennung.isChecked());
            if (cbGeschwindigkeitsbasiert.isChecked()){
                extras.putBoolean("geschwindigkeitsbasiert", true);
                extras.putFloat("geschwindigkeit", Float.parseFloat(etMaximalgeschwindigkeit.getText().toString().replace(',', '.')));
                extras.putBoolean("geschwindigkeitserkennung", cbGeschwindigkeitErkennen.isChecked());
            }
        }
        intent.putExtras(extras);
        startService(intent);
        activateTB = false;
        tbSammeln.setChecked(true);
        activateTB = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService(new Intent(this, PositionProvider.class));
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
