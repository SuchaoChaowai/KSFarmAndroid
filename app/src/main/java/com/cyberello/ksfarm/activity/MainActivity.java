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
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.cyberello.ksfarm.KSFarmMeta;
import com.cyberello.ksfarm.R;
import com.cyberello.ksfarm.data.KSFarmConstants;
import com.cyberello.ksfarm.data.LonganQount;
import com.cyberello.ksfarm.util.KSFarmUtil;
import com.cyberello.ksfarm.util.QRCodeUtil;
import com.cyberello.ksfarm.util.QodeUtil;
import com.cyberello.ksfarm.webService.KSFarmWebService;
import com.cyberello.ksfarm.webService.OpenWeatherAPI;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, QRCodeUtil.QRCodeListener, KSFarmUtil.MetaDataListener, OpenWeatherAPI.OpenWeatherAPIListener, KSFarmWebService.KSFarmWebServiceResultListener {

    private GestureDetectorCompat mDetector;
    private SharedPreferences sharedPreferences;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = QRCodeUtil.getQRCodeLauncher(MainActivity.this, MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDetector = new GestureDetectorCompat(this, this);
        mDetector.setOnDoubleTapListener(this);

        sharedPreferences = this.getSharedPreferences(KSFarmConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        });

        Button button = findViewById(R.id.buttonScanQodeMain);

        button.setOnClickListener(v -> barcodeLauncher.launch(QRCodeUtil.getScanOptions()));

        button = findViewById(R.id.buttonPapayaCount);

        button.setOnClickListener(v -> papayaMenuSelected());
    }

    @Override
    public void onResume() {
        super.onResume();

        getWebserviceData();
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

        if (item.getItemId() == R.id.mainPapayaMenu) {

            papayaMenuSelected();
            return true;
        }

        if (item.getItemId() == R.id.fertilizerMenu) {

            fertilizerMenuSelected();
            return true;
        }

        if (item.getItemId() == R.id.qodeMainMenu) {

            qodeMenuSelected();
            return true;
        }

        if (item.getItemId() == R.id.scanQRMainMenuItem) {

            scanQR();
            return true;
        }

        return false;
    }

    private void papayaMenuSelected() {

        Intent intent = new Intent(getApplicationContext(), PapayaCountActivity.class);
        startActivity(intent);
    }

    private void scanQR() {

        barcodeLauncher.launch(QRCodeUtil.getScanOptions());
    }

    private void qodeMenuSelected() {

        Intent intent = new Intent(getApplicationContext(), QodeActivity.class);
        startActivity(intent);
    }

    private void fertilizerMenuSelected() {

        Intent intent = new Intent(getApplicationContext(), FertilizerActivity.class);
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

        try {
            setWeatherData(openWeatherJsonObject);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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

    }

    @Override
    public void onErrorResponse(String errorMessage) {

    }

    @Override
    public void onErrorResponse(String status, String errorMessage) {

    }

    private void getWebserviceData() {

        Handler handler = new Handler(Objects.requireNonNull(Looper.myLooper()));

        handler.postDelayed(() -> OpenWeatherAPI.getOpenWeatherData(MainActivity.this, MainActivity.this), 100);
    }

    public void processQRCodeString(String scannedText) {

        QodeUtil.setQR(QRCodeUtil.getKSFarmQRString(scannedText));

        qodeMenuSelected();
    }

    private void setWeatherData(JSONObject openWeatherJsonObject) throws JSONException {

        JSONArray array;

        TextView textView = findViewById(R.id.textViewMainWeather);

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

        textString = textString + ", " + Math.round(wind.getDouble("speed") * 3.6) + " - " + Math.round(wind.getDouble("gust") * 3.6) + " กม./ชม.";

        textView = findViewById(R.id.textViewWind);

        textView.setText(textString);

        JSONObject sun = openWeatherJsonObject.getJSONObject("sys");

        textString = "พระอาทิตย์  ";

        textString = textString + KSFarmUtil.getSunTime(sun.getLong("sunrise")) + " - " + KSFarmUtil.getSunTime(sun.getLong("sunset"));

        textView = findViewById(R.id.textViewSun);

        textView.setText(textString);

        try {

            textView = findViewById(R.id.textViewLastUpdate);
            textView.setText(KSFarmUtil.getServerDateTimeString(openWeatherJsonObject.getLong("dt")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void metaDataReady() {

    }

    @Override
    public void metaDataEmpty() {

    }
}