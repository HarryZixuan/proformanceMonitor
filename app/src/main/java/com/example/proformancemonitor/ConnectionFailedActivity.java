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

public class ConnectionFailedActivity extends AppCompatActivity {
    //private TextView tv_errorMessage;
    private Button btn_ok;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.connection_failed);

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Intent intent = getIntent();

        String ipAddress = intent.getStringExtra("ipAddress");

        //tv_errorMessage = findViewById(R.id.tv_errorMessage);
        //tv_errorMessage.setText("cannot connect to: " + ipAddress);

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
