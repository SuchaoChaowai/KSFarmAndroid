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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.cyberello.ksfarm.KSFarmMeta;
import com.cyberello.ksfarm.R;
import com.cyberello.ksfarm.data.KSFarmConstants;
import com.cyberello.ksfarm.data.LonganQount;
import com.cyberello.ksfarm.util.KSFarmUtil;
import com.cyberello.ksfarm.util.QRCodeUtil;
import com.cyberello.ksfarm.webService.KSFarmWebService;
import com.cyberello.ksfarm.webService.OpenWeatherAPI;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, QRCodeUtil.QRCodeListener, KSFarmUtil.MetaDataListener, OpenWeatherAPI.OpenWeatherAPIListener, KSFarmWebService.KSFarmWebServiceResultListener {

    private GestureDetectorCompat mDetector;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDetector = new GestureDetectorCompat(this, this);
        mDetector.setOnDoubleTapListener(this);

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

        getWebserviceData();

        return true;
    }

    @Override
    public boolean onDoubleTapEvent(@NonNull MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        getWebserviceData();

        setIOTElementsVisible(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {

    }

    public void processQRCodeString(String scannedText) {

        Toast.makeText(MainActivity.this, "Scanned: " + QRCodeUtil.getKSFarmQRString(scannedText), Toast.LENGTH_LONG).show();
    }

    private void setWeatherData(JSONObject openWeatherJsonObject) {

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
    }

    @Override
    public void metaDataEmpty() {

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

        setWeatherData(openWeatherJsonObject);
    }

    @Override
    public void processWebServiceResult(JSONObject response) {

        LonganQount longanQount = KSFarmUtil.getLonganQountFromJSONDataWrapper(response);

        if (longanQount == null) {
            return;
        }

        KSFarmMeta.saveLonganQount(longanQount, sharedPreferences, null);

        showQount();
    }

    private void showQount() {

        LonganQount longanQount = KSFarmMeta.longanQount();

        String text = KSFarmUtil.getCommaNumberFormat(longanQount.getNumberFlower() * 80) + " ตัน";

        TextView textView = findViewById(R.id.textViewMainTotalWeight);

        textView.setText(text);

        text = KSFarmUtil.getCommaNumberFormat(longanQount.getNumberFlower() * 80 * 15) + " บาท";

        textView = findViewById(R.id.textViewMainTotalSell);

        textView.setText(text);

        text = KSFarmUtil.getCommaNumberFormat(longanQount.getNumberFlower()) + " ต้น";

        textView = findViewById(R.id.textViewMainTotalFlower);

        textView.setText(text);
    }

    @Override
    public void onErrorResponse(String errorMessage) {

    }

    @Override
    public void onErrorResponse(String status, String errorMessage) {

    }


    private void getWebserviceData() {

        Handler handler = new Handler(Looper.myLooper());

        handler.postDelayed(() -> OpenWeatherAPI.getOpenWeatherData(MainActivity.this, MainActivity.this), 100);

        KSFarmMeta.getLonganQountDataFromWebService(MainActivity.this, MainActivity.this);
    }

    private void setIOTElementsVisible(int visible) {

        TextView textViewKSFarmIOTLabel = findViewById(R.id.textViewKSFarmIOTLabel);

        textViewKSFarmIOTLabel.setVisibility(visible);

        SwitchMaterial relaySwitch = findViewById(R.id.switchBedLamp);

        relaySwitch.setVisibility(visible);

        relaySwitch = findViewById(R.id.switchStandingDeskLamp);

        relaySwitch.setVisibility(visible);

        relaySwitch = findViewById(R.id.switchBedRoomAirCon);

        relaySwitch.setVisibility(visible);
    }
}