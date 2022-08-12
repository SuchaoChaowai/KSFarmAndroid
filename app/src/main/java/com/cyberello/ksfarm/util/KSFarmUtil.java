package com.cyberello.ksfarm.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.cyberello.global.CyberelloConstants;
import com.cyberello.ksfarm.data.KSConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.util.Objects;

public class KSFarmUtil {

    private static Gson gson;

    public static Gson gson() {

        if (null == gson) {
            gson = new GsonBuilder().setDateFormat(CyberelloConstants.SERVER_DATE_TIME_FORMAT_STRING)
                    .create();
        }

        return gson;
    }

    public static String getServerDateTimeString(String dateTimeString) throws ParseException {

        return CyberelloConstants.DATE_TIME_FORMAT.format(Objects.requireNonNull(CyberelloConstants.SERVER_DATE_TIME_FORMAT.parse(dateTimeString)));
    }

    public static void toast(Activity activity, String toastText) {

        Toast.makeText(activity.getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
    }

    public static void setLocalIOTData(String iotJSONWrapperString, SharedPreferences sharedPreferences) {

        new Thread(() -> sharedPreferences.edit().putString(KSConstants.IOT_JSON_WRAPPER, iotJSONWrapperString).apply()).start();
    }

    public static String getLocalIOTData(SharedPreferences sharedPreferences) {

        return sharedPreferences.getString(KSConstants.IOT_JSON_WRAPPER, "");
    }
}
