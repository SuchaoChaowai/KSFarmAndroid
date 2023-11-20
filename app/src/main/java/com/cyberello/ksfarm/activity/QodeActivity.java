package com.cyberello.ksfarm.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.cyberello.ksfarm.R;
import com.cyberello.ksfarm.util.QRCodeUtil;
import com.cyberello.ksfarm.util.QodeUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.DecimalFormat;

public class QodeActivity extends AppCompatActivity implements QRCodeUtil.QRCodeListener {

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = QRCodeUtil.getQRCodeLauncher(QodeActivity.this, QodeActivity.this);

    private static final DecimalFormat dfLatLon = new DecimalFormat("0.00000");
    private static final DecimalFormat dfAltitude = new DecimalFormat("0");

    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qode);

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
            });

            locationPermissionRequest.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});

            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(this, location -> {
            if (location != null) {

                setLocation(location);
            }
        });

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {

                    setLocation(location);
                }
            }
        };

        long updateInterval = 2000;
        LocationRequest.Builder builder = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, updateInterval);

        LocationRequest locationRequest = builder.build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
            });

            locationPermissionRequest.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});

            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        Button button = findViewById(R.id.buttonScanQode);

        button.setOnClickListener(v -> barcodeLauncher.launch(QRCodeUtil.getScanOptions()));

        TextView textViewQodeAccuracy = findViewById(R.id.textViewQodeAccuracy);
        textViewQodeAccuracy.setTextColor(Color.RED);

        showQodeInfo();
    }

    private void setLocation(Location _location) {

        location = _location;

        if (QodeUtil.getQodeJSON().qr != null) {
            QodeUtil.setLocation(location);
        }

        showLocationInfo();
    }

    private void showLocationInfo() {

        if (location == null) return;

        TextView textViewQodeAccuracy = findViewById(R.id.textViewQodeAccuracy);
        textViewQodeAccuracy.setTextColor(Color.RED);

        String textString = dfLatLon.format(location.getLatitude());
        textString += ", " + dfLatLon.format(location.getLongitude());
        textString += ", " + dfAltitude.format(location.getAccuracy());

        textViewQodeAccuracy.setText(textString);

        if (location.getAccuracy() < 10.0) {
            textViewQodeAccuracy.setTextColor(Color.GREEN);
        }
    }

    private void showQodeInfo() {

        if (QodeUtil.getQodeJSON().qr == null || QodeUtil.getQodeJSON().qr.isEmpty()) {
            return;
        }

        TextView textView = findViewById(R.id.textViewQodeID);

        textView.setText(QodeUtil.getQodeJSON().qr);
    }

    @Override
    public void processQRCodeString(String qrCodeString) {

        QodeUtil.setQR(QRCodeUtil.getKSFarmQRString(qrCodeString));

        showQodeInfo();
    }
}