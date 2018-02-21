package de.hs_bochum.gruppe.die.gpssamla;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.renderscript.Float3;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.math.MathUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.util.Vector;

/**
 * Created by Blackilli on 18.02.2018.
 */

public class PositionProvider extends IntentService {
    private static final String CHANNEL_ID = "my_channel";
    private static final String NOTIFICATION_NAME = "PositionProviderNofitifaction";
    private static final int NOTIFICATION_ID = 1337;
    private static NotificationManager notificationManager;
    private static LocationManager locationManager;
    private static SensorManager sensorManager;
    private static HandlerThread handlerThread;
    private static Notification notification;
    //private static String locationProvider = LocationManager.NETWORK_PROVIDER;
    private static String locationProvider = LocationManager.GPS_PROVIDER;

    public enum Strategie {
        Periodisch,
        Distanzbasiert
    }

    private Strategie strategie;
    private boolean stillstandserkennung;
    private boolean geschwindigkeitsbasiert;
    private boolean geschwindigkeitserkennung;
    private double abstandSekunden, abstandMeter;
    private float geschwindigkeit;
    private float[] accWerte;
    private float stillstandThreshold = 1.0f;
    public static boolean running;
    private static boolean accRunning = false;

    private Logger logger;
    private RESTClient restClient;

    public PositionProvider() {
        super("PositionProvider");
    }

    SensorEventListener sensorEventListener = new SensorEventListener() {
        int p = 0;
        @Override
        public void onSensorChanged(SensorEvent event) {
            accWerte[p] = Math.abs(event.values[0]) + Math.abs(event.values[1]) + Math.abs(event.values[2]);
            accWerte[3] = (accWerte[0] + accWerte[1] + accWerte[2]) / 3;
            p = (p+1)%3;
            Log.d("Test", "AccWerte: " + accWerte[3]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        this.strategie = Strategie.valueOf(extras.getString("strategie"));
        boolean oldStillstand = this.stillstandserkennung;
        this.stillstandserkennung = extras.getBoolean("stillstandserkennung", false);
        this.geschwindigkeitsbasiert = extras.getBoolean("geschwindigkeitsbasiert", false);
        this.geschwindigkeitserkennung = extras.getBoolean("geschwindigkeitserkennung", false);
        this.abstandSekunden = extras.getDouble("abstandSekunden", 0.0);
        this.abstandMeter = extras.getDouble("abstandMeter", 0.0);
        this.geschwindigkeit = extras.getFloat("geschwindigkeit", 0.0f);
        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        if (stillstandserkennung && !accRunning) {
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
            accRunning = true;
        }
        else if (!stillstandserkennung) {
            sensorManager.unregisterListener(sensorEventListener);
            accRunning = false;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        int smallIcon = R.drawable.ic_my_location_black_24dp;
        String contentTitle = "GPSSamla";
        String contentText = "Empfangen: " + logger.receivedLocations().size() + " Gesendet: " + logger.sendLocations().size();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(notificationChannel);

            Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(smallIcon)
                    .setOngoing(true)
                    .setChannelId(CHANNEL_ID)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText);

            builder.setContentIntent(pendingIntent);
            notification = builder.build();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(smallIcon)
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_LOW)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText);

            builder.setContentIntent(pendingIntent);
            notification = builder.build();
        }

