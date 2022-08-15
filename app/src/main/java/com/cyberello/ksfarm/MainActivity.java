package com.cyberello.ksfarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.cyberello.ksfarm.data.KSConstants;
import com.cyberello.ksfarm.data.json.IOTJSON;
import com.cyberello.ksfarm.webService.IOTControl;
import com.google.android.material.switchmaterial.SwitchMaterial;

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

public class MainActivity extends AppCompatActivity implements QRCodeUtil.QRCodeListener, IOTService.WebServiceResultListener {

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

        IOTService.getIOTData(MainActivity.this, MainActivity.this);
    }

    public void processQRCodeString(String scannedText) {

        Toast.makeText(MainActivity.this, "Scanned: " + QRCodeUtil.getKSFarmQRString(scannedText), Toast.LENGTH_LONG).show();
    }

    @Override
    public void processWebServiceResult(JSONObject response) {

        IOTJSONWrapper iotJSONWrapper = KSFarmUtil.gson().fromJson(response.toString(), IOTJSONWrapper.class);

        KSFarmUtil.setLocalIOTData(response.toString(), sharedPreferences);

        processIOTJSONWrapper(iotJSONWrapper);
    }

    private void processIOTJSONWrapper(IOTJSONWrapper iotJSONWrapper) {

        new Thread(() -> iotJSONWrapper.iotJSONs.forEach(this::setIOTData)).start();
    }

    private void setIOTData(IOTJSON iotJSON) {

        new Thread(() -> {

            if (iotJSON.id.equals("KSF0001")) {

                runOnUiThread(() -> setTempData(iotJSON));

                return;
            }

            if (iotJSON.id.equals("KSF0002")) {

                runOnUiThread(() -> setAirConData(iotJSON));

                return;
            }

            if (iotJSON.id.equals("KSF0003")) {

                runOnUiThread(() -> setLampData(iotJSON, findViewById(R.id.switchOverHeadLampRelay), findViewById(R.id.textViewLightLabel)));

                return;
            }

            if (iotJSON.id.equals("KSF0010")) {

                runOnUiThread(() -> setBedRoomData(iotJSON));

                return;
            }

            if (iotJSON.id.equals("KSF0005")) {

                runOnUiThread(() -> setLampData(iotJSON, findViewById(R.id.switchDeskLampRelay), findViewById(R.id.textViewDeskLampLabel)));

                return;
            }

            if (iotJSON.id.equals("KSF0006")) {

                runOnUiThread(() -> setLampData(iotJSON, findViewById(R.id.switchStandingDeskRelay), findViewById(R.id.textViewStandingDeskLabel)));

                return;
            }

            if (iotJSON.id.equals("KSF0007")) {

                runOnUiThread(() -> setLampData(iotJSON, findViewById(R.id.switchBedSideLampRelay), findViewById(R.id.textViewBedSideLabel)));

                return;
            }

            if (iotJSON.id.equals("KSF0008")) {

                runOnUiThread(() -> setDeskData(iotJSON));

                return;
            }

            if (iotJSON.id.equals("KSF0009")) {

                runOnUiThread(() -> setSecondFloorTempData(iotJSON));
            }
        }).start();
    }

    private void setDeskData(IOTJSON iotJSON) {

        try {

            IOTTempJSON iotTempJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTTempJSON.class);

            TextView textViewTemp = findViewById(R.id.textViewDeskTemp);
            TextView textViewLastUpdate = findViewById(R.id.textViewDeskLastUpdate);

            String textString = iotTempJSON.temperature + " C, " + iotTempJSON.humidity + " %, " + Math.round(iotTempJSON.pressure) + " hPa";
            textViewTemp.setText(textString);

            try {
                textViewLastUpdate.setText(KSFarmUtil.getServerDateTimeString(iotJSON.lastUpdateTimeString));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            textViewLastUpdate.setOnClickListener(view -> refreshIOTData(iotJSON));
            findViewById(R.id.textViewDeskLabel).setOnClickListener(view -> refreshIOTData(iotJSON));

            SwitchMaterial relaySwitch = findViewById(R.id.switchDeskRelay);

            relaySwitch.setChecked(iotTempJSON.relay1.equals("on"));

            relaySwitch.setOnCheckedChangeListener(
                    (buttonView, isChecked) -> IOTControl.setRelayState(iotJSON.deviceIP, isChecked, MainActivity.this, MainActivity.this));
        } catch (NumberFormatException nex) {
            nex.printStackTrace();
        }
    }

    private void setBedRoomData(IOTJSON iotJSON) {

        try {

            IOTTempJSON iotTempJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTTempJSON.class);

            TextView textViewTemp = findViewById(R.id.textViewBedRoomTemp);
            TextView textViewLastUpdate = findViewById(R.id.textViewBedRoomLastUpdate);
            TextView textViewSecondFloorPressure = findViewById(R.id.textViewBedRoomPressure);

            String textString = iotTempJSON.temperature + " C";
            textViewTemp.setText(textString);

            textString = iotTempJSON.humidity + " %";
            textViewSecondFloorPressure.setText(textString);

            try {
                textViewLastUpdate.setText(KSFarmUtil.getServerDateTimeString(iotJSON.lastUpdateTimeString));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            textViewLastUpdate.setOnClickListener(view -> refreshIOTData(iotJSON));
            findViewById(R.id.textViewBedRoomLabel).setOnClickListener(view -> refreshIOTData(iotJSON));

        } catch (NumberFormatException nex) {
            nex.printStackTrace();
        }
    }

    private void setLampData(IOTJSON iotJSON, SwitchMaterial relaySwitch, TextView textViewLabel) {

        try {

            IOTTempJSON iotTempJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTTempJSON.class);

            relaySwitch.setChecked(iotTempJSON.relay1.equals("on"));

            relaySwitch.setOnCheckedChangeListener(
                    (buttonView, isChecked) -> IOTControl.setRelayState(iotJSON.deviceIP, isChecked, MainActivity.this, MainActivity.this));

            textViewLabel.setOnClickListener(view -> refreshIOTData(iotJSON));
        } catch (NumberFormatException nex) {
            nex.printStackTrace();
        }
    }

    private void setTempData(IOTJSON iotJSON) {

        try {

            IOTTempJSON iotTempJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTTempJSON.class);

            TextView textViewTemp = findViewById(R.id.textViewTemp);
            TextView textViewHumid = findViewById(R.id.textViewHumid);
            TextView textViewLastUpdate = findViewById(R.id.textViewLastUpdate);

            String textString = iotTempJSON.temperature + " C";
            textViewTemp.setText(textString);

            textString = iotTempJSON.humidity + " %";
            textViewHumid.setText(textString);

            try {
                textViewLastUpdate.setText(KSFarmUtil.getServerDateTimeString(iotJSON.lastUpdateTimeString));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            textViewLastUpdate.setOnClickListener(view -> refreshIOTData(iotJSON));

            findViewById(R.id.textViewFirstFloorLabel).setOnClickListener(view -> refreshIOTData(iotJSON));
        } catch (NumberFormatException nex) {
            nex.printStackTrace();
        }
    }

    private void setSecondFloorTempData(IOTJSON iotJSON) {

        try {

            IOTTempJSON iotTempJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTTempJSON.class);

            TextView textViewTemp = findViewById(R.id.textViewSecondFloorTemp);
            TextView textViewLastUpdate = findViewById(R.id.textViewSecondFloorLastUpdate);
            TextView textViewSecondFloorPressure = findViewById(R.id.textViewSecondFloorPressure);

            String textString = iotTempJSON.temperature + " C, " + iotTempJSON.humidity + " %";
            textViewTemp.setText(textString);

            textString = Math.round(iotTempJSON.pressure) + " hPa";
            textViewSecondFloorPressure.setText(textString);

            try {
                textViewLastUpdate.setText(KSFarmUtil.getServerDateTimeString(iotJSON.lastUpdateTimeString));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            textViewLastUpdate.setOnClickListener(view -> refreshIOTData(iotJSON));
            findViewById(R.id.textViewSecondFloorLabel).setOnClickListener(view -> refreshIOTData(iotJSON));
        } catch (NumberFormatException nex) {
            nex.printStackTrace();
        }
    }

    private void setAirConData(IOTJSON iotJSON) {

        try {

            IOTTempJSON iotTempJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTTempJSON.class);

            TextView textViewTemp = findViewById(R.id.textViewAirConTemp);

            String textString = iotTempJSON.temperature + " C, " + iotTempJSON.humidity + " %";
            textViewTemp.setText(textString);

            SwitchMaterial relaySwitch = findViewById(R.id.switchAirConRelay);

            relaySwitch.setChecked(iotTempJSON.relay1.equals("on"));

            relaySwitch.setOnCheckedChangeListener(
                    (buttonView, isChecked) -> IOTControl.setRelayState(iotJSON.deviceIP, isChecked, MainActivity.this, MainActivity.this));

            findViewById(R.id.textViewAirConLabel).setOnClickListener(view -> refreshIOTData(iotJSON));
        } catch (NumberFormatException nex) {
            nex.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(String errorMessage) {

    }

    @Override
    public void onErrorResponse(String status, String message) {

    }

    private void refreshIOTData(IOTJSON iotJSON) {

        IOTControl.refreshIOTData(iotJSON.deviceIP, this, this);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            IOTService.getIOTData(MainActivity.this, MainActivity.this);

            KSFarmUtil.toast(MainActivity.this, "Data refreshed!");
            return true;
        }
    }
}