package com.example.alex.test;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by denis on 28.07.2017.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageButton imgbttn_focus; // Ähnlich, wie bei GoogleMaps Button zum fokussieren
    private Button bttn_loc;
    Sensoren sens;
    LatLng latLng;

    boolean record = false;

    private ArrayList<Location> locationList = new ArrayList<>(); //Liste zum Speichern von Locations.
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sens = new Sensoren(this);

        init();//Aufruf der Initalisierungsmethode
        imgbttn_focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focus();
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
    }
    private void init()//Initalisierungsmethode
    {
        imgbttn_focus = (ImageButton) findViewById(R.id.imgbttn_focus);
        bttn_loc = (Button) findViewById(R.id.bttn_loc);
        onclick();
        Toast toast = Toast.makeText(getApplicationContext(), "Zum fokussieren der akuellen Position den Button unten rechts betätigen!", Toast.LENGTH_LONG);
        toast.show();
    }

    private void location() {
        //Automatisch generierter Permissioncheck.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(sens.getLocation()!=null){

                mMap.clear();
                mMap.addCircle(new CircleOptions()
                        .center(new LatLng(sens.getLocation().getLatitude(),sens.getLocation().getLongitude()))
                        .radius(1)
                        .strokeColor(Color.RED)
                        .fillColor(Color.BLUE));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(sens.getLocation().getLatitude(),sens.getLocation().getLongitude())));
                //mMap.setMinZoomPreference(17.0f);
            }
        }


    }//Methode, welche durch die AwarenessAPI die aktuelle Location ermittelt.



    private  void onclick()
    {


        bttn_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bttn_loc.getText() == "start")
                {
                    bttn_loc.setText("stop");
                    bttn_loc.invalidate();
                    record = true;
                    handler(1000);

                }
                else
                {
                    bttn_loc.setText("start");
                    record = false;
                }

            }
        }
        );

    }


    private void handler(final int i) // Methode, welche alle n Sekunden einen bestimmten Quelltext ausführt. (1000 = 1s)
    {
        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {

            //Hier werden die Berechnungen zur Strecke und Geschwindigkeit aufgerufen
            @Override
            public void run() {
                //TODO: abspeichern der Daten in die Datenbank
                if (record) {
                    ha.postDelayed(this, i); //1000 = 1s
                    location();
                }
            }
        }, i);
    }



    private void focus() // Methode welche
    {
        try {
            if (sens.getLocation() != null) {//Nur ausführen, falls ein Locationobjekt vorhanden ist.
                setLatLng(new LatLng(sens.getLocation().getLatitude(), sens.getLocation().getLongitude()));//Maps verwendet LatLng
            }
        }catch (Exception e)
        {
            //Fallls die Konvertierung des Locationobjekts fehl schlägt, soll der Fehler als Toast ausgegeben werden.
            Toast toast = Toast.makeText(getApplicationContext(), (CharSequence) e, Toast.LENGTH_LONG);
            toast.show();
        }
        if (latLng != null) {//Nur ausführen, falls ein LatLng vorhanden ist.
            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(getLatLng().latitude,getLatLng().longitude))
                    .radius(1)
                    .strokeColor(Color.RED)
                    .fillColor(Color.BLUE));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(getLatLng()));
            mMap.setMinZoomPreference(17.0f);
        }
    }


    //Berechnung der Entfernung zwischen zwei Punkten
    private double calcDist(double lat1, double lon1, double lat2, double lon2) {
        double dist = 0.0;
        double earth = 6371000;
        double lat = Math.toRadians(lat1 - lat2);
        double lng = Math.toRadians(lon1 - lon2);
        double a = Math.sin(lat/2) * Math.sin(lat/2) +
                Math.cos(Math.toRadians(lat2))
                        * Math.cos(Math.toRadians(lat1))
                        * Math.sin(lng/2)
                        * Math.sin(lng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        dist = earth * c;
        return (dist);
    }









    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }



}
