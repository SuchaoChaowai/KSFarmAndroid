package com.cyberello.npk;

import android.location.Location;

public class NPK_JSON {

    public long time;
    public double latitude;
    public double longitude;
    public double altitude;
    public float accuracy;

    public int nitrogen;
    public int phosphorous;
    public int potassium;

    public void setLocation(Location location) {

        time = location.getTime();
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = location.getAltitude();
        accuracy = location.getAccuracy();
    }

    public void setNPK(int _nitrogen, int _phosphorous, int _potassium) {

        nitrogen = _nitrogen;
        phosphorous = _phosphorous;
        potassium = _potassium;
    }
}
