package com.cyberello.ksfarm;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    processScanResult(result.getContents());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScanOptions options = new ScanOptions();

        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan QR code");
        options.setCameraId(0);  // Use a specific camera of the device
        options.setBeepEnabled(true);

        findViewById(R.id.qr_scan_button).setOnClickListener(v -> barcodeLauncher.launch(options));
    }

    private void processScanResult(String scannedText) {

        Toast.makeText(MainActivity.this, "Scanned: " + scannedText, Toast.LENGTH_LONG).show();
    }
}