package com.cyberello.ksfarm.activity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.cyberello.global.CyberelloConstants;
import com.cyberello.ksfarm.KSFarmMeta;
import com.cyberello.ksfarm.R;
import com.cyberello.ksfarm.data.KSFarmConstants;
import com.cyberello.ksfarm.data.LonganQount;
import com.cyberello.ksfarm.util.KSFarmUtil;
import com.cyberello.ksfarm.webService.KSFarmWebService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.json.JSONObject;
import java.util.Objects;

public class LonganQountActivity extends AppCompatActivity implements KSFarmMeta.KSFarmMetaListener, KSFarmWebService.KSFarmWebServiceResultListener {

    private SharedPreferences sharedPreferences;
    private Location location;

    private EditText editTextLonganQountName;
    private EditText editQountNumberNonFlowerEditText;
    private EditText editQountNumberFlowerEditText;
    private EditText editQountNumberTotalEditText;
    private EditText editQountNumberTotalEditTextPercentage;
    private EditText editLonganQountTextEditText;
    private TextView editQountLastUpdateDateTextView;
    private TextView editQountLastUpdateTimeTextView;
    private TextView textViewLatLon;
    private TextView textViewAccuracy;

    private Vibrator vibrator;
    int textColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_longan_qount);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_longan_qount);

        sharedPreferences = LonganQountActivity.this.getSharedPreferences(KSFarmConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        VibratorManager vibratorManager = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
        vibrator = vibratorManager.getDefaultVibrator();

        setScreenComponents();

        setGPS();
    }

    @Override
    public void onResume() {
        super.onResume();

        KSFarmMeta.getLonganQountDataFromWebService(LonganQountActivity.this, LonganQountActivity.this);

        showQount();
    }

    private void setScreenComponents() {

        editTextLonganQountName = findViewById(R.id.editTextLonganQountName);
        editQountNumberNonFlowerEditText = findViewById(R.id.editQountNumberNonFlowerEditText);
        editQountNumberNonFlowerEditText.setFocusable(false);
        editQountNumberFlowerEditText = findViewById(R.id.editQountNumberFlowerEditText);
        editQountNumberFlowerEditText.setFocusable(false);
        editQountNumberTotalEditText = findViewById(R.id.editQountNumberTotalEditText);
        editQountNumberTotalEditTextPercentage = findViewById(R.id.editQountNumberTotalEditTextPercentage);
        editQountNumberTotalEditTextPercentage.setFocusable(false);
        editQountNumberTotalEditText.setFocusable(false);
        editLonganQountTextEditText = findViewById(R.id.editLonganQountTextEditText);
        editQountLastUpdateDateTextView = findViewById(R.id.editQountLastUpdateDateTextView);
        editQountLastUpdateTimeTextView = findViewById(R.id.editQountLastUpdateTimeTextView);

        textViewLatLon = findViewById(R.id.textViewLatLon);
        textViewAccuracy = findViewById(R.id.textViewAccuracy);

        textColor = textViewAccuracy.getCurrentTextColor();

        editTextLonganQountName.setOnFocusChangeListener((view, hasFocus) -> {

            if (!hasFocus) {

                LonganQount qount = KSFarmMeta.longanQount();

                if (qount.name.equals(editTextLonganQountName.getText().toString())) {

                    return;
                }

                qount.name = editTextLonganQountName.getText().toString();

                qount.event = "n";

                saveLonganQount(qount);
            }
        });

        editLonganQountTextEditText.setOnFocusChangeListener((view, hasFocus) -> {

            if (!hasFocus) {

                LonganQount qount = KSFarmMeta.longanQount();

                if (qount.text.equals(editLonganQountTextEditText.getText().toString())) {

                    return;
                }

                qount.text = editLonganQountTextEditText.getText().toString();

                qount.event = "t";

                saveLonganQount(qount);
            }
        });

        ImageView plusButtonQountFlower = findViewById(R.id.plusButtonQountFlower);

        plusButtonQountFlower.setOnClickListener(v -> {

            KSFarmUtil.beepPlus(vibrator);

            KSFarmMeta.longanQount().addNumberFlower(location);

            saveLonganQount(KSFarmMeta.longanQount());
        });

        ImageView minusButtonFlowerLonganQount = findViewById(R.id.minusButtonFlowerLonganQount);

        minusButtonFlowerLonganQount.setOnClickListener(v -> {

            int count = KSFarmUtil.parseInt(editQountNumberFlowerEditText.getText().toString());

            count -= 1;

            if (count < 0) {

                return;
            }

            new AlertDialog.Builder(LonganQountActivity.this)
                    .setIcon(android.R.drawable.ic_input_delete)
                    .setTitle("ลบจำนวน").setMessage("ลบจำนวนต้นออกดอก?")
                    .setPositiveButton("ไม่ลบ", null)
                    .setNegativeButton("ลบ", (dialog, which) -> {

                        KSFarmMeta.longanQount().minusNumberFlower(location);

                        saveLonganQount(KSFarmMeta.longanQount());
                    })
                    .show();
        });

        ImageView plusButtonAddQountNonFlower = findViewById(R.id.plusButtonAddQountNonFlower);

        plusButtonAddQountNonFlower.setOnClickListener(v -> {

            KSFarmUtil.beepPlus(vibrator);

            KSFarmMeta.longanQount().addNumberNonFlower(location);

            saveLonganQount(KSFarmMeta.longanQount());
        });

        ImageView minusButtonNonFlowerLonganQount = findViewById(R.id.minusButtonNonFlowerLonganQount);

        minusButtonNonFlowerLonganQount.setOnClickListener(v -> {

            int count = KSFarmUtil.parseInt(editQountNumberNonFlowerEditText.getText().toString());

            count -= 1;

            if (count < 0) {

                return;
            }

            new AlertDialog.Builder(LonganQountActivity.this)
                    .setIcon(android.R.drawable.ic_input_delete)
                    .setTitle("ลบจำนวน").setMessage("ลบจำนวนต้นไม่ออกดอก?")
                    .setPositiveButton("ไม่ลบ", null)
                    .setNegativeButton("ลบ", (dialog, which) -> {

                        KSFarmMeta.longanQount().minusNumberNonFlower(location);

                        saveLonganQount(KSFarmMeta.longanQount());
                    })
                    .show();
        });
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
                .addOnSuccessListener(this, this::setGPSLocation);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

                for (Location location : locationResult.getLocations()) {

                    setGPSLocation(location);
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

        if (item.getItemId() == R.id.longanQountResetZero) {

            new AlertDialog.Builder(LonganQountActivity.this)
                    .setTitle("ล้างข้อมูลนับต้น?").setMessage("ล้างข้อมูลนับทั้งหมด").setPositiveButton("No", null)
                    .setNegativeButton("Yes", (dialog, which) -> resetZero())
                    .show();

            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {

    }

    private void setGPSLocation(Location _location) {

        location = _location;

        showLocation();
    }

    private void showLocation() {

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

    private void showQount() {

        LonganQount longanQount = KSFarmMeta.longanQount();

        editTextLonganQountName.setText(longanQount.name);
        editQountNumberNonFlowerEditText.setText(KSFarmUtil.getCommaNumberFormat(longanQount.getNumberNonFlower()));
        editQountNumberFlowerEditText.setText(KSFarmUtil.getCommaNumberFormat(longanQount.getNumberFlower()));
        editQountNumberTotalEditText.setText(KSFarmUtil.getCommaNumberFormat(longanQount.getNumberNonFlower() + longanQount.getNumberFlower()));

        String percentageString = getFlowerPercentage(longanQount) + " %";

        editQountNumberTotalEditTextPercentage.setText(percentageString);
        editLonganQountTextEditText.setText(longanQount.text);
        editQountLastUpdateDateTextView.setText(longanQount.lastUpdateDateString);
        editQountLastUpdateTimeTextView.setText(longanQount.lastUpdateTimeString);
    }

    private String getFlowerPercentage(LonganQount longanQount) {

        double percentage = longanQount.getNumberFlower() + longanQount.getNumberNonFlower();

        percentage = longanQount.getNumberFlower() / percentage * 100;

        return Integer.toString((int) percentage);
    }

    private void saveLonganQount(LonganQount qount) {

        KSFarmMeta.saveLonganQount(qount, sharedPreferences, LonganQountActivity.this);
    }

    @Override
    public void saveLonganQountSuccess() {

        showQount();
    }

    private void resetZero() {

        KSFarmMeta.resetZero(location);

        saveLonganQount(KSFarmMeta.longanQount());

        showQount();
    }

    @Override
    public void processWebServiceResult(JSONObject response) {

        LonganQount longanQount = KSFarmUtil.getLonganQountFromJSONDataWrapper(response);

        if (longanQount.statusCode.equals(CyberelloConstants.STATUS_CODE_NEW)) {

            KSFarmMeta.loadLocalLonganQount(sharedPreferences);

            if (KSFarmMeta.longanQount().statusCode.equals(CyberelloConstants.STATUS_CODE_ACTIVE)) {

                KSFarmMeta.setLonganQountDataToWebService(LonganQountActivity.this, null);
            }

            showQount();
            return;
        }

        if (longanQount.equals(KSFarmMeta.longanQount())) {

            return;
        }

        if (KSFarmMeta.longanQount() == null || KSFarmMeta.longanQount().lastUpdate.before(longanQount.lastUpdate) || KSFarmMeta.longanQount().statusCode.equals(CyberelloConstants.STATUS_CODE_NEW)) {

            KSFarmMeta.saveLonganQount(KSFarmMeta.longanQount(), sharedPreferences, null);

            showQount();
        }
    }

    @Override
    public void onErrorResponse(String errorMessage) {

    }

    @Override
    public void onErrorResponse(String status, String errorMessage) {

    }
}