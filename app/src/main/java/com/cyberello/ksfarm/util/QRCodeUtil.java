package com.cyberello.ksfarm.util;

import com.journeyapps.barcodescanner.ScanOptions;

public class QRCodeUtil {

    private static final String QR_CODE_URL = "https://ksfarm.co/qr/";

    public static ScanOptions getScanOptions() {

        ScanOptions options = new ScanOptions();

        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan QR code");
        options.setCameraId(0);
        options.setBeepEnabled(true);

        return options;
    }

    public static String getKSFarmQRString(String qrString) {

        return qrString.replace(QR_CODE_URL, "");
    }
}
