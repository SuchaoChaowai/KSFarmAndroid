package com.cyberello.ksfarm.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.cyberello.global.CyberelloConstants;
import com.cyberello.ksfarm.KSFarmMeta;
import com.cyberello.ksfarm.data.KSFarmConstants;
import com.cyberello.ksfarm.data.LonganQount;
import com.cyberello.ksfarm.data.json.IOTJSON;
import com.cyberello.ksfarm.data.json.IOTMetaJSON;
import com.cyberello.ksfarm.data.json.JSONDataWrapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

public class KSFarmUtil {

    private static Gson gson;
    private static IOTMetaJSON iotMetaJSON;
    private static final DecimalFormat numberFormatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
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

        return CyberelloConstants.SERVER_DATE_TIME_FORMAT.format(date);
    }

    public static String getDateString(Date date) {

        return CyberelloConstants.SIMPLE_DATE_FORMAT.format(date);
    }

    public static String getTimeString(Date date) {

        return CyberelloConstants.SIMPLE_TIME_FORMAT.format(date);
    }

    public static void beepPlus(Vibrator v) {

        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 250);

        if (v == null) return;

        v.vibrate(VibrationEffect.createOneShot(110L, VibrationEffect.DEFAULT_AMPLITUDE));
    }

    public static void speak(TextToSpeech textToSpeech, String text) {

        Handler handler = new Handler(Looper.myLooper());

        handler.postDelayed(() -> textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null), 100);
    }

    public static void toast(Activity activity, String toastText) {

        Toast.makeText(activity.getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
    }

    public static void setLocalIOTData(String iotJSONWrapperString, SharedPreferences sharedPreferences) {

        new Thread(() -> sharedPreferences.edit().putString(KSFarmConstants.IOT_JSON_WRAPPER, iotJSONWrapperString).apply()).start();
    }

    public static String getLocalIOTData(SharedPreferences sharedPreferences) {

        return sharedPreferences.getString(KSFarmConstants.IOT_JSON_WRAPPER, "");
    }

    public static void setLocalWeatherData(String jsonDataString, SharedPreferences sharedPreferences) {

        new Thread(() -> sharedPreferences.edit().putString(KSFarmConstants.WEATHER_DATA, jsonDataString).apply()).start();
    }

    public static String getLocalWeatherData(SharedPreferences sharedPreferences) {

        return sharedPreferences.getString(KSFarmConstants.WEATHER_DATA, "");
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

    public static int parseInt(String number) {

        if (null == number || number.isEmpty()) return 0;

        try {
            return Objects.requireNonNull(numberFormatter.parse(number)).intValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static String getCommaNumberFormat(int number) {

        if (number < 1000) {
            return Integer.toString(number);
        }

        numberFormatter.applyPattern("#,###");
        return numberFormatter.format(number);
    }

    public static void log(String tag, String message) {

        if (null == tag || null == message) {
            return;
        }

        new Thread(() -> Log.d(KSFarmConstants.LOG_TAG + ": " + tag, message)).start();
    }

    public static LonganQount getLonganQountFromJSONDataWrapper(JSONObject response) {

        JSONDataWrapper jsonDataWrapper = KSFarmMeta.gson().fromJson(String.valueOf(response), JSONDataWrapper.class);

        return KSFarmMeta.gson().fromJson(jsonDataWrapper.jsonData, LonganQount.class);
    }

    public static void hideSoftKeyboard(Activity activity) {

        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    public interface MetaDataListener {

        void metaDataReady();

        void metaDataEmpty();
    }
}
