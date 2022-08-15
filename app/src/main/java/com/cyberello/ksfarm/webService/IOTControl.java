package com.cyberello.ksfarm.webService;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class IOTControl {

    public static void setRelayState(String ipAddress, boolean isChecked, Activity activity, IOTService.WebServiceResultListener listener) {

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

    public static void refreshIOTData(String ipAddress, Activity activity, IOTService.WebServiceResultListener listener) {

        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = "http://" + ipAddress + "/json";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> updateIOTData(response, activity, listener), error -> {
        });

        queue.add(stringRequest);
    }

    private static void updateIOTData(String iotDataString, Activity activity, IOTService.WebServiceResultListener listener) {

        IOTService.sendKSFarmWebServiceRequest(iotDataString, "updateIOTData()", activity, listener);
    }
}
