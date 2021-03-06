package com.example.proformancemonitor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.BatchUpdateException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private EditText et_ip;
    private Button btn_go;
    private Button btn_scanQR;
    private String ipAddress;
    private ArrayList<String> connectionState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        et_ip = findViewById(R.id.et_ip);
        btn_go = findViewById(R.id.btn_go);
        btn_scanQR = findViewById(R.id.btn_scanQR);


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

        btn_scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setPrompt("scan the QR code displayed on your computer");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setCaptureActivity(QRCapture.class);
                intentIntegrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(intentResult.getContents() != null){
            et_ip.setText(intentResult.getContents());
        }
    }

    public void checkNetworkConnection(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //use cpuUsage to check network connection
                //network connection failed exception will be handled in NetworkConnection
                NetworkConnection networkConnection
                        = new NetworkConnection(ipAddress, "{\"text\": \"cpuUsage\"}", getApplicationContext());
                connectionState = networkConnection.connect(2);

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