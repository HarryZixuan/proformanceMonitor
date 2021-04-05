package com.example.proformancemonitor;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class NetworkFragment extends Fragment {
    private Bundle bundle;
    private String ipAddress;
    private Timer timer;
    private TimerTask task;

    ArrayList<String> networkUsageList;
    private GraphView gv_networkUsage;
    private LineGraphSeries<DataPoint> networkTxSeries;
    private LineGraphSeries<DataPoint> networkRxSeries;
    private LineGraphSeries<DataPoint> networkLatencySeries;
    private int timeCounter =0;

    ArrayList<String> networkInfoList;
    private TextView tv_networkIp4;
    private TextView tv_networkIp6;
    private TextView tv_networkType;
    private TextView tv_networkSpeed;
    private TextView tv_networkVirtual;
    private TextView tv_networkLatency;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bundle = this.getArguments();
        ipAddress = "";
        if (bundle != null){
            ipAddress = bundle.getString("ipAddress");
            System.out.println("IPAddress: " + ipAddress);
        }

        return inflater.inflate(R.layout.fragment_network,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_networkIp4 = getView().findViewById(R.id.tv_networkIp4);
        tv_networkIp6 = getView().findViewById(R.id.tv_networkIp6);
        tv_networkType = getView().findViewById(R.id.tv_networkType);
        tv_networkSpeed = getView().findViewById(R.id.tv_networkSpeed);
        tv_networkVirtual = getView().findViewById(R.id.tv_networkVirtual);
        tv_networkLatency = getView().findViewById(R.id.tv_networkLatency);
        updateNetworkInfo();

        gv_networkUsage = getView().findViewById(R.id.gv_networkUsage);
        gv_networkUsage.setPivotX(17);
        networkTxSeries = new LineGraphSeries<DataPoint>();
        networkTxSeries.setColor(Color.rgb(235,204,195));
        networkTxSeries.setThickness(8);
        networkTxSeries.setTitle("Upload Spd, Mb/s");
        networkRxSeries = new LineGraphSeries<DataPoint>();
        networkRxSeries.setColor(Color.rgb(117,216,190));
        networkRxSeries.setThickness(8);
        networkRxSeries.setTitle("Download Spd, Mb/s");
        networkLatencySeries = new LineGraphSeries<DataPoint>();
        networkLatencySeries.setColor(Color.rgb(199,76,105));
        networkLatencySeries.setThickness(8);
        networkLatencySeries.setTitle("Latency, ms");

        gv_networkUsage.addSeries(networkRxSeries);
        gv_networkUsage.addSeries(networkTxSeries);
        gv_networkUsage.addSeries(networkLatencySeries);

        gv_networkUsage.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        gv_networkUsage.getGridLabelRenderer().setHorizontalAxisTitleTextSize(5);
        gv_networkUsage.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        gv_networkUsage.getGridLabelRenderer().setVerticalAxisTitleTextSize(5);
        gv_networkUsage.getLegendRenderer().setVisible(true);
        gv_networkUsage.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        gv_networkUsage.getLegendRenderer().setTextSize(40);
        gv_networkUsage.getLegendRenderer().setBackgroundColor(Color.argb(150, 36, 37, 59));
        gv_networkUsage.getLegendRenderer().setTextColor(Color.WHITE);
        Viewport viewport = gv_networkUsage.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(500);
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
                        updateNetworkUsage();
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

    public void updateNetworkInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkConnection networkConnection
                        = new NetworkConnection(ipAddress, "{\"text\": \"networkInfo\"}", getActivity());
                networkInfoList = networkConnection.connect(5);

                if(getActivity() != null){ //used to handel  java.lang.NullPointerException
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_networkIp4.setText(networkInfoList.get(0));
                            tv_networkIp6.setText(networkInfoList.get(1));
                            tv_networkType.setText(networkInfoList.get(2));
                            tv_networkSpeed.setText(networkInfoList.get(3));
                            tv_networkVirtual.setText(networkInfoList.get(4));
                        }
                    });
                }}
        }).start();;
    }

    public void updateNetworkUsage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkConnection networkConnection
                        = new NetworkConnection(ipAddress, "{\"text\": \"networkUsage\"}", getActivity());
                networkUsageList = networkConnection.connect(3);


                if (getActivity() != null) { //used to handel  java.lang.NullPointerException
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            double networkTxbits;
                            try {
                                networkTxbits = Double.parseDouble(networkUsageList.get(0));
                                networkTxbits = (networkTxbits*8)/(Math.pow(1024, 2));
                            } catch (Exception e) {
                                networkTxbits = 1;
                            }

                            double networkRxbits;
                            try {
                                networkRxbits = Double.parseDouble(networkUsageList.get(1));
                                networkRxbits = (networkRxbits*8)/(Math.pow(1024, 2));
                            } catch (Exception e) {
                                networkRxbits = 1;
                            }

                            int networkLatnecyMs;
                            try {
                                networkLatnecyMs = Integer.parseInt(networkUsageList.get(2));
                                tv_networkLatency.setText(networkLatnecyMs + " ms");
                                addGraphViewEntry(networkTxbits, networkRxbits, networkLatnecyMs);
                            } catch (Exception e) {
                                tv_networkLatency.setText("N/A");
                                addGraphViewEntry(networkTxbits, networkRxbits);
                            }
                        }
                    });
                }
            }
        }).start();
    }

    public void addGraphViewEntry(double tx, double rx, int latency){
        if(timeCounter < 35) {
            networkTxSeries.appendData(new DataPoint(timeCounter, tx), false, 60);
            networkRxSeries.appendData(new DataPoint(timeCounter, rx), false, 60);
            networkLatencySeries.appendData(new DataPoint(timeCounter, latency), false, 60);
        }
        else {
            networkTxSeries.appendData(new DataPoint(timeCounter, tx), true, 60);
            networkRxSeries.appendData(new DataPoint(timeCounter, rx), true, 60);
            networkLatencySeries.appendData(new DataPoint(timeCounter, latency), true, 60);
        }
        timeCounter ++;
    }
    //overload this method, for the case there is no network connection
    public void addGraphViewEntry(double tx, double rx){
        if(timeCounter < 35) {
            networkTxSeries.appendData(new DataPoint(timeCounter, tx), false, 60);
            networkRxSeries.appendData(new DataPoint(timeCounter, rx), false, 60);
        }
        else {
            networkTxSeries.appendData(new DataPoint(timeCounter, tx), true, 60);
            networkRxSeries.appendData(new DataPoint(timeCounter, rx), true, 60);
        }
        timeCounter ++;
    }
}