package com.cyberello.ksfarm;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.switchmaterial.SwitchMaterial;

import android.util.Log;
import android.widget.CompoundButton;
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

public class MainActivity extends AppCompatActivity implements QRCodeUtil.QRCodeListener, IOTService.WebServiceResultListener {

    private static ActivityResultLauncher<ScanOptions> qrcodeLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initQRCodeLauncher();

        findViewById(R.id.button_qr_scan).setOnClickListener(v -> scanQR());

        MainActivity self = this;
        findViewById(R.id.button_refresh).setOnClickListener(v -> IOTService.getIOTData(this, this));
    }

    @Override
    public void onResume() {
        super.onResume();
        IOTService.getIOTData(this, this);
    }

    private void initQRCodeLauncher() {

        if (qrcodeLauncher == null) {

            qrcodeLauncher = QRCodeUtil.getQRCodeLauncher(this, this);
        }
    }

    private void scanQR() {

        qrcodeLauncher.launch(QRCodeUtil.getScanOptions());
    }

    public void processQRCodeString(String scannedText) {

        Toast.makeText(MainActivity.this, "Scanned: " + QRCodeUtil.getKSFarmQRString(scannedText), Toast.LENGTH_LONG).show();
    }

    @Override
    public void processWebServiceResult(JSONObject response) {

        IOTJSONWrapper iotJSONWrapper = KSFarmUtil.gson().fromJson(response.toString(), IOTJSONWrapper.class);

        iotJSONWrapper.iotJSONs.forEach((iotJSON) -> {

            if (iotJSON.type.equals("temp")) {

                IOTTempJSON iotTempJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTTempJSON.class);

                TextView textViewTemp = findViewById(R.id.textViewTemp);
                TextView textViewHumid = findViewById(R.id.textViewHumid);
                TextView textViewLastUpdate = findViewById(R.id.textViewLastUpdate);

                textViewTemp.setText(iotTempJSON.temperature + " C");
                textViewHumid.setText(iotTempJSON.humidity + " %");

                try {
                    textViewLastUpdate.setText(KSFarmUtil.getServerDateTimeString(iotJSON.lastUpdateTimeString));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (iotJSON.type.equals("air_con")) {

                IOTAirConJSON iotAirConJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTAirConJSON.class);

                TextView textViewTemp = findViewById(R.id.textViewAirConTemp);
                TextView textViewHumid = findViewById(R.id.textViewAirConHumid);
                TextView textViewLastUpdate = findViewById(R.id.textViewAirConLastUpdate);

                textViewTemp.setText(iotAirConJSON.temperature + " C");
                textViewHumid.setText(iotAirConJSON.humidity + " %");

                try {
                    textViewLastUpdate.setText(KSFarmUtil.getServerDateTimeString(iotJSON.lastUpdateTimeString));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SwitchMaterial relaySwitch = findViewById(R.id.switchAirConRelay);

                if (iotAirConJSON.relay1.equals("on")) {

                    relaySwitch.setChecked(true);
                }

                relaySwitch.setOnCheckedChangeListener(
                        (buttonView, isChecked) -> SwitchRelay(iotJSON.deviceIP, isChecked));
            }

            if (iotJSON.type.equals("relay")) {

                IOTAirConJSON iotAirConJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTAirConJSON.class);

                TextView textViewLastUpdate = findViewById(R.id.textViewLightLastUpdate);

                try {
                    textViewLastUpdate.setText(KSFarmUtil.getServerDateTimeString(iotJSON.lastUpdateTimeString));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SwitchMaterial relaySwitch = findViewById(R.id.switchLightRelay);

                if (iotAirConJSON.relay1.equals("on")) {

                    relaySwitch.setChecked(true);
                }

                relaySwitch.setOnCheckedChangeListener(
                        (buttonView, isChecked) -> SwitchRelay(iotJSON.deviceIP, isChecked));
            }

        });
    }

    private void SwitchRelay(String ipAddress, boolean isChecked) {

        String state = "off";

        if (isChecked) {
            state = "on";
        }

    }

    @Override
    public void onErrorResponse(String errorMessage) {

    }
}