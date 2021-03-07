package com.example.proformancemonitor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class SoundFragment extends Fragment {
    Bundle bundle;
    String ipAddress;
    String currentSoundVolume;
    String setSoundVolumelRes;
    TextView tv_soundVolume;
    SeekBar sb_soundVolume;

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
        return inflater.inflate(R.layout.fragment_sound,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_soundVolume = getView().findViewById(R.id.tv_soundVolume);
        sb_soundVolume = getView().findViewById(R.id.sb_soundVolume);
        getCurrentSoundVolume();

        sb_soundVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv_soundVolume.setText("" + i + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                setSoundVoulme(progress);
                tv_soundVolume.setText(String.valueOf(progress));
            }
        });
    }


    public void getCurrentSoundVolume(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkConnection networkConnection
                        = new NetworkConnection(ipAddress, "{\"text\": \"getSoundVolume\"}", getActivity());
                currentSoundVolume = networkConnection.connect();


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_soundVolume.setText(currentSoundVolume);
                        sb_soundVolume.setProgress(Integer.parseInt(currentSoundVolume));
                    }
                });
            }
        }).start();;
    }

    public void setSoundVoulme (int soundVolume){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkConnection networkConnection
                        = new NetworkConnection(ipAddress, "{\"text\": \"setSoundVolume\", \"value\": \"" + soundVolume + "\" }" , getActivity());
                setSoundVolumelRes = networkConnection.connect();


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(setSoundVolumelRes.equals("success")) {
                            //tv_soundVolume.setText(String.valueOf(soundVolume));
                        }
                        else {
                            Intent intent_error = new Intent(getActivity(), ConnectionFailedActivity.class);
                            intent_error.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent_error.putExtra("ipAddress", ipAddress);
                            getActivity().startActivity(intent_error);
                        }
                    }
                });
            }
        }).start();;
    }

}
