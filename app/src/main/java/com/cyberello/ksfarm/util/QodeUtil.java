package com.cyberello.ksfarm.util;

import android.location.Location;

import com.cyberello.ksfarm.data.json.QodeJSON;

import java.util.Calendar;

public class QodeUtil {

    private static QodeJSON qodeJSON;

    public static QodeJSON getQodeJSON() {

        if (qodeJSON == null) {

            qodeJSON = new QodeJSON();
            setQodeJSONTime();
        }

        return qodeJSON;
    }

    public static QodeJSON setQR(String _qr) {

        qodeJSON = new QodeJSON();

        setQodeJSONTime();

        qodeJSON.qr = _qr;

        return qodeJSON;
    }

    public static void setLocation(Location location) {

        qodeJSON.latitude = location.getLatitude();
        qodeJSON.longitude = location.getLongitude();
        qodeJSON.accuracy = location.getAccuracy();
    }

    public static void setQodeJSONTime() {

        qodeJSON.time = Calendar.getInstance().getTimeInMillis();
    }
}
