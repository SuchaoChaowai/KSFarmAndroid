package com.cyberello.ksfarm;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.cyberello.ksfarm.data.json.IOTTempJSON;
import com.cyberello.ksfarm.util.KSFarmUtil;
import com.cyberello.ksfarm.util.QRCodeUtil;
import com.cyberello.ksfarm.webService.IOTService;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONObject;

import com.cyberello.ksfarm.data.json.IOTJSON;

public class MainActivity extends AppCompatActivity implements QRCodeUtil.QRCodeListener, IOTService.WebServiceResultListener {

    private static ActivityResultLauncher<ScanOptions> qrcodeLauncher;

    private IOTJSON iotJSON;
    private IOTTempJSON iotTempJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initQRCodeLauncher();

        findViewById(R.id.qr_scan_button).setOnClickListener(v -> scanQR());

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

        iotJSON = KSFarmUtil.gson().fromJson(response.toString(), IOTJSON.class);

        if (iotJSON.type.equals("temp")) {

            iotTempJSON = KSFarmUtil.gson().fromJson(iotJSON.jsonString, IOTTempJSON.class);
        }
    }

    @Override
    public void onErrorResponse(String errorMessage) {

    }
}