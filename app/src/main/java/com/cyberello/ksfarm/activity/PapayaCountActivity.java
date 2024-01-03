package com.cyberello.ksfarm.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.cyberello.ksfarm.R;
import com.cyberello.ksfarm.data.KSFarmConstants;

public class PapayaCountActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_papaya_count);

        sharedPreferences = this.getSharedPreferences(KSFarmConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        });
    }
}