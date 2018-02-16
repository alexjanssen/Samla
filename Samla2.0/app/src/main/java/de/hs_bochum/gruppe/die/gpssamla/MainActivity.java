package de.hs_bochum.gruppe.die.gpssamla;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity {

    RadioButton rbPeriodisch, rbDistanzbasiert;
    CheckBox cbEnergiersparend, cbStillstandserkennung, cbGeschwindigkeitsbasiert, cbGeschwindigkeitErkennen;
    LinearLayout llPeriodisch, llDistanzbasiert, llEnergiesparend, llGeschwindigkeitsbaiert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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


        rbPeriodisch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            llPeriodisch.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            llDistanzbasiert.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        });

        rbDistanzbasiert.setOnCheckedChangeListener((buttonView, isChecked) -> {
            llDistanzbasiert.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            llPeriodisch.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        });

        cbEnergiersparend.setOnCheckedChangeListener((buttonView, isChecked) -> {
            llEnergiesparend.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        cbStillstandserkennung.setOnCheckedChangeListener((buttonView, isChecked) -> {
        });

        cbGeschwindigkeitsbasiert.setOnCheckedChangeListener((buttonView, isChecked) -> {
            llGeschwindigkeitsbaiert.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        cbGeschwindigkeitErkennen.setOnCheckedChangeListener((buttonView, isChecked) -> {
        });


        llGeschwindigkeitsbaiert.setVisibility(View.GONE);
        llEnergiesparend.setVisibility(View.GONE);
        llDistanzbasiert.setVisibility(View.GONE);
        rbPeriodisch.setChecked(true);
    }
}
