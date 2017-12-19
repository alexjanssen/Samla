package com.example.alex.test;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class NewMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FrameLayout mapFrame;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker aktuellePositionMarker;
    private RequestQueue requestQueue;
    private ArrayList<Strecke> streckeArrayList = new ArrayList<>();
    private ArrayList<Session> sessionArrayList = new ArrayList<>();
    private Spinner spinnerStrecke, spinnerSessions;
    private LinkedList<Marker> streckenMarker = new LinkedList<>();
    private LinkedList<Circle> recordedPositions = new LinkedList<>();
    private Sensorupdate sensorupdate;
    private TextView tvNextcheckpoint, tvPositioncount;
    private ToggleButton bttnStartStop, bttnPauseContinue, bttnCDF;
    private Button bttnLoadSession;
    private LinearLayout llRecordData;
    private int nextCheckpoint = 1;
    private Gpssession gpssession;
    private Strecke aktuelleStrecke;
    private LocationRequest locationRequest;
    private GraphView graphCDF;

    public final static long LOCATION_INTERVAL = 1000;


    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            if (location != null) {
                aktuelleStrecke.aufgezeichneteWerte.get(nextCheckpoint - 1).add(new Sensorwert(System.nanoTime(), new double[]{location.getLatitude(), location.getLongitude(), 0.0f}));
                sensorupdate.addSensorWert(sensorupdate.TYPE_GPS, new Sensorwert(System.nanoTime(), new double[]{location.getLatitude(), location.getLongitude(), 0.0f}));
                recordedPositions.add(mMap.addCircle(new CircleOptions().center(new LatLng(location.getLatitude(), location.getLongitude())).radius(1.0)));
                updateRecordetWerteCount();
            }
        }
    };

    private void updateRecordetWerteCount(){
        tvPositioncount.setText(aktuelleStrecke.countAufgezeichneteWerte() + "");
    }


    private CompoundButton.OnCheckedChangeListener bttnPauseContinueListener = (compoundButton, isChecked) -> {
        if (isChecked){
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            increaseNextCheckpoint();
        } else {
            try {
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } catch (SecurityException ex) {}
        }
    };

    private CompoundButton.OnCheckedChangeListener bttnCDFListener = (compoundButton, isChecked) -> {
        if (isChecked){
            graphCDF.setVisibility(View.VISIBLE);
            mapFrame.setVisibility(View.GONE);
        } else {
            graphCDF.setVisibility(View.GONE);
            mapFrame.setVisibility(View.VISIBLE);
        }
    };

    private CompoundButton.OnCheckedChangeListener bttnStartStopListener = (CompoundButton compoundButton, boolean isChecked) -> {
        spinnerStrecke.setEnabled(!isChecked);
        bttnPauseContinue.setEnabled(isChecked);
        if (isChecked){
            for (Circle c : recordedPositions)
                c.remove();
            recordedPositions.clear();
            llRecordData.setVisibility(View.VISIBLE);
            //gpssession = new Gpssession("testsession", System.nanoTime(), sensorupdate.TYPE_GPS);
            try {

                locationRequest = LocationRequest.create();
                locationRequest.setInterval(LOCATION_INTERVAL);
                locationRequest.setMaxWaitTime(LOCATION_INTERVAL);
                locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } catch (SecurityException ex) {

            }

        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            aktuelleStrecke.saveStrecke("teststrecke11");
            //gpssession.endzeit = System.nanoTime();
            //gpssession.sendSession();
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
        graphCDF = findViewById(R.id.graphCDF);
        mapFrame = findViewById(R.id.mapFrame);
        bttnCDF = findViewById(R.id.bttnCDF);
        bttnCDF.setOnCheckedChangeListener(bttnCDFListener);
        bttnStartStop = findViewById(R.id.bttnStartStop);
        bttnStartStop.setOnCheckedChangeListener(bttnStartStopListener);
        bttnPauseContinue = findViewById(R.id.bttnPauseContinue);
        bttnPauseContinue.setEnabled(false);
        tvNextcheckpoint = findViewById(R.id.tvNextcheckpoint);
        tvPositioncount = findViewById(R.id.tvPositioncount);
        spinnerStrecke = findViewById(R.id.spinnerStrecke);
        spinnerSessions = findViewById(R.id.spinnerSessions);
        llRecordData = findViewById(R.id.llRecordData);
        llRecordData.setVisibility(View.GONE);
        bttnLoadSession = findViewById(R.id.bttnLoadSession);
        bttnLoadSession.setOnClickListener(view -> {
            for (Circle c : recordedPositions)
                c.remove();
            Session session = (Session) spinnerSessions.getSelectedItem();
            requestQueue.add(new JsonObjectRequest(Request.Method.GET, "http://blackilli.de/session?id=" + session.sessionid, null, response -> {
                try {
                    JSONArray jsonTeilstrecken = response.getJSONArray("Teilstrecken");
                    for (int i = 0; i < jsonTeilstrecken.length(); i++)
                    {
                        aktuelleStrecke.aufgezeichneteWerte.set(i, new LinkedList<>());
                        JSONArray jsonPositionen = jsonTeilstrecken.getJSONObject(i).getJSONArray("Positionen");
                        for (int j = 0; j < jsonPositionen.length(); j++){
                            JSONObject position = jsonPositionen.getJSONObject(j);
                            long timestamp = position.getLong("Timestamp");
                            double[] values = new double[3];
                            JSONArray jsonValues = position.getJSONArray("Values");
                            for (int k = 0; k < jsonValues.length(); k++)
                                values[k] = jsonValues.getDouble(k);
                            aktuelleStrecke.aufgezeichneteWerte.get(i).add(new Sensorwert(timestamp, values));
                            recordedPositions.add(mMap.addCircle(new CircleOptions().radius(1.0).center(new LatLng(values[0], values[1]))));
                        }
                    }

                    LatLng center = null;
                    LinkedList<LinkedList<Sensorwert>> interpolierteWerte = aktuelleStrecke.interpolierteWerte();
                    for (int i = 0; i < interpolierteWerte.size(); i++){
                        for (int j = 0; j < interpolierteWerte.get(i).size(); j++)
                        {
                            center = interpolierteWerte.get(i).get(j).getLatLng();
                            //recordedPositions.add(mMap.addCircle(new CircleOptions().radius(1.0).center(center)));
                            recordedPositions.add(mMap.addCircle(new CircleOptions().radius(1.0).center(center).strokeColor(Color.BLUE)));
                            Log.d("Markiere InterpolWerte", "Aufgenommener Sensorwert: Lat: " + interpolierteWerte.get(i).get(j).getValues()[0] + "     Lon: " + interpolierteWerte.get(i).get(j).getValues()[1]);
                            Log.d("Markiere InterpolWerte", "Center: Lat: " + center.latitude + "     Lon: " + center.longitude);
                        }
                    }
                    if (center != null)
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(center));

                    PointsGraphSeries<DataPoint> pointPointsGraphSeries = new PointsGraphSeries<>();
                    //LineGraphSeries<DataPoint> pointPointsGraphSeries = new LineGraphSeries<>();
                    LinkedList<Float> diffs = aktuelleStrecke.getAllDiffs();
                    for (int i =  0; i < diffs.size(); i++){
                        if (i + 1 != diffs.size())
                            if (diffs.get(i).equals(diffs.get(i+1)))
                                continue;
                        pointPointsGraphSeries.appendData(new DataPoint(diffs.get(i), (double) 100 / (double) diffs.size() * (double) (i + 1)), false, diffs.size());
                        Log.e("GraphView", "x: " + diffs.get(i) + "    y: " + ((double) 100 / (double) diffs.size() * (double) (i + 1)));
                    }
                    pointPointsGraphSeries.setSize(5.0f);
                    graphCDF.getViewport().setMaxX(diffs.getLast());
                    graphCDF.getViewport().setMaxY(100.0);
                    graphCDF.getViewport().setXAxisBoundsManual(true);
                    graphCDF.getViewport().setYAxisBoundsManual(true);
                    graphCDF.getSeries().clear();
                    graphCDF.addSeries(pointPointsGraphSeries);

                    //TODO An dieser stelle kann mit aktuelleStrecke.aufgezeichneteWerteDif() die differenzen durchgegangen werden
                    //aktuelleStrecke.aufgezeichneteWerte               Liste der Streckenabschnitte
                    //aktuelleStrecke.aufgezeichneteWerte.get(1)        Liste der Werte pro Streckenabschnitt
                    //aktuelleStrecke.aufgezeichneteWerte.get(1).get(1) Einzelnes Sensorwertobjekt

                    //aktuelleStrecke.interpolierteWerte()                  Hat die gleiche Struktur wie aktuelleStrecke.aufgezeichneteWerte
                    //aktuelleStrecke.interpolierteWerte().get(1).get(1)    Interpolierter Sensorwert

                    //aktuelleStrecke.aufgezeichneteWerteDif()                  Hat wieder die gleiche Struktur
                    //aktuelleStrecke.aufgezeichneteWerteDif().get(1).get(1)    Abstand zwischen echtem und interpolierten Sensorwert

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {}));
        });
        bttnPauseContinue.setOnCheckedChangeListener(bttnPauseContinueListener);
        fusedLocationProviderClient = new FusedLocationProviderClient(this);
        sensorupdate = Sensorupdate.getInstance();
        requestQueue = sensorupdate.requestQueue;
        requestQueue.add(new JsonArrayRequest(Request.Method.GET, "http://blackilli.de/session", null, response -> {
            for (int i = 0; i < response.length(); i++){
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    sessionArrayList.add(new Session(jsonObject.getInt("Sessionid"), jsonObject.getString("Bezeichnung")));
                    spinnerSessions.setAdapter(new ArrayAdapter<Session>(this, android.R.layout.simple_spinner_dropdown_item, sessionArrayList));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {}));
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
                aktuelleStrecke = selectedStrecke;
                for (int j = 0; j < selectedStrecke.wegpunkte.size(); j++){
                    Wegpunkt wegpunkt = selectedStrecke.wegpunkte.get(j);
                    streckenMarker.add(mMap.addMarker(new MarkerOptions().title(j + "").position(wegpunkt.getLatLng())));
                }
            }
        });

        requestQueue = sensorupdate.requestQueue;
        requestQueue.add(new JsonArrayRequest(Request.Method.GET, "http://blackilli.de/gpssession", null, response -> {
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonStrecke = response.getJSONObject(i);
                    Strecke strecke = new Strecke(jsonStrecke.getInt("Id"), jsonStrecke.getString("Bezeichung"));
                    for (int j = 0; j < jsonStrecke.getJSONArray("Wegpunkte").length(); j++) {
                        JSONObject jsonWegpunkt = jsonStrecke.getJSONArray("Wegpunkte").getJSONObject(j);
                        strecke.addWegpunkt(new Wegpunkt(jsonWegpunkt.getDouble("Latitude"), jsonWegpunkt.getDouble("Longitude")));
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
            if(aktuellePosition != null) {
                if (aktuellePositionMarker == null)
                    aktuellePositionMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(aktuellePosition.getLatitude(), aktuellePosition.getLongitude())).title("Aktuelle Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                aktuellePositionMarker.setPosition(new LatLng(aktuellePosition.getLatitude(), aktuellePosition.getLongitude()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(aktuellePosition.getLatitude(), aktuellePosition.getLongitude())));
                mMap.setMinZoomPreference(17.0f);
            }
        });
    }
}
