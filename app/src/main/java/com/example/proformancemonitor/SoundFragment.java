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

//sound/display fragment
public class SoundFragment extends Fragment {
    Bundle bundle;
    String ipAddress;
    ArrayList<String> currentSoundVolAndDisplayInfo;
    String setSoundVolumelRes;
    TextView tv_soundVolume;
    SeekBar sb_soundVolume;

    String setBrightnessRes;
    TextView tv_brightness;
    SeekBar sb_brightness;

    TextView tv_graphicCardModel;
    TextView tv_graphicCardBus;
    TextView tv_monitorResolution;
    TextView tv_monitorModel;
    TextView tv_monitorConnection;


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
        tv_graphicCardModel = getView().findViewById(R.id.tv_graphicCardModel);
        tv_graphicCardBus = getView().findViewById(R.id.tv_graphicCardBus);
        tv_monitorResolution = getView().findViewById(R.id.tv_monitorResolution);
        tv_monitorModel = getView().findViewById(R.id.tv_monitorModel);
        tv_monitorConnection = getView().findViewById(R.id.tv_monitorConnection);

        btn_shutdown = getView().findViewById(R.id.btn_shutdown);

        getCurrentSoundAndDisplayInfo();

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


    //handles static informations
    public void getCurrentSoundAndDisplayInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkConnection networkConnection
                        = new NetworkConnection(ipAddress, "{\"text\": \"getSoundAndDisplayInfo\"}", getActivity());
                currentSoundVolAndDisplayInfo  = networkConnection.connect(7);

                if (getActivity() != null) { //used to handel  java.lang.NullPointerException
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_soundVolume.setText(currentSoundVolAndDisplayInfo.get(0));
                            sb_soundVolume.setProgress(Integer.parseInt(currentSoundVolAndDisplayInfo.get(0)));
                            int tempBrightness = (int) Double.parseDouble(currentSoundVolAndDisplayInfo.get(1));
                            tv_brightness.setText(String.valueOf(tempBrightness));
                            sb_brightness.setProgress(tempBrightness);

                            tv_graphicCardModel.setText(currentSoundVolAndDisplayInfo.get(2));
                            tv_graphicCardBus.setText(currentSoundVolAndDisplayInfo.get(3));
                            tv_monitorResolution.setText(currentSoundVolAndDisplayInfo.get(4));
                            tv_monitorModel.setText(currentSoundVolAndDisplayInfo.get(5));
                            tv_monitorConnection.setText(currentSoundVolAndDisplayInfo.get(6));
                        }
                    });
                }
            }
        }).start();;
    }

    //handles the set sound volume request
    public void setSoundVoulme (int soundVolume){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkConnection networkConnection
                        = new NetworkConnection(ipAddress, "{\"text\": \"setSoundVolume\", \"value\": \"" + soundVolume + "\" }" , getActivity());
                setSoundVolumelRes = networkConnection.connect();

                if(getActivity() != null) { //used to handel  java.lang.NullPointerException
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (setSoundVolumelRes.equals("success")) {
                                //tv_soundVolume.setText(String.valueOf(soundVolume));
                            } else {
                                Intent intent_error = new Intent(getActivity(), ConnectionFailedActivity.class);
                                intent_error.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent_error.putExtra("ipAddress", ipAddress);
                                getActivity().startActivity(intent_error);
                            }
                        }
                    });
                }
            }
        }).start();;
    }

    //handles the set brightness volume request
    public void setBrightness (int brightness){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkConnection networkConnection
                        = new NetworkConnection(ipAddress, "{\"text\": \"setBrightness\", \"value\": \"" + brightness + "\" }" , getActivity());
                setBrightnessRes = networkConnection.connect();

                if(getActivity() != null) { //used to handel  java.lang.NullPointerException
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (setBrightnessRes.equals("success")) {
                                //tv_soundVolume.setText(String.valueOf(soundVolume));
                            } else {
                                Intent intent_error = new Intent(getActivity(), ConnectionFailedActivity.class);
                                intent_error.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent_error.putExtra("ipAddress", ipAddress);
                                getActivity().startActivity(intent_error);
                            }
                        }
                    });
                }
            }
        }).start();;
    }

    //handles the shutdown server request
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
