package com.example.proformancemonitor;

import android.content.Context;
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

import java.util.Timer;
import java.util.TimerTask;

public class CPUFragment extends Fragment {
    Bundle bundle;
    String ipAddress;
    TextView tv_cpuUsage;
    int test = 0;
    private Timer timer;
    private TimerTask task;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        bundle = this.getArguments();
        ipAddress = "";

        if (bundle != null){
            ipAddress = bundle.getString("ipAddress");
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
                        updateCpuInfo mAsync = new updateCpuInfo();
                        mAsync.execute();
                    }
                });
            }
        };
        timer.schedule(task, 0, 1000); //Every 1 second


    }

    @Override
    public void onResume() {
        super.onResume();
    }
    protected class updateCpuInfo extends AsyncTask <String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            test ++;
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            tv_cpuUsage.setText(String.valueOf(test));
        }
    }
}
