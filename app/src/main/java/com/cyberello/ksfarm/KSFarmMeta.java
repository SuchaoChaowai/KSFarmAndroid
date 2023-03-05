package com.cyberello.ksfarm;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.cyberello.global.CyberelloConstants;
import com.cyberello.ksfarm.data.KSFarmConstants;
import com.cyberello.ksfarm.data.LonganQount;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class KSFarmMeta {

    private static LonganQount longanQount;

    private static Gson gson;

    public static void init(SharedPreferences sharedPreferences) {

        LonganQount qount;

        String qountJSON = sharedPreferences.getString(KSFarmConstants.LONGAN_QOUNT_JSON, "");

        if (!qountJSON.isEmpty()) {

            qount = gson().fromJson(qountJSON, LonganQount.class);
            longanQount = qount;
        }
    }

    public static Gson gson() {

        if (null == gson) {
            gson = new GsonBuilder().setDateFormat(CyberelloConstants.SERVER_DATE_TIME_FORMAT_STRING)
                    .create();
        }

        return gson;
    }

    public static LonganQount longanQount() {

        if (longanQount == null) {
            longanQount = new LonganQount();
        }

        return longanQount;
    }

    public static void saveLonganQount(LonganQount qount, SharedPreferences sharedPreferences, KSFarmMetaListener listener) {

        longanQount = qount;

        Handler handler = new Handler(Looper.myLooper());

        handler.postDelayed(() -> {

            sharedPreferences.edit().putString(KSFarmConstants.LONGAN_QOUNT_JSON, getLonGanQountJSON()).apply();

            if (listener != null) listener.saveLonganQountSuccess();
        }, 200);
    }

    private static String getLonGanQountJSON() {

        return gson().toJson(longanQount);
    }

    private static LonganQount getLonGanQount(String longanQountJSON) {

        return gson().fromJson(longanQountJSON, LonganQount.class);
    }

    public interface KSFarmMetaListener {

        void saveLonganQountSuccess();
    }
}
