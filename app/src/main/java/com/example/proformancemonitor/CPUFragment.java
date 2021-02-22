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
    Bundle bundle;
    String ipAddress;
    TextView tv_cpuUsage;
    int test = 0;
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
        System.out.println("created");

        final Handler handler = new Handler();
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        UpdateCpuInfo updateCpuInfo = new UpdateCpuInfo();
                        updateCpuInfo.execute(ipAddress);
                    }
                });
            }
        };
        timer.schedule(task, 0, 2000); //Every 2 second

    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }

    protected class UpdateCpuInfo extends AsyncTask <String, Void, String> {

        @Override
        protected String doInBackground(String... param) {
            URL url = null;

            try {
                url = new URL(param[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setConnectTimeout(1000);

                String jsonInputString = "{\"text\": \"cpuInfo\"}";

                try(OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }


                int code = urlConnection.getResponseCode();
                bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream));
                stringBuffer = new StringBuffer();
                String str = "";

                if(code == 200){
                    while ((str = bufferedReader.readLine()) != null){
                        stringBuffer.append(str);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    if (bufferedReader != null){
                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(stringBuffer != null){
                System.out.println(stringBuffer.toString());
            }
            else {
                System.out.println("string buffer is empty");
            }
            return stringBuffer.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject jsonObject = null;
            String cpuUsage = "cpuUsage";

            try {
                jsonObject = new JSONObject(s);
                cpuUsage = jsonObject.getString("text");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            tv_cpuUsage.setText(cpuUsage);
        }
    }
}