        notificationManager.notify(NOTIFICATION_ID, notification);
    }


    LocationListener periodicListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            logger.locationReceived(location);
            restClient.sendUpdate(location);
            showNotification();
            Log.d("PositionProvider", "Location erhalten: " + location.toString() + "\n" + location.getSpeed());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    LocationListener distanceListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            logger.locationReceived(location);
            if (logger.getLastSendLocation() == null || logger.getLastSendLocation().distanceTo(location) > abstandMeter)
                restClient.sendUpdate(location);
            showNotification();
            Log.d("PositionProvider", "Location erhalten: " + location.toString() + "\n" + location.getSpeed());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    LocationListener speedListener = new LocationListener() {
        @SuppressLint("MissingPermission")
        @Override

        public void onLocationChanged(Location location) {
            logger.locationReceived(location);
            Location lastLocation = logger.getLastSendLocation();
            Location lastLastLocation = logger.getLastSendLocation();
            if (lastLocation == null || lastLocation.distanceTo(location) > abstandMeter){
                restClient.sendUpdate(location);
                lastLocation = location;
            }
            showNotification();
            if (geschwindigkeitsbasiert && running) {
                try {
                    float speed = geschwindigkeit;
                    //50m bei 10m/s max -> min 5 sekunden warten
                    if(geschwindigkeitserkennung)
                        if(location.getSpeed() != 0.0f)
                            speed = location.getSpeed();
                        else
                            speed = lastLastLocation.distanceTo(location) / (((float)location.getTime() - (float)lastLastLocation.getTime()) / 1000.0f);
                    Log.e("PositionProvider", "Location erhalten. Geschwindigkeit: " + location.getSpeed());
                    //Toast.makeText(getApplicationContext(), "Location erhalten. Geschwindigkeit: " + location.getSpeed(), Toast.LENGTH_LONG).show();
                    mySleep((long)((abstandMeter - lastLocation.distanceTo(location)) / speed) * 1000L);

                    locationManager.requestSingleUpdate(locationProvider, speedListener, handlerThread.getLooper());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        /*
        public void onLocationChanged(Location location) {
            logger.locationReceived(location);
            Location lastLocation = logger.getLastSendLocation();
            Location lastLastLocation = logger.getLastSendLocation();
            if (lastLocation == null || lastLocation.distanceTo(location) > abstandMeter){
                restClient.sendUpdate(location);
                lastLocation = location;
            }
            showNotification();
            if (geschwindigkeitsbasiert && running) {
                try {
                    float speed = geschwindigkeit;

                    mySleep((long)((abstandMeter - lastLocation.distanceTo(location)) / speed) * 1000L);

                    locationManager.requestSingleUpdate(locationProvider, speedListener, handlerThread.getLooper());
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            for(String s : locationManager.getProviders(true))
                Log.e("PositionProvider", s);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            while (running) {
                if (strategie.equals(Strategie.Periodisch)){
                    Log.e("PositionProvider", "Starte Periodisch");
                    while(strategie.equals(Strategie.Periodisch) && running){
                        Log.d("PositionProvider", "request neue Location");
                        locationManager.requestSingleUpdate(locationProvider, periodicListener, handlerThread.getLooper());
                        Thread.sleep((long)(abstandSekunden * 1000));
                    }
                    locationManager.removeUpdates(periodicListener);
                } else if (strategie.equals(Strategie.Distanzbasiert) && !geschwindigkeitsbasiert){
                    Log.e("PositionProvider", "Starte Distanzbasiert");
                    while (strategie.equals(Strategie.Distanzbasiert) && running)
                    {
                        locationManager.requestSingleUpdate(locationProvider, distanceListener, handlerThread.getLooper());
                        mySleep(1000);
                    }
                    locationManager.removeUpdates(distanceListener);
                } else if (strategie.equals(Strategie.Distanzbasiert) && geschwindigkeitsbasiert){
                    Log.e("PositionProvider", "Starte Geschwindigkeitsbasiert");
                    while (strategie.equals(Strategie.Distanzbasiert) && geschwindigkeitsbasiert && running)
                    {
                        locationManager.requestSingleUpdate(locationProvider, speedListener, handlerThread.getLooper());
                        mySleep(1000);
                    }
                    locationManager.removeUpdates(speedListener);
                }
            }
        } catch (InterruptedException e) {
            locationManager.removeUpdates(speedListener);
            locationManager.removeUpdates(distanceListener);
            locationManager.removeUpdates(periodicListener);
            Thread.currentThread().interrupt();
        }
    }

    private void mySleep(long millis) throws InterruptedException {
        if (stillstandserkennung){
            long l = 0;
            while (l < millis){
                Thread.sleep(100);
                if (accWerte[3] > stillstandThreshold) {
                    l += 100;
                    Log.d("Stillstandserkennung", "Bewegung erkannt: acc: " + accWerte[3] + "   threshold: " + stillstandThreshold);
                }
            }
        } else Thread.sleep(millis);
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        logger = Logger.getInstance();
        restClient = RESTClient.getInstance();
        showNotification();
        startForeground(NOTIFICATION_ID, notification);
        running = true;
        accWerte = new float[4];
        handlerThread = new HandlerThread("handlerThread");
        handlerThread.start();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        running = false;
        Thread.currentThread().interrupt();
        stopForeground(true);
        stopSelf();
        handlerThread.quitSafely();
        logger = null;
        restClient = null;
        if (accRunning) sensorManager.unregisterListener(sensorEventListener);
        accRunning = false;
        super.onDestroy();
    }
}
