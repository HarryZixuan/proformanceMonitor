package com.example.proformancemonitor;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//A class to handle all the network connection Fails

public class ConnectionFailedActivity extends AppCompatActivity {
    private Button btn_ok;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.connection_failed);

        Intent intent = getIntent();

        String ipAddress = intent.getStringExtra("ipAddress");

        btn_ok = findViewById(R.id.btn_errorOK);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //return to main activity
                Intent intent_main = new Intent(getApplicationContext(), MainActivity.class);
                intent_main.putExtra("ipAddress", ipAddress);
                startActivity(intent_main);

            }
        });
    }
}
