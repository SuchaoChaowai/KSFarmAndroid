package com.cyberello.ksfarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.cyberello.ksfarm.data.KSConstants;
import com.cyberello.ksfarm.data.json.BaroTestResultJSON;
import com.cyberello.ksfarm.data.json.IOTJSON;
import com.cyberello.ksfarm.data.json.IOTMetaJSON;
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
    public void processWebServicePostDataResult(JSONObject response) {

        KSFarmUtil.setLocalIOTData(response.toString(), sharedPreferences);
    }

    @Override
    public void processWebServiceGetIOTDataResult(JSONObject response) {

        IOTJSONWrapper iotJSONWrapper = KSFarmUtil.gson().fromJson(response.toString(), IOTJSONWrapper.class);

        KSFarmUtil.setLocalIOTData(response.toString(), sharedPreferences);

        processIOTJSONWrapper(iotJSONWrapper);
    }

    @Override
    public void onErrorResponse(String errorMessage) {


    }

    @Override
    public void onErrorResponse(String status, String message) {

    }

    @Override
    public void processWebServiceGetIOTMetaDataResult(JSONObject response) {

        KSFarmUtil.setIOTMetaData(response, MainActivity.this);
    }

    private void processIOTJSONWrapper(IOTJSONWrapper iotJSONWrapper) {

        new Thread(() -> iotJSONWrapper.iotJSONs.forEach(this::setIOTData)).start();
    }

    private void setIOTData(IOTJSON iotJSON) {

        new Thread(() -> {

            IOTMetaJSON.IOTDevice device = KSFarmUtil.getDeviceName(iotJSON.id);

            if (device == null) {
                return;
            }

            iotJSON.name = device.description;

            if (device.name.equals(KSConstants.FIRST_FLOOR_IOT_DEVICE_ID)) {

                runOnUiThread(() -> setTempData(iotJSON));

                return;
            }

            if (device.name.equals(KSConstants.BED_ROOM_AIR_CON_IOT_DEVICE_ID)) {

                runOnUiThread(() -> setAirConData(iotJSON));

                return;
            }

            if (device.name.equals(KSConstants.OVER_HEAD_DESK_LAMP_IOT_DEVICE_ID)) {

                runOnUiThread(() -> setLampData(iotJSON, findViewById(R.id.switchOverHeadLampRelay), findViewById(R.id.textViewLightLabel)));

                return;
            }

            if (device.name.equals(KSConstants.BED_ROOM_TEMP_IOT_DEVICE_ID)) {

                runOnUiThread(() -> setBedRoomData(iotJSON));

                return;
            }

            if (device.name.equals(KSConstants.DESK_LAMP_IOT_DEVICE_ID)) {

                runOnUiThread(() -> setLampData(iotJSON, findViewById(R.id.switchDeskLampRelay), findViewById(R.id.textViewDeskLampLabel)));

                return;
            }

            if (device.name.equals(KSConstants.STANDING_DESK_LAMP_IOT_DEVICE_ID)) {

                runOnUiThread(() -> setLampData(iotJSON, findViewById(R.id.switchStandingDeskRelay), findViewById(R.id.textViewStandingDeskLabel)));

                return;
            }

            if (device.name.equals(KSConstants.BED_SIDE_LAMP_IOT_DEVICE_ID)) {

                runOnUiThread(() -> setLampData(iotJSON, findViewById(R.id.switchBedSideLampRelay), findViewById(R.id.textViewBedSideLabel)));

                return;
            }

            if (device.name.equals(KSConstants.DESK_TOP_IOT_DEVICE_ID)) {

                runOnUiThread(() -> setDeskData(iotJSON));

                return;
            }

            if (device.name.equals(KSConstants.SECOND_FLOOR_BALCONY_IOT_DEVICE_ID)) {

                runOnUiThread(() -> setSecondFloorBalconyTempData(iotJSON));
            }
            if (device.name.equals(KSConstants.BARO_SENSOR_TEST)) {

                runOnUiThread(() -> setBaroTestData(iotJSON));
            }
            if (device.name.equals(KSConstants.ESP_01_TEST_DEVICE_001)) {

                runOnUiThread(() -> setLampData(iotJSON, findViewById(R.id.switcESP01TestDeviceRelay), findViewById(R.id.textViewESP01TestDeviceLabel)));
            }
        }).start();
    }

    private void setBaroTestData(IOTJSON iotJSON) {

        BaroTestResultJSON iotTempJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, BaroTestResultJSON.class);

        TextView textView = findViewById(R.id.textViewBaroTestLabel);
        textView.setText(iotJSON.name);

        textView = findViewById(R.id.textViewBaroTestReadData);

        String textString = iotTempJSON.temperature + " C, " + iotTempJSON.pressure + " hPa";
        textView.setText(textString);

        textView = findViewById(R.id.textViewBaroTestErrorCount);

        textString = "ReadCount: " + iotTempJSON.readCount + "/" + iotTempJSON.readErrorCount;
        textView.setText(textString);

        textView = findViewById(R.id.textViewBaroTestLastUpdate);

        try {
            textView.setText(KSFarmUtil.getServerDateTimeString(iotJSON.lastUpdateTimeString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setDeskData(IOTJSON iotJSON) {

        try {

            IOTTempJSON iotTempJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTTempJSON.class);

            TextView textViewLabel = findViewById(R.id.textViewDeskLabel);
            textViewLabel.setText(iotJSON.name);

            TextView textViewTemp = findViewById(R.id.textViewDeskTemp);
            TextView textViewLastUpdate = findViewById(R.id.textViewDeskLastUpdate);

            String textString = iotTempJSON.temperature + " C, " + iotTempJSON.humidity + " %, " + iotTempJSON.pressure + " hPa";
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

            TextView textViewLabel = findViewById(R.id.textViewBedRoomLabel);
            textViewLabel.setText(iotJSON.name);

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

            textViewLabel.setText(iotJSON.name);
            textViewLabel.setOnClickListener(view -> refreshIOTData(iotJSON));
        } catch (NumberFormatException nex) {
            nex.printStackTrace();
        }
    }

    private void setTempData(IOTJSON iotJSON) {

        try {

            IOTTempJSON iotTempJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTTempJSON.class);

            TextView textViewLabel = findViewById(R.id.textViewFirstFloorLabel);
            textViewLabel.setText(iotJSON.name);

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

    private void setSecondFloorBalconyTempData(IOTJSON iotJSON) {

        try {

            IOTTempJSON iotTempJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTTempJSON.class);

            TextView textViewLabel = findViewById(R.id.textViewSecondFloorLabel);
            textViewLabel.setText(iotJSON.name);

            TextView textViewTemp = findViewById(R.id.textViewSecondFloorTemp);
            TextView textViewLastUpdate = findViewById(R.id.textViewSecondFloorLastUpdate);
            TextView textViewSecondFloorPressure = findViewById(R.id.textViewSecondFloorPressure);

            String textString = iotTempJSON.temperature + " C";
            textViewTemp.setText(textString);

            textString = Math.round(iotTempJSON.humidity) + " %";
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

            TextView textViewLabel = findViewById(R.id.textViewAirConLabel);
            textViewLabel.setText(iotJSON.name);

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