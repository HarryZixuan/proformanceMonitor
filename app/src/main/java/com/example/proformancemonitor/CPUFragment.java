package com.example.proformancemonitor;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CPUFragment extends Fragment {
    private Bundle bundle;
    private String ipAddress;
    private String cpuUsage = "";
    private TextView tv_cpuUsage;
    private Timer timer;
    private TimerTask task;

    ArrayList<String> cpuUsageList;
    private GraphView gv_cpuUsage;
    private LineGraphSeries<DataPoint> cpuUsageSeries;
    private LineGraphSeries<DataPoint> cpuTempertureSeries;
    private int timeCounter =0;

    ArrayList<String> cpuInfoList;
    private TextView tv_cpuManu;
    private TextView tv_cpuBrand;
    private TextView tv_cpuSpeed;
    private TextView tv_cpuSocket;
    private TextView tv_cpuNrofPhyCores;
    private TextView tv_cpuNrofCores;


//CPU Fragment

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        //handle cpu info
        tv_cpuManu = getView().findViewById(R.id.tv_cpuManu);
        tv_cpuBrand = getView().findViewById(R.id.tv_cpuBrand);
        tv_cpuSpeed = getView().findViewById(R.id.tv_cpuSpeed);
        tv_cpuSocket = getView().findViewById(R.id.tv_cpuSocket);
        tv_cpuNrofPhyCores = getView().findViewById(R.id.tv_cpuNrofPhyCores);
        tv_cpuNrofCores = getView().findViewById(R.id.tv_cpuNrofCores);
        updateCPUInfo();


        tv_cpuUsage = getView().findViewById(R.id.tv_cpuUsage); //for debugging, will not be displayed on GUI
        gv_cpuUsage = getView().findViewById(R.id.gv_cpuUsage);

        //set graph view format
        gv_cpuUsage.setPivotX(17);
        cpuUsageSeries = new LineGraphSeries<DataPoint>();
        cpuUsageSeries.setColor(Color.rgb(235,204,195));
        cpuUsageSeries.setThickness(8);
        cpuUsageSeries.setTitle("CPU Usage");
        cpuTempertureSeries = new LineGraphSeries<DataPoint>();
        cpuTempertureSeries.setColor(Color.rgb(117,216,190));
        cpuTempertureSeries.setThickness(8);
        cpuTempertureSeries.setTitle("CPU Temperature");

        gv_cpuUsage.addSeries(cpuUsageSeries);
        gv_cpuUsage.addSeries(cpuTempertureSeries);

        gv_cpuUsage.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        gv_cpuUsage.getGridLabelRenderer().setHorizontalAxisTitleTextSize(5);
        gv_cpuUsage.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        gv_cpuUsage.getGridLabelRenderer().setVerticalAxisTitleTextSize(5);
        gv_cpuUsage.getLegendRenderer().setVisible(true);
        gv_cpuUsage.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        gv_cpuUsage.getLegendRenderer().setTextSize(40);
        gv_cpuUsage.getLegendRenderer().setBackgroundColor(Color.argb(150, 36, 37, 59));
        gv_cpuUsage.getLegendRenderer().setTextColor(Color.WHITE);
        Viewport viewport = gv_cpuUsage.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(100);
        viewport.setScrollable(true);

        viewport.setMinX(0);
        viewport.setMaxX(35);
        viewport.setScalable(true);


        //set up timer, grant cpu usage every second
        final Handler handler = new Handler();
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        updateCPUUsage();
                    }
                });
            }

        };
        timer.schedule(task, 0, 1000); //Every 1 second
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();  //cancel the timer, ie, stop sending the request when the Fragment is no longer started
    }

    public void updateCPUInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkConnection networkConnection
                        = new NetworkConnection(ipAddress, "{\"text\": \"cpuInfo\"}", getActivity());
                cpuInfoList = networkConnection.connect(6);

                if(getActivity() != null){ //used to handel  java.lang.NullPointerException
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_cpuManu.setText(cpuInfoList.get(0));
                            tv_cpuBrand.setText(cpuInfoList.get(1));
                            tv_cpuSpeed.setText(cpuInfoList.get(2));
                            tv_cpuSocket.setText(cpuInfoList.get(3));
                            tv_cpuNrofPhyCores.setText(cpuInfoList.get(4));
                            tv_cpuNrofCores.setText(cpuInfoList.get(5));
                        }
                    });
            }}
        }).start();;
    }

    public void updateCPUUsage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkConnection networkConnection
                        = new NetworkConnection(ipAddress, "{\"text\": \"cpuUsage\"}", getActivity());
                cpuUsageList = networkConnection.connect(2);


                if (getActivity() != null) { //used to handel  java.lang.NullPointerException
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_cpuUsage.setText(cpuUsage);

                            //some system cannot get cpu temperature
                            //add try catch to handle the exception
                            double cpuTemper;
                            try {
                                cpuTemper = Double.parseDouble(cpuUsageList.get(1));
                            } catch (Exception e) {
                                cpuTemper = 0;
                            }

                            addGraphViewEntry(Double.parseDouble(cpuUsageList.get(0)), cpuTemper);
                        }
                    });
                }
            }
        }).start();

    }

    public void addGraphViewEntry(double cpuUsage, double cpuTemper){
        //set scrollToEnd to false if timecounter < 35,
        //otherwise, the graph displays negative x scale at beginning
        if(timeCounter < 35) {
            cpuUsageSeries.appendData(new DataPoint(timeCounter, cpuUsage), false, 60);
            cpuTempertureSeries.appendData(new DataPoint(timeCounter, cpuTemper), false, 60);
        }
        else {
            cpuUsageSeries.appendData(new DataPoint(timeCounter, cpuUsage), true, 60);
            cpuTempertureSeries.appendData(new DataPoint(timeCounter, cpuTemper), true, 60);
        }
        timeCounter ++;

    }

}
