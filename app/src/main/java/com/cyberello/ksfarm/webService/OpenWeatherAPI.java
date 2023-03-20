package com.cyberello.ksfarm.webService;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cyberello.ksfarm.data.KSFarmConstants;

import org.json.JSONObject;

public class OpenWeatherAPI {

    public static void getOpenWeatherData(Activity activity, OpenWeatherAPIListener listener) {

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                KSFarmConstants.OPEN_WEATHER_URL, null, response -> {

            try {

                listener.openWeatherDataReady(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {

        });

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(jsonObjReq);
        requestQueue.start();
    }

    public interface OpenWeatherAPIListener {

        void openWeatherDataReady(JSONObject openWeatherJsonObject);
    }
}
