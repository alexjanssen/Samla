package com.example.alex.test;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Blackilli on 17.12.2017.
 */

public class Wegpunkt {
    public double Latitude;
    public double Longitude;

    public Wegpunkt(double latitude, double longitude){
        this.Latitude = latitude;
        this.Longitude = longitude;
    }

    public LatLng getLatLng(){
        return new LatLng(Latitude, Longitude);
    }
}
