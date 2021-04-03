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

import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;

import java.util.ArrayList;


public class SoundFragment extends Fragment {
    Bundle bundle;
    String ipAddress;
    ArrayList<String> currentSoundVolAndBrightness;
    String setSoundVolumelRes;
    TextView tv_soundVolume;
    SeekBar sb_soundVolume;

    String setBrightnessRes;
    TextView tv_brightness;
    SeekBar sb_brightness;

    SwipeButton btn_shutdown;

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
        tv_brightness = getView().findViewById(R.id.tv_brightness);
        sb_brightness = getView().findViewById(R.id.sb_brightness);

        btn_shutdown = getView().findViewById(R.id.btn_shutdown);

        getCurrentSoundVolAndBrightness();

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

        sb_brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                setBrightness(progress);
                tv_brightness.setText(String.valueOf(progress));
            }
        });

        btn_shutdown.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                shutdownServer();
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        });
    }


    public void getCurrentSoundVolAndBrightness(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkConnection networkConnection
                        = new NetworkConnection(ipAddress, "{\"text\": \"getSoundVolAndBrightness\"}", getActivity());
                currentSoundVolAndBrightness = networkConnection.connect(2);


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_soundVolume.setText(currentSoundVolAndBrightness.get(0));
                        sb_soundVolume.setProgress(Integer.parseInt(currentSoundVolAndBrightness.get(0)));
                        int tempBrightness = (int)Double.parseDouble(currentSoundVolAndBrightness.get(1));
                        tv_brightness.setText(String.valueOf(tempBrightness));
                        sb_brightness.setProgress(tempBrightness);
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

    public void setBrightness (int brightness){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkConnection networkConnection
                        = new NetworkConnection(ipAddress, "{\"text\": \"setBrightness\", \"value\": \"" + brightness + "\" }" , getActivity());
                setBrightnessRes = networkConnection.connect();


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(setBrightnessRes.equals("success")) {
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

    public void shutdownServer (){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkConnection networkConnection
                        = new NetworkConnection(ipAddress, "{\"text\": \"shutdownServer\"}" , getActivity());
                setBrightnessRes = networkConnection.connect();
            }
        }).start();;
    }
}
