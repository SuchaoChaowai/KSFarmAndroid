package com.cyberello.ksfarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.cyberello.ksfarm.data.KSConstants;
import com.cyberello.ksfarm.data.json.IOTJSON;
import com.cyberello.ksfarm.webService.IOTControl;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.cyberello.ksfarm.data.json.IOTJSONWrapper;
import com.cyberello.ksfarm.data.json.IOTTempJSON;
import com.cyberello.ksfarm.util.KSFarmUtil;
import com.cyberello.ksfarm.util.QRCodeUtil;
import com.cyberello.ksfarm.webService.IOTService;

import org.json.JSONObject;

import java.text.ParseException;

public class MainActivity extends AppCompatActivity implements QRCodeUtil.QRCodeListener, IOTService.WebServiceResultListener, KSFarmUtil.MetaDataListener {

    private GestureDetectorCompat mDetector;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyGestureListener myGestureListener = new MyGestureListener();

        mDetector = new GestureDetectorCompat(this, myGestureListener);

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

        KSFarmUtil.setIOTMetaData(response, MainActivity.this);
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

        if (iotJSON.name.equals(KSConstants.SECOND_FLOOR_BALCONY)) {

            runOnUiThread(() -> setSecondFloorBalconyTempData(iotJSON));
        }
    }

    private void setSecondFloorBalconyTempData(IOTJSON iotJSON) {

        try {

            IOTTempJSON iotTempJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTTempJSON.class);


            TextView textView = findViewById(R.id.textViewTemp);

            String textString = Math.round(iotTempJSON.temperature) + " C";
            textView.setText(textString);

            textView = findViewById(R.id.textViewHumid);
            textString = Math.round(iotTempJSON.humidity) + " %";
            textView.setText(textString);

            textView = findViewById(R.id.textViewPressure);
            textString = String.valueOf(Math.round(iotTempJSON.pressure));

            if (textString.length() > 3) {

                textString = textString.charAt(0) + "," + textString.substring(1);
            }

            textString += " hPa";
            textView.setText(textString);

            textView = findViewById(R.id.textViewLastUpdate);

            try {
                textView.setText(KSFarmUtil.getServerDateTimeString(iotJSON.lastUpdateTimeString));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            textView.setOnClickListener(view -> refreshIOTData(iotJSON));

        } catch (NumberFormatException nex) {
            nex.printStackTrace();
        }
    }

    private void refreshIOTData(IOTJSON iotJSON) {

        IOTControl.refreshIOTData(iotJSON.deviceIP, this, this);
    }

    @Override
    public void metaDataReady() {

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