package com.cyberello.ksfarm.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.cyberello.ksfarm.KSFarmMeta;
import com.cyberello.ksfarm.R;
import com.cyberello.ksfarm.util.QRCodeUtil;
import com.journeyapps.barcodescanner.ScanOptions;

public class QodeActivity extends AppCompatActivity implements QRCodeUtil.QRCodeListener {

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = QRCodeUtil.getQRCodeLauncher(QodeActivity.this, QodeActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qode);

        Button button = findViewById(R.id.buttonScanQode);

        button.setOnClickListener(v -> barcodeLauncher.launch(QRCodeUtil.getScanOptions()));

        showQodeInfo();
    }

    private void showQodeInfo() {

        if (KSFarmMeta.qodeID == null || KSFarmMeta.qodeID.isEmpty()) {
            return;
        }

        TextView textView = findViewById(R.id.textViewQodeID);

        textView.setText(KSFarmMeta.qodeID);
    }

    @Override
    public void processQRCodeString(String qrCodeString) {

        KSFarmMeta.qodeID = QRCodeUtil.getKSFarmQRString(qrCodeString);

        showQodeInfo();
    }
}