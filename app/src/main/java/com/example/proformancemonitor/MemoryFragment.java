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


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MemoryFragment extends Fragment {
    private Bundle bundle;
    private String ipAddress;
    private String memoryUsage = "";
    private Timer timer;
    private TimerTask task;

    ArrayList<String> memoryUsageList;
    private GraphView gv_memoryUsage;
    private LineGraphSeries<DataPoint> memoryUsageSeries;
    private int timeCounter =0;

    private TextView tv_memoryTotal;
    private TextView tv_memoryActive;
    private TextView tv_memoryFree;
    private TextView tv_memoryUsed;
    private TextView tv_memorySwapUsed;
    private TextView tv_memorySwapFree;


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


        return inflater.inflate(R.layout.fragment_memory,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //handle memory info
        tv_memoryTotal = getView().findViewById(R.id.tv_memoryTotal);
        tv_memoryActive = getView().findViewById(R.id.tv_memoryActive);
        tv_memoryFree = getView().findViewById(R.id.tv_memoryFree);
        tv_memoryUsed = getView().findViewById(R.id.tv_memoryUsed);
        tv_memorySwapUsed = getView().findViewById(R.id.tv_memorySwapUsed);
        tv_memorySwapFree = getView().findViewById(R.id.tv_memorySwapFree);

        //handle memory usage
        gv_memoryUsage = getView().findViewById(R.id.gv_memoryUsage);
        gv_memoryUsage.setPivotX(17);
        memoryUsageSeries = new LineGraphSeries<DataPoint>();
        memoryUsageSeries.setColor(Color.rgb(235,204,195));
        memoryUsageSeries.setThickness(8);
        memoryUsageSeries.setTitle("memory Usage");


        gv_memoryUsage.addSeries(memoryUsageSeries);


        //set graph view format
        gv_memoryUsage.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        gv_memoryUsage.getGridLabelRenderer().setHorizontalAxisTitleTextSize(5);
        gv_memoryUsage.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        gv_memoryUsage.getGridLabelRenderer().setVerticalAxisTitleTextSize(5);
        gv_memoryUsage.getLegendRenderer().setVisible(true);
        gv_memoryUsage.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        gv_memoryUsage.getLegendRenderer().setTextSize(40);
        gv_memoryUsage.getLegendRenderer().setBackgroundColor(Color.argb(150, 36, 37, 59));
        gv_memoryUsage.getLegendRenderer().setTextColor(Color.WHITE);
        Viewport viewport = gv_memoryUsage.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(100);
        viewport.setScrollable(true);

        viewport.setMinX(0);
        viewport.setMaxX(35);
        viewport.setScalable(true);


        //set up timer, grant memory usage every second
        final Handler handler = new Handler();
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        updateMemoryUsage();
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

    public void updateMemoryUsage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkConnection networkConnection
                        = new NetworkConnection(ipAddress, "{\"text\": \"memoryInfo\"}", getActivity());
                memoryUsageList = networkConnection.connect(6);


                if(getActivity() != null) { //used to handel  java.lang.NullPointerException
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //the server will return memory info by String
                            //first, convert String to double, and calculate the memory usage
                            ArrayList<Double> memoryUsageDoubleList = new ArrayList<Double>();
                            for (String str : memoryUsageList) {
                                try {
                                    memoryUsageDoubleList.add(Double.parseDouble(str));
                                } catch (Exception e) {
                                    memoryUsageDoubleList.add(-1d);
                                }
                            }
                            //second update the GUI, use convertBitsToGbytes to convert double value to string
                            addGraphViewEntry(memoryUsageDoubleList.get(3) / memoryUsageDoubleList.get(0));
                            tv_memoryTotal.setText(convertBitsToGbytes(memoryUsageDoubleList.get(0)));
                            tv_memoryActive.setText(convertBitsToGbytes(memoryUsageDoubleList.get(1)));
                            tv_memoryFree.setText(convertBitsToGbytes(memoryUsageDoubleList.get(2)));
                            tv_memoryUsed.setText(convertBitsToGbytes(memoryUsageDoubleList.get(3)));
                            tv_memorySwapUsed.setText(convertBitsToGbytes(memoryUsageDoubleList.get(4)));
                            tv_memorySwapFree.setText(convertBitsToGbytes(memoryUsageDoubleList.get(5)));
                        }
                    });
                }
            }
        }).start();;
    }

    public String convertBitsToGbytes(double val){
        if(val < 0){ //the item is not available
            return "N/A";
        }
        else {
            DecimalFormat df = new DecimalFormat("#.##");
            double tempVal = val/(Math.pow(1024, 3));
            System.out.println(tempVal);
            return df.format(tempVal) + " GB";
        }

    }

    public void addGraphViewEntry(double memoryUsage){
        //set scrollToEnd to false if timecounter < 35,
        //otherwise, the graph display negative x scale at beginning
        if(timeCounter < 35) {
            memoryUsageSeries.appendData(new DataPoint(timeCounter, memoryUsage*100), false, 60);
        }
        else {
            memoryUsageSeries.appendData(new DataPoint(timeCounter, memoryUsage*100), true, 60);
        }
        timeCounter ++;

    }
}
