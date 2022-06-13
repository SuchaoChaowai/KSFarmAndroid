package com.cyberello.ksfarm;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.cyberello.ksfarm.util.QRCodeUtil;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity implements QRCodeUtil.QRCodeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityResultLauncher<ScanOptions> qrcodeLauncher = QRCodeUtil.getQRCodeLauncher(this, this);

        findViewById(R.id.qr_scan_button).setOnClickListener(v -> qrcodeLauncher.launch(QRCodeUtil.getScanOptions()));
    }

    public void processQRCodeString(String scannedText) {

        Toast.makeText(MainActivity.this, "Scanned: " + QRCodeUtil.getKSFarmQRString(scannedText), Toast.LENGTH_LONG).show();
    }
}