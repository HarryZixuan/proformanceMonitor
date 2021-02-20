package com.example.proformancemonitor;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ConnectionFailedActivity extends Activity {
    private TextView tv_errorMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_failed);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Intent intent = getIntent();

        String ipAddress = intent.getStringExtra("ipAddress");

        tv_errorMessage = findViewById(R.id.tv_errorMessage);
        tv_errorMessage.setText("cannot connect to: " + ipAddress);
    }
}
