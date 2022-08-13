package com.cyberello.ksfarm.webService;

import android.app.Activity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cyberello.global.CyberelloConstants;

import java.nio.charset.StandardCharsets;

public class IOTControl {

    public static void setRelayState(String ipAddress, boolean isChecked, Activity activity, IOTControlResultListener listener) {

        String state = "off";

        if (isChecked) {
            state = "on";
        }

        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = "http://" + ipAddress + "/set?relay=1&state=" + state;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> updateIOTData(response, activity, listener), error -> {
        });

        queue.add(stringRequest);
    }

    public static void refreshIOTData(String ipAddress, Activity activity, IOTControlResultListener listener) {

        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = "http://" + ipAddress + "/json";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> updateIOTData(response, activity, listener), error -> {
        });

        queue.add(stringRequest);
    }

    private static void updateIOTData(String iotDataString, Activity activity, IOTControlResultListener listener) {

        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, CyberelloConstants.IOT_WEB_SERVICE_URL, response -> {
            if (listener != null && response.equals("200")) {
                listener.processIOTControlResult(iotDataString);
            }
        }, error -> {
            if (listener != null) {
                listener.onIOTControlErrorResponse(error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {

                return iotDataString == null ? null : iotDataString.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString;
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
                return null;
            }
        };

        requestQueue.add(stringRequest);
    }

    public interface IOTControlResultListener {

        void processIOTControlResult(String response);

        void onIOTControlErrorResponse(String errorMessage);
    }
}
