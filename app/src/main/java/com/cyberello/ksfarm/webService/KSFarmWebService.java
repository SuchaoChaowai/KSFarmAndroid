package com.cyberello.ksfarm.webService;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cyberello.global.CyberelloConstants;
import com.cyberello.ksfarm.KSFarmMeta;
import com.cyberello.ksfarm.data.KSFarmConstants;
import com.cyberello.ksfarm.util.KSFarmUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class KSFarmWebService {

    private static final String URL_PATTERN = "/ksfarmService";

    private static String url;

    public static String url() {

        if (null == url) {
            url = KSFarmConstants.SERVER_URL + URL_PATTERN;
        }

        return url;
    }

    public static void sendRequest(String jsonDataString, String type, Activity activity, KSFarmWebServiceResultListener listener) {

        postData(jsonDataString, type, activity, listener);
    }

    private static void postData(String jsonDataString, String type, Activity activity, final KSFarmWebServiceResultListener listener) {

        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        JSONObject jsonObject = KSFarmMeta.getJSONObject(jsonDataString, type);

        KSFarmUtil.log("url()", url());
        KSFarmUtil.log("type", type);
        KSFarmUtil.log("json", jsonObject.toString());

        byte[] jsonDataBytes = KSFarmMeta.getJsonDataBytes(jsonDataString, type);

        KSFarmUtil.log("jsonDataBytes", new String(jsonDataBytes));

        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST, url(), jsonObject,
                response -> {

                    try {

                        if (response.has("status")) {

                            String status = response.getString("status");

                            if (status.equals(CyberelloConstants.STATUS_CODE_ERROR)) {

                                if (null != listener) {
                                    listener.onErrorResponse(response.getString("status"), response.getString("message"));
                                }

                                KSFarmUtil.log("response error", response.toString());
                                return;
                            }
                        }
                    } catch (JSONException e) {

                        KSFarmUtil.log("JSONException", e.getMessage());
                    }

                    KSFarmUtil.log("response", response.toString());

                    if (listener != null) {
                        listener.processWebServiceResult(response);
                    }
                },
                error -> {

                    KSFarmUtil.log("error", error.getMessage() == null ? error.getMessage() : "Call web service error!");

                    if (listener != null) {
                        listener.onErrorResponse(error.getMessage());
                    }
                }) {
            @Override
            public byte[] getBody() {

                return jsonDataBytes;
            }
        };

        requestQueue.add(jsonObjRequest);
        requestQueue.start();
    }

    public interface KSFarmWebServiceResultListener {

        void processWebServiceResult(JSONObject response);

        void onErrorResponse(String errorMessage);

        void onErrorResponse(String status, String errorMessage);
    }
}