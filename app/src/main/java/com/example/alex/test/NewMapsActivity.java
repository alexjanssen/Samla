package com.example.alex.test;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;

public class NewMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker aktuellePositionMarker;
    private RequestQueue requestQueue;
    private ArrayList<Strecke> streckeArrayList = new ArrayList<>();
    private Spinner spinnerStrecke;
    private LinkedList<Marker> streckenMarker = new LinkedList<>();
    private Sensorupdate sensorupdate = Sensorupdate.getInstance();
    private TextView tvNextcheckpoint, tvPositioncount;
    private ToggleButton bttnStartStop, bttnPauseContinue;
    private LinearLayout llRecordData;
    private int nextCheckpoint = 1;
    private Gpssession gpssession;

    public final static long LOCATION_INTERVAL = 2000;


    private CompoundButton.OnCheckedChangeListener bttnPauseContinueListener = (compoundButton, isChecked) -> {
        if (isChecked){
            increaseNextCheckpoint();
        } else {

        }
    };

    private CompoundButton.OnCheckedChangeListener bttnStartStopListener = (CompoundButton compoundButton, boolean isChecked) -> {
        spinnerStrecke.setEnabled(!isChecked);
        bttnPauseContinue.setEnabled(isChecked);
        if (isChecked){
            llRecordData.setVisibility(View.VISIBLE);
            gpssession = new Gpssession("testsession", System.nanoTime(), sensorupdate.TYPE_GPS);
            try {

                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setInterval(LOCATION_INTERVAL);
                locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

                fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        Location location = locationResult.getLastLocation();
                        if (location != null)
                            sensorupdate.addSensorWert(sensorupdate.TYPE_GPS, new Sensorwert(location.getTime(), new float[]{(float) location.getLatitude(), (float) location.getLongitude(), 0.0f}));
                    }
                }, null);
            } catch (SecurityException ex) {

            }

        } else {
            gpssession.endzeit = System.nanoTime();
            gpssession.sendSession();
            sensorupdate.saveSensorwerte();
            llRecordData.setVisibility(View.GONE);
            bttnPauseContinue.setOnCheckedChangeListener(null);
            bttnPauseContinue.setChecked(false);
            bttnPauseContinue.setOnCheckedChangeListener(bttnPauseContinueListener);
            resetNextCheckpoint();
        }
    };

    private void increaseNextCheckpoint(){
        nextCheckpoint++;
        tvNextcheckpoint.setText(nextCheckpoint + "");
    }

    private void resetNextCheckpoint(){
        nextCheckpoint = 1;
        tvNextcheckpoint.setText(nextCheckpoint + "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        bttnStartStop = findViewById(R.id.bttnStartStop);
        bttnStartStop.setOnCheckedChangeListener(bttnStartStopListener);
        bttnPauseContinue = findViewById(R.id.bttnPauseContinue);
        bttnPauseContinue.setEnabled(false);
        tvNextcheckpoint = findViewById(R.id.tvNextcheckpoint);
        tvPositioncount = findViewById(R.id.tvPositioncount);
        spinnerStrecke = findViewById(R.id.spinnerStrecke);
        llRecordData = findViewById(R.id.llRecordData);
        llRecordData.setVisibility(View.GONE);
        fusedLocationProviderClient = new FusedLocationProviderClient(this);

        requestQueue = sensorupdate.requestQueue;
        requestQueue.add(new JsonArrayRequest(Request.Method.GET, "http://blackilli.de/gpssession", null, response -> {
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonStrecke = response.getJSONObject(i);
                    Strecke strecke = new Strecke(jsonStrecke.getInt("Id"), jsonStrecke.getString("Bezeichung"));
                    for (int j = 0; j < jsonStrecke.getJSONArray("Wegpunkte").length(); j++) {
                        JSONObject jsonWegpunkt = jsonStrecke.getJSONArray("Wegpunkte").getJSONObject(j);
                        strecke.wegpunkte.add(new Wegpunkt(jsonWegpunkt.getDouble("Latitude"), jsonWegpunkt.getDouble("Longitude")));
                    }
                    streckeArrayList.add(strecke);
                }
                ArrayAdapter<Strecke> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, streckeArrayList);
                spinnerStrecke.setAdapter(arrayAdapter);
                Log.e("RESPONSE", response.toString());
            } catch (JSONException ex) {
                Log.e("MapActivityInit", "Fehler beim parsen des StreckenJSON", ex);
            }
        }
                , error -> Log.e("RESPONSE", error.toString())));


    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        spinnerStrecke.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                for (Marker marker : streckenMarker)
                    marker.remove();
                streckenMarker.clear();
            }

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                for (Marker marker : streckenMarker)
                    marker.remove();
                streckenMarker.clear();

                Strecke selectedStrecke = (Strecke) adapterView.getItemAtPosition(i);
                for (int j = 0; j < selectedStrecke.wegpunkte.size(); j++){
                    Wegpunkt wegpunkt = selectedStrecke.wegpunkte.get(j);
                    streckenMarker.add(mMap.addMarker(new MarkerOptions().title(j + "").position(wegpunkt.getLatLng())));
                }
            }
        });

        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            Location aktuellePosition = task.getResult();
            if (aktuellePositionMarker == null)
                aktuellePositionMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(aktuellePosition.getLatitude(), aktuellePosition.getLongitude())).title("Aktuelle Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            aktuellePositionMarker.setPosition(new LatLng(aktuellePosition.getLatitude(), aktuellePosition.getLongitude()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(aktuellePosition.getLatitude(), aktuellePosition.getLongitude())));
            mMap.setMinZoomPreference(17.0f);
        });
    }
}
