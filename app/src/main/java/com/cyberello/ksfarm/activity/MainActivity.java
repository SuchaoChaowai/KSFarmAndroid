package com.cyberello.ksfarm.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.cyberello.ksfarm.KSFarmMeta;
import com.cyberello.ksfarm.R;
import com.cyberello.ksfarm.data.KSFarmConstants;
import com.cyberello.ksfarm.data.json.IOTDataJSON;
import com.cyberello.ksfarm.data.json.IOTJSON;
import com.cyberello.ksfarm.data.json.IOTJSONWrapper;
import com.cyberello.ksfarm.util.KSFarmUtil;
import com.cyberello.ksfarm.util.QRCodeUtil;
import com.cyberello.ksfarm.webService.IOTControl;
import com.cyberello.ksfarm.webService.IOTService;
import com.cyberello.ksfarm.webService.OpenWeatherAPI;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, QRCodeUtil.QRCodeListener, IOTService.WebServiceResultListener, KSFarmUtil.MetaDataListener, OpenWeatherAPI.OpenWeatherAPIListener {

    private GestureDetectorCompat mDetector;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDetector = new GestureDetectorCompat(this, this);
        mDetector.setOnDoubleTapListener(this);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_iot);

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

        handler.postDelayed(() -> OpenWeatherAPI.getOpenWeatherData(MainActivity.this, MainActivity.this), 200);

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

        Handler handler = new Handler(Looper.myLooper());

        handler.postDelayed(() -> OpenWeatherAPI.getOpenWeatherData(MainActivity.this, MainActivity.this), 200);

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

    private void setSecondFloorBalconyWeatherData(JSONObject openWeatherJsonObject) {

        JSONArray array;

        TextView textView = findViewById(R.id.textViewMainWeather);

        try {

            array = openWeatherJsonObject.getJSONArray("weather");

            for (int i = 0; i < array.length(); i++) {

                JSONObject weather = array.getJSONObject(i);

                String main = weather.getString("main") + ", " + weather.getString("description");

                textView.setText(main);
            }

            JSONObject main = openWeatherJsonObject.getJSONObject("main");

            textView = findViewById(R.id.textViewTemp);

            String textString = Math.round(main.getDouble("temp")) + " °C";
            textView.setText(textString);

            textView = findViewById(R.id.textViewHumid);
            textString = Math.round(main.getDouble("humidity")) + " %";
            textView.setText(textString);

            textView = findViewById(R.id.textViewPressure);

            textString = Math.round(main.getDouble("pressure")) + " hPa";
            textView.setText(textString);

            JSONObject wind = openWeatherJsonObject.getJSONObject("wind");

            textString = "ลม" + KSFarmUtil.getWindDirection(wind.getDouble("deg"));

            textString = textString + ", " + Math.round(wind.getDouble("speed") * 3.6) + "-" + Math.round(wind.getDouble("gust") * 3.6) + " กม./ชม.";

            textView = findViewById(R.id.textViewWind);

            textView.setText(textString);

            JSONObject sun = openWeatherJsonObject.getJSONObject("sys");

            textString = "พระอาทิตย์  ";

            textString = textString + KSFarmUtil.getSunTime(sun.getLong("sunrise")) + "-" + KSFarmUtil.getSunTime(sun.getLong("sunset"));

            textView = findViewById(R.id.textViewSun);

            textView.setText(textString);

            try {

                textView = findViewById(R.id.textViewLastUpdate);
                textView.setText(KSFarmUtil.getServerDateTimeString(openWeatherJsonObject.getLong("dt")));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {


        }
    }

    @Override
    public void metaDataReady() {

        Handler handler = new Handler(Looper.myLooper());

        handler.postDelayed(() -> OpenWeatherAPI.getOpenWeatherData(MainActivity.this, MainActivity.this), 200);

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

    @Override
    public void openWeatherDataReady(JSONObject openWeatherJsonObject) {

        setSecondFloorBalconyWeatherData(openWeatherJsonObject);
    }
}