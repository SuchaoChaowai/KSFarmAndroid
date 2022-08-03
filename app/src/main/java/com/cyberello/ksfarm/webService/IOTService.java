package com.cyberello.ksfarm.webService;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class IOTService {

    public static void getIOTData(Activity activity, WebServiceResultListener listener) {

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        String url = "http://192.168.0.104:8080/KSFarm-Server/iotService";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET,
                        url,
                        null,
                        response -> {

                            if (listener != null) {
                                listener.processWebServiceResult(response);
                            }
                        },
                        error -> {

                            if (listener != null) {
                                listener.onErrorResponse(error.getMessage());
                            }
                        });

        requestQueue.add(jsonObjectRequest);
        requestQueue.start();
    }

    public interface WebServiceResultListener {

        void processWebServiceResult(JSONObject response);

        void onErrorResponse(String errorMessage);
    }
}
