package com.cyberello.ksfarm.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.cyberello.ksfarm.KSFarmMeta;
import com.cyberello.ksfarm.R;
import com.cyberello.ksfarm.data.KSFarmConstants;
import com.cyberello.ksfarm.data.json.IOTJSON;
import com.cyberello.ksfarm.data.json.JSONDataWrapper;
import com.cyberello.ksfarm.data.json.OpenWeatherJSON;
import com.cyberello.ksfarm.webService.IOTControl;

import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.cyberello.ksfarm.data.json.IOTJSONWrapper;
import com.cyberello.ksfarm.data.json.IOTDataJSON;
import com.cyberello.ksfarm.util.KSFarmUtil;
import com.cyberello.ksfarm.util.QRCodeUtil;
import com.cyberello.ksfarm.webService.IOTService;
import com.cyberello.ksfarm.webService.OpenWeatherAPI;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, QRCodeUtil.QRCodeListener, IOTService.WebServiceResultListener, KSFarmUtil.MetaDataListener {

    private GestureDetectorCompat mDetector;
    private SharedPreferences sharedPreferences;
    private OpenWeatherJSON weatherJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDetector = new GestureDetectorCompat(this, this);
        mDetector.setOnDoubleTapListener(this);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_iot);

        weatherJSON = new OpenWeatherJSON();

        sharedPreferences = this.getSharedPreferences(KSFarmConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        KSFarmMeta.init(MainActivity.this, sharedPreferences);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.mainLonganQount) {

            showLonganQountScreen();
            return true;
        }

        return false;
    }

    private void showLonganQountScreen() {

        Intent intent = new Intent(getApplicationContext(), LonganQountActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (this.mDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(@NonNull MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTap(@NonNull MotionEvent motionEvent) {

        Handler handler = new Handler(Looper.myLooper());

        handler.postDelayed(() -> IOTService.getWeatherData(MainActivity.this, MainActivity.this), 200);

        handler = new Handler(Looper.myLooper());

        handler.postDelayed(() -> KSFarmUtil.getIOTMetaJSON(MainActivity.this), 200);

        return true;
    }

    @Override
    public boolean onDoubleTapEvent(@NonNull MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        String jsonDataString = KSFarmUtil.getLocalWeatherData(sharedPreferences);

        OpenWeatherAPI.makeJsonRequest(MainActivity.this);

        weatherJSON.setJsonString(jsonDataString);

        setSecondFloorBalconyTempData(weatherJSON);

        KSFarmUtil.getIOTMetaJSON(MainActivity.this);
    }

    @Override
    public void onBackPressed() {

    }

    public void processQRCodeString(String scannedText) {

        Toast.makeText(MainActivity.this, "Scanned: " + QRCodeUtil.getKSFarmQRString(scannedText), Toast.LENGTH_LONG).show();
    }

    @Override
    public void processPostDataResult(JSONObject response) {

        KSFarmUtil.setLocalIOTData(response.toString(), sharedPreferences);
    }

    @Override
    public void processGetIOTDataResult(JSONObject response) {

        IOTJSONWrapper iotJSONWrapper = KSFarmUtil.gson().fromJson(response.toString(), IOTJSONWrapper.class);

        KSFarmUtil.setLocalIOTData(response.toString(), sharedPreferences);

        Handler handler = new Handler(Looper.myLooper());

        handler.postDelayed(() -> iotJSONWrapper.iotJSONs.forEach(this::setIOTData), 200);
    }

    @Override
    public void processGetWeatherDataResult(JSONObject response) {

        JSONDataWrapper jsonDataWrapper = KSFarmUtil.gson().fromJson(response.toString(), JSONDataWrapper.class);

        String jsonDataString = jsonDataWrapper.getJsonData();

        KSFarmUtil.setLocalWeatherData(jsonDataString, sharedPreferences);

        weatherJSON.setJsonString(jsonDataString);

        setSecondFloorBalconyTempData(weatherJSON);
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

        KSFarmUtil.setIOTMetaData(response, MainActivity.this);
    }

    private void setIOTData(IOTJSON iotJSON) {

        KSFarmUtil.setIOTData(iotJSON);

        if (iotJSON.name.equals(KSFarmConstants.BED_SIDE_LAMP)) {

            runOnUiThread(() -> setLampData(iotJSON, findViewById(R.id.switchBedSideLamp)));

            return;
        }

        if (iotJSON.name.equals(KSFarmConstants.STANDING_DESK_LAMP)) {

            runOnUiThread(() -> setLampData(iotJSON, findViewById(R.id.switchStandingDeskLamp)));

            return;
        }

        if (iotJSON.name.equals(KSFarmConstants.DESK_LAMP)) {

            runOnUiThread(() -> setLampData(iotJSON, findViewById(R.id.switchDeskLamp)));

            return;
        }

        if (iotJSON.name.equals(KSFarmConstants.OVER_HEAD_DESK_LAMP)) {

            runOnUiThread(() -> setLampData(iotJSON, findViewById(R.id.switchOverHeadDeskLamp)));

            return;
        }

        if (iotJSON.name.equals(KSFarmConstants.BED_ROOM_AIR_CON)) {

            runOnUiThread(() -> setLampData(iotJSON, findViewById(R.id.switchAirCon)));
            return;
        }

        if (iotJSON.name.equals(KSFarmConstants.DESK_HATARI_FAN)) {

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

    @Override
    public boolean onDown(@NonNull MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(@NonNull MotionEvent motionEvent, @NonNull MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(@NonNull MotionEvent motionEvent, @NonNull MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent event) {

            IOTService.getIOTData(MainActivity.this, MainActivity.this);

            return true;
        }
    }
}