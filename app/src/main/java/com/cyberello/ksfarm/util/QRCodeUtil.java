package com.cyberello.ksfarm.util;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.cyberello.ksfarm.data.KSFarmConstants;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class QRCodeUtil {

    private static ScanOptions options;

    public static ScanOptions getScanOptions() {

        if (options != null) {

            return options;
        }

        options = new ScanOptions();

        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan QR code");
        options.setCameraId(0);
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);

        return options;
    }

    public static ActivityResultLauncher<ScanOptions> getQRCodeLauncher(AppCompatActivity activity, QRCodeListener listener) {

        return activity.registerForActivityResult(new ScanContract(),
                result -> {

                    if (result.getContents() != null) {
                        listener.processQRCodeString(result.getContents());
                    }
                });
    }

    public static String getKSFarmQRString(String qrString) {

        return qrString.replace(KSFarmConstants.QR_CODE_URL, "");
    }

    public interface QRCodeListener {

        void processQRCodeString(String qrCodeString);
    }
}
