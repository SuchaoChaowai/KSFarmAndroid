package com.cyberello.ksfarm.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.cyberello.global.CyberelloConstants;
import com.cyberello.ksfarm.data.KSConstants;
import com.cyberello.ksfarm.data.json.IOTMetaJSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Objects;

public class KSFarmUtil {

    private static Gson gson;
    private static IOTMetaJSON iotMetaJSON;

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

    public static String getDeviceName(String deviceId) {

        return iotMetaJSON.getDeviceName(deviceId);
    }

    public interface MetaDataListener {

        void metaDataReady();
        void metaDataEmpty();
    }
}
