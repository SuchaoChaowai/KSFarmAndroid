package com.cyberello.ksfarm;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.cyberello.ksfarm.util.QRCodeUtil;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
                result -> {
                    if (result.getContents() != null) {
                        processScanResult(result.getContents());
                    }
                });

        findViewById(R.id.qr_scan_button).setOnClickListener(v -> barcodeLauncher.launch(QRCodeUtil.getScanOptions()));
    }

    private void processScanResult(String scannedText) {

        Toast.makeText(MainActivity.this, "Scanned: " + QRCodeUtil.getKSFarmQRString(scannedText), Toast.LENGTH_LONG).show();
    }
}