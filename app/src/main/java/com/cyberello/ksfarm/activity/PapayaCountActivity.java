package com.cyberello.ksfarm.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.cyberello.ksfarm.R;
import com.cyberello.ksfarm.data.KSFarmConstants;
import com.cyberello.ksfarm.data.PapayaMeta;

public class PapayaCountActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    Spinner spinnerRowName;

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

        PapayaMeta papayaMeta = PapayaMeta.getPapayaMeta();

        Spinner spinner = (Spinner) findViewById(R.id.spinnerPapayaCountPlotName);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, papayaMeta.plotNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner = (Spinner) findViewById(R.id.spinnerPapayaCountRowName);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, papayaMeta.plotRows.get(0));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinnerRowName = spinner;
    }
}