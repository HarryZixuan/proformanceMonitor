package com.example.proformancemonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProformanceMonitoringActivity extends AppCompatActivity {
    private Intent intent;
    private String ipAddress;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_proformance_monitoring);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);


        bottomNav.setOnNavigationItemSelectedListener(navListener);

        intent = getIntent();
        ipAddress = intent.getStringExtra("ipAddress");

        bundle =  new Bundle();
        bundle.putString("ipAddress", ipAddress);

        //set up default fragment
        Fragment defaultFragment = new CPUFragment();
        defaultFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                defaultFragment).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.nav_cpu:
                    selectedFragment = new CPUFragment();
                    selectedFragment.setArguments(bundle);
                    break;
                case R.id.nav_memory:
                    selectedFragment = new MemoryFragment();
                    selectedFragment.setArguments(bundle);
                    break;
                case R.id.nav_sound:
                    selectedFragment = new SoundFragment();
                    selectedFragment.setArguments(bundle);
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();
            return true;
        }
    };
}