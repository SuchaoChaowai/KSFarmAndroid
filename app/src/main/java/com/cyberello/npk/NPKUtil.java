package com.cyberello.npk;

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.cyberello.CyberelloConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class NPKUtil {

    private static Gson gson;

    public static int nitrogen;
    public static int potassium;
    public static int phosphorus;

    public static int n;
    public static int p;
    public static int k;

    private static final ArrayList<Integer> ns = new ArrayList<>();
    private static final ArrayList<Integer> ps = new ArrayList<>();
    private static final ArrayList<Integer> ks = new ArrayList<>();

    public static void init() {

        n = 100;
        p = 20;
        k = 100;
    }

    public static Gson gson() {

        if (null == gson) {
            gson = new GsonBuilder().setDateFormat(CyberelloConstants.SERVER_DATE_TIME_FORMAT_STRING).create();
        }

        return gson;
    }

    public static String getNPK_Sensor_URL(String ipAddress) {

        return "http://" + ipAddress + "/npk";
    }

    public static void toast(Activity activity, String toastText) {

        Toast.makeText(activity.getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
    }

    public static void setLocalNPKSensorURLData(String urlString, SharedPreferences sharedPreferences) {

        new Thread(() -> sharedPreferences.edit().putString(NPKConstants.NPK_SENSOR_URL_STRING, urlString).apply()).start();
    }

    public static String getLocalNPKSensorURLData(SharedPreferences sharedPreferences) {

        return sharedPreferences.getString(NPKConstants.NPK_SENSOR_URL_STRING, "");
    }

    public static void resetNPK(Activity activity) {

        nitrogen = 0;
        phosphorus = 0;
        potassium = 0;

        ns.clear();
        ps.clear();
        ks.clear();

        toast(activity, "NPK data reset!");
    }

    public static void setNPK(int n, int p, int k) {

        if (n > 0) {

            ns.add(n);
            nitrogen = average(ns);
        }
        if (p > 0) {

            ps.add(p);
            phosphorus = average(ps);
        }
        if (k > 0) {

            ks.add(k);
            potassium = average(ks);
        }
    }

    public static String getNPKString() {

        return nitrogen + ", " + phosphorus + ", " + potassium;
    }

    private static int average(ArrayList<Integer> npks) {

        int result = 0;

        for (int i = 0; i < npks.size(); i++) {
            result += npks.get(i);
        }

        result = result / npks.size();

        return result;
    }
}
