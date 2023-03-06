package com.cyberello.ksfarm;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import com.cyberello.global.CyberelloConstants;
import com.cyberello.ksfarm.data.KSFarmConstants;
import com.cyberello.ksfarm.data.LonganQount;
import com.cyberello.ksfarm.data.User;
import com.cyberello.ksfarm.webService.KSFarmWebService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class KSFarmMeta {

    private static LonganQount longanQount;

    private static Gson gson;

    private static User user;

    public static void init(Activity activity, SharedPreferences sharedPreferences) {

        user = new User("ksfarm1998@gmail.com", "Google", "", "");

        loadLocalLonganQount(sharedPreferences);

        if (activity.getString(R.string.environment).equals(CyberelloConstants.PROD_STRING)) {

            KSFarmConstants.SERVER_URL = activity.getString(R.string.prod_server_url);
        } else {

            KSFarmConstants.SERVER_URL = activity.getString(R.string.dev_server_url);
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
            longanQount.email = user.email;
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

    public static void resetZero(Location location) {

        longanQount().resetZero(location);
    }

    public static String getJsonDataString(String jsonDataString, String type) {

        return getJSONObject(jsonDataString, type).toString();
    }

    public static byte[] getJsonDataBytes(String jsonDataString, String type) {

        return getJsonDataString(jsonDataString, type).getBytes(StandardCharsets.UTF_8);
    }

    public static JSONObject getJSONObject(String jsonDataString, String type) {

        HashMap data = new HashMap();

        data.put("jsonData", jsonDataString);
        data.put("type", type);

        JSONObject jsonObject = new JSONObject(data);

        try {
            jsonObject.put("user", new JSONObject(gson().toJson(user)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static void getLonganQountDataFromWebService(Activity activity, KSFarmWebService.KSFarmWebServiceResultListener listener) {

        KSFarmWebService.sendRequest(gson().toJson(longanQount()), KSFarmConstants.TYPE_WEB_SERVICE_GET, activity, listener);

        longanQount = null;
    }

    public static void loadLocalLonganQount(SharedPreferences sharedPreferences) {

        LonganQount qount;

        String qountJSON = sharedPreferences.getString(KSFarmConstants.LONGAN_QOUNT_JSON, "");

        if (!qountJSON.isEmpty()) {

            qount = gson().fromJson(qountJSON, LonganQount.class);

            qount.email = user.email;
            longanQount = qount;
        }
    }

    public static void setLonganQountDataToWebService(Activity activity, KSFarmWebService.KSFarmWebServiceResultListener listener) {

        KSFarmWebService.sendRequest(gson().toJson(longanQount()), KSFarmConstants.TYPE_WEB_SERVICE_SET, activity, listener);
    }

    public interface KSFarmMetaListener {

        void saveLonganQountSuccess();
    }
}
