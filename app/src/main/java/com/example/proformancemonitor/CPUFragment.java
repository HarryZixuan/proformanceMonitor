package com.example.proformancemonitor;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class CPUFragment extends Fragment {
    private Bundle bundle;
    private String ipAddress;
    private String cpuUsage = "";
    private TextView tv_cpuUsage;
    private Timer timer;
    private TimerTask task;
    private BufferedInputStream bufferedInputStream;
    private BufferedReader bufferedReader;
    private StringBuffer stringBuffer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        bundle = this.getArguments();
        ipAddress = "";

        if (bundle != null){
            ipAddress = bundle.getString("ipAddress");
            System.out.println("IPAddress: " + ipAddress);
        }


        return inflater.inflate(R.layout.fragment_cpu,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_cpuUsage = getView().findViewById(R.id.tv_cpuUsage);

        final Handler handler = new Handler();
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        //UpdateCpuInfo updateCpuInfo = new UpdateCpuInfo();
                        //updateCpuInfo.execute(ipAddress);
                        //NetworkConnection networkConnection = new NetworkConnection(ipAddress, "{\"text\": \"cpuInfo\"}");
                        //String responseStr = networkConnection.connect();
                        //System.out.println("tv: " + responseStr);
                        //tv_cpuUsage.setText(responseStr);
                        updateCPUInfo();
                    }
                });
            }

        };
        timer.schedule(task, 0, 2000); //Every 2 second

    }

    public void updateCPUInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkConnection networkConnection
                        = new NetworkConnection(ipAddress, "{\"text\": \"cpuInfo\"}", getActivity());
                cpuUsage = networkConnection.connect();


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_cpuUsage.setText(cpuUsage);
                    }
                });
            }
        }).start();;
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }

}
