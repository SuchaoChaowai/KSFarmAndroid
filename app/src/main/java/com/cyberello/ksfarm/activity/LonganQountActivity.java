package com.cyberello.ksfarm.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.cyberello.ksfarm.R;
import com.cyberello.ksfarm.util.KSFarmUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LonganQountActivity extends AppCompatActivity {

    private TextView textViewLatLon;
    private TextView textViewAccuracy;

    private Vibrator vibrator;
    int textColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_longan_qount);

        VibratorManager vibratorManager = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
        vibrator = vibratorManager.getDefaultVibrator();

        textViewLatLon = findViewById(R.id.textViewLatLon);
        textViewAccuracy = findViewById(R.id.textViewAccuracy);

        textColor = textViewAccuracy.getCurrentTextColor();

        setGPS();
    }

    private void setGPS() {

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityResultLauncher<String[]> locationPermissionRequest =
                    registerForActivityResult(new ActivityResultContracts
                                    .RequestMultiplePermissions(), result -> {
                                result.getOrDefault(
                                        Manifest.permission.ACCESS_FINE_LOCATION, false);
                                result.getOrDefault(
                                        Manifest.permission.ACCESS_COARSE_LOCATION, false);
                            }
                    );

            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    setGPSLocation(location);
                    showLocation(location);
                });

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

                for (Location location : locationResult.getLocations()) {

                    setGPSLocation(location);
                    showLocation(location);
                }
            }
        };

        long updateInterval = 3000;

        LocationRequest.Builder builder = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, updateInterval);

        LocationRequest locationRequest = builder.build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityResultLauncher<String[]> locationPermissionRequest =
                    registerForActivityResult(new ActivityResultContracts
                                    .RequestMultiplePermissions(), result -> {
                                result.getOrDefault(
                                        Manifest.permission.ACCESS_FINE_LOCATION, false);
                                result.getOrDefault(
                                        Manifest.permission.ACCESS_COARSE_LOCATION, false);
                            }
                    );

            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });

            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.longan_qount_menu, menu);

        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.longanQountIOT) {

            showMainScreen();
            return true;
        }

        return false;
    }

    private void showMainScreen() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

    }

    private void setGPSLocation(Location location) {
    }


    private void showLocation(Location location) {

        String textString = KSFarmUtil.dfLatLon.format(location.getLatitude()) + ", " + KSFarmUtil.dfLatLon.format(location.getLongitude()) + ", Alt " + KSFarmUtil.dfAltitude.format(location.getAltitude()) + " m.";

        textViewLatLon.setText(textString);

        textString = "Acc: " + KSFarmUtil.dfAltitude.format(location.getAccuracy()) + " m.";
        textViewAccuracy.setText(textString);

        if (location.getAccuracy() < 10) {

            textViewLatLon.setTextColor(Color.GREEN);
            textViewAccuracy.setTextColor(Color.GREEN);

            return;
        }

        if (location.getAccuracy() < 20) {

            textViewLatLon.setTextColor(textColor);
            textViewAccuracy.setTextColor(textColor);

            return;
        }

        textViewLatLon.setTextColor(Color.RED);
        textViewAccuracy.setTextColor(Color.RED);
    }
}