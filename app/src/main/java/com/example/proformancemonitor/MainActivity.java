package com.example.proformancemonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends AppCompatActivity {

    private EditText et_ip;
    private Button btn_go;
    String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        et_ip = findViewById(R.id.et_ip);
        btn_go = findViewById(R.id.btn_go);
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ipAddress = et_ip.getText().toString().trim();

                new checkNetworkConnection().execute(ipAddress);
            }
        });


    }

    private class checkNetworkConnection extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... param) {
            URL url = null;

            try {
                url = new URL(param[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(1000);
                int code = urlConnection.getResponseCode();

                if(code == 200){
                    Intent intent_good = new Intent(getApplicationContext(), ProformanceMonitoringActivity.class);
                    intent_good.putExtra("ipAddress", ipAddress);
                    startActivity(intent_good);
                }

                else {
                    Intent intent_error = new Intent(getApplicationContext(), ConnectionFailedActivity.class);
                    intent_error.putExtra("ipAddress", ipAddress);
                    startActivity(intent_error);

                }

            }catch (Exception e){
                e.printStackTrace();
                Intent intent_error = new Intent(getApplicationContext(), ConnectionFailedActivity.class);
                intent_error.putExtra("ipAddress", ipAddress);
                startActivity(intent_error);
            }
            return null;
        }
    }
}