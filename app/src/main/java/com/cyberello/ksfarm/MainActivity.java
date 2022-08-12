package com.cyberello.ksfarm;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.os.Bundle;

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
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONObject;

import java.text.ParseException;

public class MainActivity extends AppCompatActivity implements QRCodeUtil.QRCodeListener, IOTService.WebServiceResultListener, IOTControl.IOTControlResultListener {

    private static ActivityResultLauncher<ScanOptions> qrcodeLauncher;

    private GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyGestureListener myGestureListener = new MyGestureListener();
        myGestureListener.activity = this;

        mDetector = new GestureDetectorCompat(this, myGestureListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onResume() {
        super.onResume();
        IOTService.getIOTData(this, this);
    }

    public void processQRCodeString(String scannedText) {

        Toast.makeText(MainActivity.this, "Scanned: " + QRCodeUtil.getKSFarmQRString(scannedText), Toast.LENGTH_LONG).show();
    }

    @Override
    public void processWebServiceResult(JSONObject response) {

        IOTJSONWrapper iotJSONWrapper = KSFarmUtil.gson().fromJson(response.toString(), IOTJSONWrapper.class);

        MainActivity self = this;

        iotJSONWrapper.iotJSONs.forEach((iotJSON) -> {

            if (iotJSON.id.equals("KSF0001")) {

                setTempData(iotJSON);
            }

            if (iotJSON.id.equals("KSF0009")) {

                setSecondFloorTempData(iotJSON);
            }

            if (iotJSON.id.equals("KSF0002")) {

                setAirConData(self, iotJSON);
            }

            if (iotJSON.id.equals("KSF0003")) {

                setLampData(self, iotJSON, findViewById(R.id.switchOverHeadLampRelay));
            }

            if (iotJSON.id.equals("KSF0005")) {

                setLampData(self, iotJSON, findViewById(R.id.switchDeskLampRelay));
            }

            if (iotJSON.id.equals("KSF0006")) {

                setLampData(self, iotJSON, findViewById(R.id.switchStandingDeskRelay));
            }

            if (iotJSON.id.equals("KSF0007")) {

                setLampData(self, iotJSON, findViewById(R.id.switchBedSideLampRelay));
            }
        });
    }

    private void setLampData(MainActivity self, IOTJSON iotJSON, SwitchMaterial relaySwitch) {

        IOTAirConJSON iotAirConJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTAirConJSON.class);

        relaySwitch.setChecked(iotAirConJSON.relay1.equals("on"));

        relaySwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> IOTControl.setRelayState(iotJSON.deviceIP, isChecked, self, self));
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
    }

    private void setSecondFloorTempData(IOTJSON iotJSON) {

        IOTTempJSON iotTempJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTTempJSON.class);

        TextView textViewTemp = findViewById(R.id.textViewSecondFloorTemp);
        TextView textViewHumid = findViewById(R.id.textViewSecondFloorHumid);
        TextView textViewLastUpdate = findViewById(R.id.textViewSecondFloorLastUpdate);
        TextView textViewSecondFloorPressure = findViewById(R.id.textViewSecondFloorPressure);

        String textString = iotTempJSON.temperature + " C";
        textViewTemp.setText(textString);

        textString = iotTempJSON.humidity + " %";
        textViewHumid.setText(textString);

        textString = Math.round(iotTempJSON.pressure) + " hPa";
        textViewSecondFloorPressure.setText(textString);

        try {
            textViewLastUpdate.setText(KSFarmUtil.getServerDateTimeString(iotJSON.lastUpdateTimeString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
                (buttonView, isChecked) -> IOTControl.setRelayState(iotJSON.deviceIP, isChecked, self, self));
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

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        public MainActivity activity;

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            IOTService.getIOTData(activity, activity);
            return true;
        }
    }
}