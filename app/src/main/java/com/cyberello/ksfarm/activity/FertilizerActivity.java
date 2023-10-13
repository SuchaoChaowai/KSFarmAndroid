package com.cyberello.ksfarm.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cyberello.ksfarm.R;
import com.cyberello.ksfarm.util.KSFarmUtil;
import com.cyberello.npk.NPKConstants;
import com.cyberello.npk.NPKUtil;
import com.cyberello.npk.NPK_JSON;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FertilizerActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private NPK_JSON npk_json;

    private TextView textViewDateTime;
    private TextView textViewLatLon;
    private TextView textViewAltitude;
    private TextView textViewAccuracy;
    private TextView textViewSenSorStatus;
    private TextView textViewServerAddress;
    private TextView textViewNPK;

    private static final DecimalFormat dfLatLon = new DecimalFormat("0.00000");
    private static final DecimalFormat dfAltitude = new DecimalFormat("0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fertilizer);

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        npk_json = new NPK_JSON();

        sharedPreferences = this.getSharedPreferences(NPKConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        initNPK();

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

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    if (location != null) {

                        npk_json.setLocation(location);

                        getNPKData();
                        showData();
                    }
                });

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {

                    npk_json.setLocation(location);

                    getNPKData();
                    showData();
                }
            }
        };

        long updateInterval = 2000;
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

        textViewDateTime = findViewById(R.id.textViewKSDateTime);
        textViewLatLon = findViewById(R.id.textViewKSLatLon);
        textViewLatLon.setTextColor(Color.RED);
        textViewAltitude = findViewById(R.id.textViewKSAltitude);
        textViewAccuracy = findViewById(R.id.textViewKSAccuracy);
        textViewSenSorStatus = findViewById(R.id.textViewSenSorStatus);
        textViewServerAddress = findViewById(R.id.textViewServerAddress);
        textViewNPK = findViewById(R.id.textViewNPKValue);

        textViewServerAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                NPKUtil.setLocalNPKSensorURLData(s.toString(), sharedPreferences);
            }
        });

        setSensorOnlineStatus(false);

        Button button = findViewById(R.id.buttonResetPlotAverage);
        button.setOnClickListener(v -> NPKUtil.resetNPK(this));

        setNPKUrl();

        showData();
    }

    private void initNPK() {

        NPKUtil.init();

        TextView textView = findViewById(R.id.textViewNNeeded);

        textView.setText(Integer.toString(NPKUtil.n));

        textView = findViewById(R.id.textViewPNeeded);

        textView.setText(Integer.toString(NPKUtil.p));

        textView = findViewById(R.id.textViewKNeeded);

        textView.setText(Integer.toString(NPKUtil.k));
    }

    private void setNPKUrl() {

        String npkSensorUrl = NPKUtil.getLocalNPKSensorURLData(sharedPreferences);

        if (npkSensorUrl.length() == 0) {

            npkSensorUrl = getString(R.string.npk_sensor_ip);

            NPKUtil.setLocalNPKSensorURLData(npkSensorUrl, sharedPreferences);
        }

        textViewServerAddress.setText(npkSensorUrl);
    }

    private void getNPKData() {

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, NPKUtil.getNPK_Sensor_URL(textViewServerAddress.getText().toString()),
                response -> {

                    NPK_JSON responseJSON = NPKUtil.gson().fromJson(response, NPK_JSON.class);

                    npk_json.setNPK(responseJSON.nitrogen, responseJSON.phosphorous, responseJSON.potassium);

                    NPKUtil.setNPK(responseJSON.nitrogen, responseJSON.phosphorous, responseJSON.potassium);

                    showData();

                    setSensorOnlineStatus(true);

                }, error -> setSensorOnlineStatus(false));

        queue.add(stringRequest);
    }

    private void setSensorOnlineStatus(boolean isSensorOnLine) {

        if (isSensorOnLine) {

            textViewSenSorStatus.setText(R.string.sensor_on_line);
            textViewSenSorStatus.setTextColor(Color.GREEN);
            textViewServerAddress.setTextColor(Color.GREEN);

            return;
        }

        textViewSenSorStatus.setText(R.string.sensor_off_line);
        textViewSenSorStatus.setTextColor(Color.RED);
        textViewNPK.setTextColor(Color.RED);
        textViewServerAddress.setTextColor(Color.RED);
    }

    private void showData() {

        if (npk_json.time == 0) {
            npk_json.time = Calendar.getInstance().getTimeInMillis();
        }

        Date date = new Date(npk_json.time);

        SimpleDateFormat df2;
        df2 = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.US);

        String textString = df2.format(date);
        textViewDateTime.setText(textString);

        textString = dfLatLon.format(npk_json.latitude) + ", " + dfLatLon.format(npk_json.longitude);
        textViewLatLon.setText(textString);

        textString = "Alt: " + dfAltitude.format(npk_json.altitude) + " m.";
        textViewAltitude.setText(textString);

        textString = "Acc: " + dfAltitude.format(npk_json.accuracy) + " m.";
        textViewAccuracy.setText(textString);

        if (npk_json.accuracy > 10.0) {
            textViewLatLon.setTextColor(Color.RED);
            textViewNPK.setTextColor(Color.RED);
        } else {
            textViewLatLon.setTextColor(Color.GREEN);
            textViewNPK.setTextColor(Color.GREEN);
        }

        textViewNPK.setText(NPKUtil.getNPKString());

        processNPKData();
    }

    private void processNPKData() {

        try {

            TextView textView = findViewById(R.id.textViewNNeeded);

            int n = Integer.parseInt(textView.getText().toString());

            textView = findViewById(R.id.textViewPNeeded);

            textView.setText(Integer.toString(NPKUtil.p));

            textView = findViewById(R.id.textViewKNeeded);

            textView.setText(Integer.toString(NPKUtil.k));

            int calAddValue = n - NPKUtil.nitrogen;

            double npkNeed = calAddValue;

            textView = findViewById(R.id.textViewNAdd);

            textView.setText(Integer.toString(calAddValue));

            calAddValue = NPKUtil.p - NPKUtil.potassium;

            textView = findViewById(R.id.textViewPAdd);

            textView.setText(Integer.toString(calAddValue));

            calAddValue = NPKUtil.k - NPKUtil.phosphorus;

            textView = findViewById(R.id.textViewKAdd);

            textView.setText(Integer.toString(calAddValue));

            textView = findViewById(R.id.textViewFertilizerAddValue);

            int fetilizerNeeded = (int) Math.round(NPKUtil.fetilizerNeeded(npkNeed, 160));

            textView.setText(KSFarmUtil.getCommaNumberFormat(fetilizerNeeded));

            EditText editTextFertilizerInstruction = findViewById(R.id.editTextFertilizerInstruction);

            String instruction = "ใส่ปุ๋ย 16-16-16 ครั้งละ 1 กำมือ\nโปรยรอบๆโคนต้น ทุก 30 วัน\nฉีดพ่น 16-16-16 100 กรัมต่อน้ำ 20 ลิตร ทุก 7 วัน";

            editTextFertilizerInstruction.setText(instruction);

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }
}