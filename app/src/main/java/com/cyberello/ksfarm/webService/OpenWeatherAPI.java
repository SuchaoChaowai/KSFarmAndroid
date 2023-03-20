package com.cyberello.ksfarm.webService;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cyberello.ksfarm.data.KSFarmConstants;

import org.json.JSONArray;
import org.json.JSONObject;

public class OpenWeatherAPI {

    public static void makeJsonRequest(Activity activity) {

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                KSFarmConstants.OPEN_WEATHER_URL, null, response -> {

                    try {

                        JSONArray array = response.getJSONArray("weather");

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject weather = array.getJSONObject(i);

    //                        String main = weather.getString("main");
    //                        String desc = weather.getString("description");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {

                });

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(jsonObjReq);
        requestQueue.start();
    }
}
