package com.cyberello.ksfarm.data;

import android.location.Location;

import com.cyberello.global.CyberelloConstants;
import com.cyberello.ksfarm.util.KSFarmUtil;

import java.util.Calendar;
import java.util.Date;

public class LonganQount {

    public String email;
    public String lastUpdateDateTimeString;
    public Date lastUpdate;
    public String createDateTimeString;
    public String name = "";
    private int numberFlower;
    private int numberNonFlower;
    public double latitude;
    public double longitude;
    public double altitude;
    public float accuracy;
    public float bearing;
    public String text = "";
    public boolean isHTML;
    public String event = "";
    public String json = "";
    public String statusCode;
    public String lastUpdateDateString;
    public String lastUpdateTimeString;

    public LonganQount() {

        setCreateDateTimeString();
        setLastUpdateString();
        statusCode = CyberelloConstants.STATUS_CODE_NEW;
    }

    public void setCreateDateTimeString() {
        createDateTimeString = KSFarmUtil.getServerDateTimeString(new Date());
    }

    public void setLastUpdateString() {

        Date currentDate = Calendar.getInstance().getTime();

        lastUpdateDateString = KSFarmUtil.getDateString(currentDate);
        lastUpdateTimeString = KSFarmUtil.getTimeString(currentDate);
        lastUpdateDateTimeString = KSFarmUtil.getServerDateTimeString(currentDate);
        statusCode = CyberelloConstants.STATUS_CODE_ACTIVE;

        lastUpdate = null;
    }

    public void setLocation(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = location.getAltitude();
        accuracy = location.getAccuracy();
        bearing = location.getBearing();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;

        LonganQount qount = (LonganQount) o;

        return this.lastUpdateDateTimeString.equals(qount.lastUpdateDateTimeString);
    }

    public int getNumberFlower() {

        return numberFlower;
    }

    public void addNumberFlower(Location location) {

        numberFlower = numberFlower + 1;

        event = "fa";

        setLocation(location);

        setLastUpdateString();
    }

    public void minusNumberFlower(Location location) {

        numberFlower = numberFlower - 1;

        event = "fr";

        setLocation(location);

        setLastUpdateString();
    }

    public int getNumberNonFlower() {

        return numberNonFlower;
    }

    public void addNumberNonFlower(Location location) {

        numberNonFlower = numberNonFlower + 1;

        event = "na";

        setLocation(location);

        setLastUpdateString();
    }

    public void minusNumberNonFlower(Location location) {

        numberNonFlower = numberNonFlower - 1;

        event = "nr";

        setLocation(location);

        setLastUpdateString();
    }

    public void resetZero(Location location) {

        numberNonFlower = 0;
        numberFlower = 0;

        event = "0";

        setLocation(location);

        setLastUpdateString();
    }
}
