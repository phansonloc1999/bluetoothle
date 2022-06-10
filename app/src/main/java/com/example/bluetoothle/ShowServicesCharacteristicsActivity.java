package com.example.bluetoothle;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ShowServicesCharacteristicsActivity extends AppCompatActivity {
    private static ListView servicesCharacteristicsListView = null;
    private static ArrayAdapter<String> servicesCharacteristicsArrAdapter = null;
    private static ArrayList<String> servicesCharacteristicsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_services_characteristics);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        servicesCharacteristicsListView = findViewById(R.id.servicesCharacteristicsListView);
        servicesCharacteristicsList = new ArrayList<>();
        servicesCharacteristicsArrAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, servicesCharacteristicsList);
        servicesCharacteristicsListView.setAdapter(servicesCharacteristicsArrAdapter);

        Intent intent = getIntent();
        int serviceCount = intent.getIntExtra("SERVICE_COUNT", 0);
        for (int i = 0; i < serviceCount; i++) {
            servicesCharacteristicsArrAdapter.add(intent.getStringExtra("SERVICE_" + i));
        }
        servicesCharacteristicsArrAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
