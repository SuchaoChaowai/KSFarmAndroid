package com.cyberello.ksfarm;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.cyberello.ksfarm.data.json.IOTTempJSON;
import com.cyberello.ksfarm.util.KSFarmUtil;
import com.cyberello.ksfarm.util.QRCodeUtil;
import com.cyberello.ksfarm.webService.IOTService;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONObject;

import com.cyberello.ksfarm.data.json.IOTJSON;

import java.text.ParseException;

public class MainActivity extends AppCompatActivity implements QRCodeUtil.QRCodeListener, IOTService.WebServiceResultListener {

    private static ActivityResultLauncher<ScanOptions> qrcodeLauncher;

    private TextView textViewIOT_ID;
    private TextView textViewIP_Address;
    private TextView textViewLastUpdate;
    private TextView textViewTemp;
    private TextView textViewHumid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initQRCodeLauncher();

        textViewIOT_ID = findViewById(R.id.textViewIOT_ID);
        textViewIP_Address = findViewById(R.id.textViewIP_Address);
        textViewLastUpdate = findViewById(R.id.textViewLastUpdate);
        textViewTemp = findViewById(R.id.textViewTemp);
        textViewHumid = findViewById(R.id.textViewHumid);

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

        IOTTempJSON iotTempJSON = new IOTTempJSON();

        IOTJSON iotJSON = KSFarmUtil.gson().fromJson(response.toString(), IOTJSON.class);

        if (iotJSON.type.equals("temp")) {

            iotTempJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTTempJSON.class);
        }

        textViewIOT_ID.setText(iotJSON.id);
        textViewIP_Address.setText(iotJSON.deviceIP);
        try {
            textViewLastUpdate.setText(KSFarmUtil.getServerDateTimeString(iotJSON.lastUpdateTimeString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        textViewTemp.setText(iotTempJSON.temperature + " C");
        textViewHumid.setText(iotTempJSON.humidity + " %");
    }

    @Override
    public void onErrorResponse(String errorMessage) {

    }
}