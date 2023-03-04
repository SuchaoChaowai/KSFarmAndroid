package com.cyberello.ksfarm.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.cyberello.ksfarm.R;
import com.cyberello.ksfarm.data.KSConstants;
import com.cyberello.ksfarm.data.json.IOTJSON;
import com.cyberello.ksfarm.data.json.JSONDataWrapper;
import com.cyberello.ksfarm.data.json.OpenWeatherJSON;
import com.cyberello.ksfarm.webService.IOTControl;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.cyberello.ksfarm.data.json.IOTJSONWrapper;
import com.cyberello.ksfarm.data.json.IOTDataJSON;
import com.cyberello.ksfarm.util.KSFarmUtil;
import com.cyberello.ksfarm.util.QRCodeUtil;
import com.cyberello.ksfarm.webService.IOTService;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONObject;

import java.text.ParseException;

public class MainActivity extends AppCompatActivity implements QRCodeUtil.QRCodeListener, IOTService.WebServiceResultListener, KSFarmUtil.MetaDataListener {

    private GestureDetectorCompat mDetector;

    private SharedPreferences sharedPreferences;

    private OpenWeatherJSON weatherJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyGestureListener myGestureListener = new MyGestureListener();

        mDetector = new GestureDetectorCompat(this, myGestureListener);

        weatherJSON = new OpenWeatherJSON();

        sharedPreferences = this.getSharedPreferences(KSConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onResume() {
        super.onResume();

        String jsonDataString = KSFarmUtil.getLocalWeatherData(sharedPreferences);

        weatherJSON.setJsonString(jsonDataString);

        setSecondFloorBalconyTempData(weatherJSON);

        KSFarmUtil.getIOTMetaJSON(MainActivity.this);
    }

    public void processQRCodeString(String scannedText) {

        Toast.makeText(MainActivity.this, "Scanned: " + QRCodeUtil.getKSFarmQRString(scannedText), Toast.LENGTH_LONG).show();
    }

    @Override
    public void processPostDataResult(JSONObject response) {

    }

    @Override
    public void processGetIOTDataResult(JSONObject response) {

        IOTJSONWrapper iotJSONWrapper = KSFarmUtil.gson().fromJson(response.toString(), IOTJSONWrapper.class);

        KSFarmUtil.setLocalIOTData(response.toString(), sharedPreferences);

        new Thread(() -> iotJSONWrapper.iotJSONs.forEach(this::setIOTData)).start();
    }

    @Override
    public void processGetWeatherDataResult(JSONObject response) {

<<<<<<<< HEAD:app/src/main/java/com/cyberello/ksfarm/activity/MainActivity.java
        JSONDataWrapper jsonDataWrapper = KSFarmUtil.gson().fromJson(response.toString(), JSONDataWrapper.class);

        String jsonDataString = jsonDataWrapper.getJsonData();

        KSFarmUtil.setLocalWeatherData(jsonDataString, sharedPreferences);

        weatherJSON.setJsonString(jsonDataString);

        setSecondFloorBalconyTempData(weatherJSON);
========
        KSFarmUtil.setIOTMetaData(response, MainActivity.this);
>>>>>>>> origin/main:app/src/main/java/com/cyberello/ksfarm/MainActivity.java
    }

    @Override
    public void onErrorResponse(String errorMessage) {


    }

    @Override
    public void onErrorResponse(String status, String message) {

    }

    @Override
    public void processGetIOTMetaDataResult(JSONObject response) {

        KSFarmUtil.setLocalIOTData(response.toString(), sharedPreferences);
    }

    private void setIOTData(IOTJSON iotJSON) {

        KSFarmUtil.setIOTData(iotJSON);

        if (iotJSON.name.equals(KSConstants.BED_SIDE_LAMP)) {

            runOnUiThread(() -> setLampData(iotJSON, findViewById(R.id.switchBedSideLamp)));

            return;
        }

        if (iotJSON.name.equals(KSConstants.STANDING_DESK_LAMP)) {

            runOnUiThread(() -> setLampData(iotJSON, findViewById(R.id.switchStandingDeskLamp)));

            return;
        }

        if (iotJSON.name.equals(KSConstants.DESK_LAMP)) {

            runOnUiThread(() -> setLampData(iotJSON, findViewById(R.id.switchDeskLamp)));

            return;
        }

        if (iotJSON.name.equals(KSConstants.OVER_HEAD_DESK_LAMP)) {

            runOnUiThread(() -> setLampData(iotJSON, findViewById(R.id.switchOverHeadDeskLamp)));

            return;
        }

        if (iotJSON.name.equals(KSConstants.BED_ROOM_AIR_CON)) {

            runOnUiThread(() -> setLampData(iotJSON, findViewById(R.id.switchAirCon)));
            return;
        }

        if (iotJSON.name.equals(KSConstants.DESK_HATARI_FAN)) {

            runOnUiThread(() -> setLampData(iotJSON, findViewById(R.id.switchHatariFan)));
        }
    }

    private void setLampData(IOTJSON iotJSON, SwitchMaterial relaySwitch) {

        try {

            IOTDataJSON iotDataJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTDataJSON.class);

            relaySwitch.setChecked(iotDataJSON.relay1.equals("on"));

            relaySwitch.setOnCheckedChangeListener(
                    (buttonView, isChecked) -> IOTControl.setRelayState(iotJSON.deviceIP, isChecked, MainActivity.this, MainActivity.this));

        } catch (NumberFormatException nex) {
            nex.printStackTrace();
        }
    }

    private void setSecondFloorBalconyTempData(OpenWeatherJSON weatherJSON) {

        try {

            TextView textView = findViewById(R.id.textViewLastUpdate);

            String textString = "---";

            if (weatherJSON.dt() == 0) {

                textView.setText(textString);

                textView = findViewById(R.id.textViewTemp);
                textView.setText(textString);

                textView = findViewById(R.id.textViewHumid);
                textView.setText(textString);

                textView = findViewById(R.id.textViewPressure);
                textView.setText(textString);

                return;
            }

            try {
                textView.setText(KSFarmUtil.getServerDateTimeString(weatherJSON.dt()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            textView = findViewById(R.id.textViewTemp);

            textString = Math.round(weatherJSON.temperature()) + " °C";
            textView.setText(textString);

            textView = findViewById(R.id.textViewHumid);
            textString = Math.round(weatherJSON.humidity()) + " %";
            textView.setText(textString);

            textView = findViewById(R.id.textViewPressure);
            textString = String.valueOf(Math.round(weatherJSON.pressure()));

            if (textString.length() > 3) {

                textString = textString.charAt(0) + "," + textString.substring(1);
            }

            textString += " hPa";
            textView.setText(textString);
        } catch (NumberFormatException nex) {
            nex.printStackTrace();
        }
    }

    private void refreshIOTData(IOTJSON iotJSON) {

        IOTControl.refreshIOTData(iotJSON.deviceIP, this, this);
    }

    @Override
    public void metaDataReady() {

        IOTService.getWeatherData(MainActivity.this, MainActivity.this);

        IOTService.getIOTData(MainActivity.this, MainActivity.this);
    }

    @Override
    public void metaDataEmpty() {

        IOTService.getIOTMetaData(MainActivity.this, MainActivity.this);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent event) {

            IOTService.getIOTData(MainActivity.this, MainActivity.this);

            return true;
        }
    }
}