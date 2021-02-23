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
    private String ipAddress;
    private String connectionState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_ip = findViewById(R.id.et_ip);
        btn_go = findViewById(R.id.btn_go);

        //used to handle netwoek connection failed case
        //will keep the current ipAdress
        Intent intent = getIntent();
        if(intent != null) {
            ipAddress = intent.getStringExtra("ipAddress");
            et_ip.setText(ipAddress);
        }

        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ipAddress = et_ip.getText().toString().trim();
                checkNetworkConnection();
            }
        });
    }

    public void checkNetworkConnection(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //use cpuInfo to check network connection
                //network connection failed exception will be handled in NetworkConnection
                NetworkConnection networkConnection
                        = new NetworkConnection(ipAddress, "{\"text\": \"cpuInfo\"}", getApplicationContext());
                connectionState = networkConnection.connect();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(connectionState != null){
                            Intent intent_good = new Intent(getApplicationContext(), ProformanceMonitoringActivity.class);
                            intent_good.putExtra("ipAddress", ipAddress);
                            startActivity(intent_good);
                        }
                        else {
                            Intent intent_error = new Intent(getApplicationContext(), ConnectionFailedActivity.class);
                            intent_error.putExtra("ipAddress", ipAddress);
                            startActivity(intent_error);
                        }
                    }
                });
            }
        }).start();;

    }

}