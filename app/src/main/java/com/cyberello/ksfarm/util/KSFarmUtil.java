package com.cyberello.ksfarm.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import com.cyberello.global.CyberelloConstants;
import com.cyberello.ksfarm.data.KSConstants;
import com.cyberello.ksfarm.data.json.IOTJSON;
import com.cyberello.ksfarm.data.json.IOTMetaJSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

public class KSFarmUtil {

    private static Gson gson;
    private static IOTMetaJSON iotMetaJSON;
    private static final String SERVER_DATE_TIME_FORMAT = "yyyy-MM-DD'T'HH:mm:ss.SSS'Z'";
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String TIME_FORMAT = "kk:mm:ss";
    public static final SimpleDateFormat simpleServerDateTimeFormat = new SimpleDateFormat(SERVER_DATE_TIME_FORMAT, Locale.US);
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
    public static final SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.US);
    private static final ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
    public static final DecimalFormat dfLatLon = new DecimalFormat("0.00000");
    public static final DecimalFormat dfAltitude = new DecimalFormat("0");

    public static Gson gson() {

        if (null == gson) {
            gson = new GsonBuilder().setDateFormat(CyberelloConstants.SERVER_DATE_TIME_FORMAT_STRING)
                    .create();
        }

        return gson;
    }

    public static String getServerDateTimeString(long dateTime) throws ParseException {

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(dateTime * 1000);

        return CyberelloConstants.DATE_TIME_FORMAT.format(cal.getTime());
    }

    public static String getServerDateTimeString(Date date) {

        return simpleServerDateTimeFormat.format(date);
    }

    public static String getDateString(Date date) {

        return simpleDateFormat.format(date);
    }

    public static String getTimeString(Date date) {

        return simpleTimeFormat.format(date);
    }

    public static void beepPlus(Vibrator v) {

        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 250);

        if (v == null) return;

        v.vibrate(VibrationEffect.createOneShot(110L, VibrationEffect.DEFAULT_AMPLITUDE));
    }

    public static void beepMinus(Vibrator v) {

        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 250);

        Handler handler = new Handler(Looper.myLooper());

        handler.postDelayed(() -> toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 250), 200);

        if (v == null) return;

        v.vibrate(VibrationEffect.createOneShot(110L, VibrationEffect.DEFAULT_AMPLITUDE));

        Handler handlerVibrate = new Handler(Looper.myLooper());

        handlerVibrate.postDelayed(() -> v.vibrate(VibrationEffect.createOneShot(110L, VibrationEffect.DEFAULT_AMPLITUDE)),
                200);
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

    public static void setLocalWeatherData(String jsonDataString, SharedPreferences sharedPreferences) {

        new Thread(() -> sharedPreferences.edit().putString(KSConstants.WEATHER_DATA, jsonDataString).apply()).start();
    }

    public static String getLocalWeatherData(SharedPreferences sharedPreferences) {

        return sharedPreferences.getString(KSConstants.WEATHER_DATA, "");
    }

    public static JSONObject getJSONObject(String jsonDataString, String type) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("jsonData", jsonDataString);
            jsonObject.put("type", type);
            jsonObject.put("user", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static byte[] getJsonDataBytes(JSONObject jsonObject) {

        return jsonObject.toString().getBytes(StandardCharsets.UTF_8);
    }

    public static void getIOTMetaJSON(MetaDataListener listener) {

        if (iotMetaJSON != null) {
            listener.metaDataReady();
            return;
        }

        listener.metaDataEmpty();
    }

    public static void setIOTMetaData(JSONObject response, MetaDataListener listener) {

        iotMetaJSON = KSFarmUtil.gson().fromJson(response.toString(), IOTMetaJSON.class);

        listener.metaDataReady();
    }

    public static IOTMetaJSON.IOTDevice getDevice(String deviceId) {

        return iotMetaJSON.getDevice(deviceId);
    }

    public static void setIOTData(IOTJSON iotJSON) {

        IOTMetaJSON.IOTDevice device = getDevice(iotJSON.id);

        if (device == null) {

            iotJSON.name = "";
            return;
        }

        iotJSON.name = device.name;
    }

    public interface MetaDataListener {

        void metaDataReady();

        void metaDataEmpty();
    }
}
