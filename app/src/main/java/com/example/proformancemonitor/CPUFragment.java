package com.example.proformancemonitor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CPUFragment extends Fragment {

    View view;

    Bundle bundle;
    String ipAddress;
    TextView tv_cpuUsage;
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
    public void onResume() {
        super.onResume();

        view = getView();

        tv_cpuUsage = view.findViewById(R.id.tv_cpuUsage);
        int test = 0;

        while(isAdded()){
            test ++;

            tv_cpuUsage.setText(test);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
