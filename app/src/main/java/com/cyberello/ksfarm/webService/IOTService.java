package com.cyberello.ksfarm.webService;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cyberello.global.CyberelloConstants;
import com.cyberello.ksfarm.data.KSFarmConstants;
import com.cyberello.ksfarm.util.KSFarmUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class IOTService {

    public static void getIOTData(Activity activity, WebServiceResultListener listener) {

        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET,
                        KSFarmConstants.IOT_WEB_SERVICE_URL,
                        null,
                        response -> {

                            if (listener != null) {
                                listener.processGetIOTDataResult(response);
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

    public static void getIOTMetaData(Activity activity, WebServiceResultListener listener) {

        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET,
                        KSFarmConstants.KS_FARM_IOT_META_URL,
                        null,
                        response -> {

                            if (listener != null) {
                                listener.processGetIOTMetaDataResult(response);
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

    public static void sendKSFarmWebServiceRequest(String jsonDataString, String type, Activity activity, WebServiceResultListener listener) {

        postData(jsonDataString, type, activity, listener);
    }

    private static void postData(String jsonDataString, String type, Activity activity, final WebServiceResultListener listener) {

        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        JSONObject jsonObject = KSFarmUtil.getJSONObject(jsonDataString, type);

        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST, KSFarmConstants.KS_FARM_WEB_SERVICE_URL, jsonObject,
                response -> {

                    try {

                        if (response.has("status")) {

                            String status = response.getString("status");

                            if (status.equals(CyberelloConstants.STATUS_CODE_ERROR)) {

                                if (null != listener) {
                                    listener.onErrorResponse(response.getString("status"), response.getString("message"));
                                }

                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (listener != null) {
                        listener.processPostDataResult(response);
                    }
                },
                error -> {

                    if (listener != null) {
                        listener.onErrorResponse(error.getMessage());
                    }
                }) {
            @Override
            public byte[] getBody() {

                return KSFarmUtil.getJsonDataBytes(jsonObject);
            }
        };

        requestQueue.add(jsonObjRequest);
        requestQueue.start();
    }

    public static void getWeatherData(Activity activity, WebServiceResultListener listener) {

        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        JSONObject jsonObject = KSFarmUtil.getJSONObject("", "getWeatherInfo()");

        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST, KSFarmConstants.KS_FARM_WEB_SERVICE_URL, jsonObject,
                response -> {

                    try {

                        if (response.has("status")) {

                            String status = response.getString("status");

                            if (status.equals(CyberelloConstants.STATUS_CODE_ERROR)) {

                                if (null != listener) {
                                    listener.onErrorResponse(response.getString("status"), response.getString("message"));
                                }

                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (listener != null) {
                        listener.processGetWeatherDataResult(response);
                    }
                },
                error -> {

                    if (listener != null) {
                        listener.onErrorResponse(error.getMessage());
                    }
                }) {
        };

        requestQueue.add(jsonObjRequest);
        requestQueue.start();
    }

    public interface WebServiceResultListener {

        void processPostDataResult(JSONObject response);

        void processGetIOTDataResult(JSONObject response);

        void processGetWeatherDataResult(JSONObject response);

        void onErrorResponse(String errorMessage);

        void onErrorResponse(String status, String message);

        void processGetIOTMetaDataResult(JSONObject response);
    }
}
