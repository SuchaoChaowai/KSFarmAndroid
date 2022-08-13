package com.cyberello.ksfarm;

import androidx.appcompat.app.AlertDialog;
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

import com.cyberello.ksfarm.data.json.IOTAirConJSON;
import com.cyberello.ksfarm.data.json.IOTJSONWrapper;
import com.cyberello.ksfarm.data.json.IOTTempJSON;
import com.cyberello.ksfarm.util.KSFarmUtil;
import com.cyberello.ksfarm.util.QRCodeUtil;
import com.cyberello.ksfarm.webService.IOTService;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements QRCodeUtil.QRCodeListener, IOTService.WebServiceResultListener, IOTControl.IOTControlResultListener {

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

        String localIOTJSONWrapper = KSFarmUtil.getLocalIOTData(sharedPreferences);

        if (localIOTJSONWrapper != null && !localIOTJSONWrapper.isEmpty()) {

            IOTJSONWrapper iotJSONWrapper = KSFarmUtil.gson().fromJson(localIOTJSONWrapper, IOTJSONWrapper.class);
            processIOTJSONWrapper(iotJSONWrapper, this);
        } else {

            IOTService.getIOTData(this, this);
            return;
        }

        MainActivity self = this;

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                IOTService.getIOTData(self, self);
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, 3500);
    }

    public void processQRCodeString(String scannedText) {

        Toast.makeText(MainActivity.this, "Scanned: " + QRCodeUtil.getKSFarmQRString(scannedText), Toast.LENGTH_LONG).show();
    }

    @Override
    public void processWebServiceResult(JSONObject response) {

        IOTJSONWrapper iotJSONWrapper = KSFarmUtil.gson().fromJson(response.toString(), IOTJSONWrapper.class);

        KSFarmUtil.setLocalIOTData(response.toString(), sharedPreferences);

        MainActivity self = this;

        processIOTJSONWrapper(iotJSONWrapper, self);
    }

    private void processIOTJSONWrapper(IOTJSONWrapper iotJSONWrapper, MainActivity self) {

        iotJSONWrapper.iotJSONs.forEach((iotJSON) -> {

            if (iotJSON.id.equals("KSF0001")) {

                setTempData(iotJSON);
            }

            if (iotJSON.id.equals("KSF0002")) {

                setAirConData(self, iotJSON);
            }

            if (iotJSON.id.equals("KSF0003")) {

                setLampData(self, iotJSON, findViewById(R.id.switchOverHeadLampRelay), findViewById(R.id.textViewLightLabel));
            }

            if (iotJSON.id.equals("KSF0004")) {

                setBedRoomData(iotJSON);
            }

            if (iotJSON.id.equals("KSF0005")) {

                setLampData(self, iotJSON, findViewById(R.id.switchDeskLampRelay), findViewById(R.id.textViewDeskLampLabel));
            }

            if (iotJSON.id.equals("KSF0006")) {

                setLampData(self, iotJSON, findViewById(R.id.switchStandingDeskRelay), findViewById(R.id.textViewStandingDeskLabel));
            }

            if (iotJSON.id.equals("KSF0007")) {

                setLampData(self, iotJSON, findViewById(R.id.switchBedSideLampRelay), findViewById(R.id.textViewBedSideLabel));
            }

            if (iotJSON.id.equals("KSF0008")) {

                setDeskData(iotJSON);
            }

            if (iotJSON.id.equals("KSF0009")) {

                setSecondFloorTempData(iotJSON);
            }
        });
    }

    private void setDeskData(IOTJSON iotJSON) {

        IOTTempJSON iotTempJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTTempJSON.class);

        TextView textViewTemp = findViewById(R.id.textViewDeskTemp);
        TextView textViewLastUpdate = findViewById(R.id.textViewDeskLastUpdate);
        TextView textViewSecondFloorPressure = findViewById(R.id.textViewDeskPressure);

        String textString = iotTempJSON.temperature + " C, " + iotTempJSON.humidity + " %";
        textViewTemp.setText(textString);

        textString = iotTempJSON.pressure + " hPa";
        textViewSecondFloorPressure.setText(textString);

        try {
            textViewLastUpdate.setText(KSFarmUtil.getServerDateTimeString(iotJSON.lastUpdateTimeString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        textViewLastUpdate.setOnClickListener(view -> refreshIOTData(iotJSON));
        findViewById(R.id.textViewDeskLabel).setOnClickListener(view -> refreshIOTData(iotJSON));
    }

    private void setBedRoomData(IOTJSON iotJSON) {

        IOTTempJSON iotTempJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTTempJSON.class);

        TextView textViewTemp = findViewById(R.id.textViewBedRoomTemp);
        TextView textViewLastUpdate = findViewById(R.id.textViewBedRoomLastUpdate);
        TextView textViewSecondFloorPressure = findViewById(R.id.textViewBedRoomPressure);

        String textString = iotTempJSON.temperature + " C";
        textViewTemp.setText(textString);

        textString = iotTempJSON.pressure + " hPa";
        textViewSecondFloorPressure.setText(textString);

        try {
            textViewLastUpdate.setText(KSFarmUtil.getServerDateTimeString(iotJSON.lastUpdateTimeString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        textViewLastUpdate.setOnClickListener(view -> refreshIOTData(iotJSON));
        findViewById(R.id.textViewBedRoomLabel).setOnClickListener(view -> refreshIOTData(iotJSON));
    }

    private void setLampData(MainActivity self, IOTJSON iotJSON, SwitchMaterial relaySwitch, TextView textViewLabel) {

        IOTAirConJSON iotAirConJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTAirConJSON.class);

        relaySwitch.setChecked(iotAirConJSON.relay1.equals("on"));

        relaySwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> IOTControl.setRelayState(iotJSON.deviceIP, isChecked, self, self));

        textViewLabel.setOnClickListener(view -> refreshIOTData(iotJSON));
    }

    private void setTempData(IOTJSON iotJSON) {

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
    }

    private void setSecondFloorTempData(IOTJSON iotJSON) {

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
    }

    private void setAirConData(MainActivity self, IOTJSON iotJSON) {

        IOTAirConJSON iotAirConJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTAirConJSON.class);

        TextView textViewTemp = findViewById(R.id.textViewAirConTemp);
        TextView textViewHumid = findViewById(R.id.textViewAirConHumid);

        String textString = iotAirConJSON.temperature + " C";
        textViewTemp.setText(textString);

        textString = iotAirConJSON.humidity + " %";
        textViewHumid.setText(textString);

        SwitchMaterial relaySwitch = findViewById(R.id.switchAirConRelay);

        relaySwitch.setChecked(iotAirConJSON.relay1.equals("on"));

        relaySwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    if (!iotAirConJSON.relay1.equals("on") && isChecked) {

                        final AlertDialog show = new AlertDialog.Builder(MainActivity.this)
                                .setIcon(android.R.drawable.ic_input_add)
                                .setTitle("เปิดแอร์?").setMessage("เปิดแอร์?")
                                .setPositiveButton("เปิด", (dialog, which) -> runOnUiThread(() -> IOTControl.setRelayState(iotJSON.deviceIP, true, self, self)))
                                .setNegativeButton("Cancel", (dialog, which) -> relaySwitch.setChecked(false))
                                .show();

                        return;
                    }

                    if (iotAirConJSON.relay1.equals("on") && !isChecked) {

                        final AlertDialog show = new AlertDialog.Builder(MainActivity.this)
                                .setIcon(android.R.drawable.ic_input_add)
                                .setTitle("ปิดแอร์?").setMessage("ปิดแอร์?")
                                .setPositiveButton("ปิด", (dialog, which) -> runOnUiThread(() -> IOTControl.setRelayState(iotJSON.deviceIP, false, self, self)))
                                .setNegativeButton("Cancel", (dialog, which) -> relaySwitch.setChecked(true))
                                .show();
                    }
                });

        findViewById(R.id.textViewAirConLabel).setOnClickListener(view -> refreshIOTData(iotJSON));
    }

    @Override
    public void onErrorResponse(String errorMessage) {

    }

    @Override
    public void processIOTControlResult(String response) {

        if (response.equals("200")) {
            IOTService.getIOTData(this, this);
        }
    }

    @Override
    public void onIOTControlErrorResponse(String errorMessage) {

    }

    private void refreshIOTData(IOTJSON iotJSON) {

        IOTControl.refreshIOTData(iotJSON.deviceIP, this, this);

        KSFarmUtil.toast(this, iotJSON.id + " Data updated!");
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