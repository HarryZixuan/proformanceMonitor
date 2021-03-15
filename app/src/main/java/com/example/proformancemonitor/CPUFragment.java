package com.example.proformancemonitor;

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
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import java.util.Timer;
import java.util.TimerTask;

public class CPUFragment extends Fragment {
    private Bundle bundle;
    private String ipAddress;
    private String cpuUsage = "";
    private TextView tv_cpuUsage;
    private Timer timer;
    private TimerTask task;

    private GraphView gv_cpuUsage;
    private LineGraphSeries<DataPoint> lineGraphSeries;
    private int timeCounter =0;



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

        gv_cpuUsage = getView().findViewById(R.id.gv_cpuUsage);
        gv_cpuUsage.setPivotX(17);
        lineGraphSeries = new LineGraphSeries<DataPoint>();
        gv_cpuUsage.addSeries(lineGraphSeries);



        //set graph view format
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
                        updateCPUInfo();
                    }
                });
            }

        };
        timer.schedule(task, 0, 1000); //Every 1 second

    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
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
                        addGraphViewEntry(Double.parseDouble(cpuUsage));
                    }
                });
            }
        }).start();;
    }

    public void addGraphViewEntry(double cpuUsage){
        //set scrollToEnd to false if timecounter < 35,
        //otherwise, the graph display negative x scale at beginning
        if(timeCounter < 35) {
            lineGraphSeries.appendData(new DataPoint(timeCounter, cpuUsage), false, 60);
        }
        else {
            lineGraphSeries.appendData(new DataPoint(timeCounter, cpuUsage), true, 60);
        }
        timeCounter ++;

    }



}
