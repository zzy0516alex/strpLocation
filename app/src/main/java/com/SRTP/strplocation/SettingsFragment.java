package com.SRTP.strplocation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.SRTP.strplocation.GnssRawData.GnssContainer;
import com.SRTP.strplocation.GnssRawData.NavigationDataReceiver;
import com.SRTP.strplocation.Listeners.SettingChangeListener;
import com.SRTP.strplocation.Utils.FileUtil;
import com.SRTP.strplocation.Utils.SettingsUtil;
import com.SRTP.strplocation.Views.customButton;
import com.github.iielse.switchbutton.SwitchView;

import com.SRTP.strplocation.GnssRawData.NavigationDataReceiver.DataReceiveHandler;
public class SettingsFragment extends Fragment {
    private GnssContainer mGPSContainer;//申明一个存放gnss测量信息的记录器
    private NavigationDataReceiver navigationDataReceiver;
    private DataReceiveHandler handler;
    private SettingChangeListener listener;
    //

    public void setGPSContainer(GnssContainer mGPSContainer) {
        this.mGPSContainer = mGPSContainer;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container, false /* attachToRoot */);
        //Location开启开关
        final SwitchView registerLocation = view.findViewById(R.id.register_location);
        registerLocation.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                if (SettingsUtil.isLocationEnabled(getActivity())) {
                    view.toggleSwitch(true);
                    mGPSContainer.registerLocation();
                }else {
                    view.toggleSwitch(false);
                    SettingsUtil.switch_to_setting(getActivity());
                }
            }

            @Override
            public void toggleToOff(SwitchView view) {
                view.toggleSwitch(false);
                mGPSContainer.unRegisterLocation();
            }
        });
        //
        //measurement开启开关
        final SwitchView registerMeasurements = view.findViewById(R.id.register_measurements);
        registerMeasurements.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(final SwitchView view) {
                view.toggleSwitch(true);
                mGPSContainer.RegisterMeasurements();
                FileUtil.clearInfoForFile(getActivity().getExternalFilesDir(null)+"/PseuData"+".txt");
            }

            @Override
            public void toggleToOff(SwitchView view) {
                view.toggleSwitch(false);
                mGPSContainer.unRegisterMeasurements();
            }
        });

        final SwitchView start_diff_location = view.findViewById(R.id.register_diff_location);
        start_diff_location.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(final SwitchView view) {
                view.toggleSwitch(true);
                if (!registerMeasurements.isOpened()){
                    mGPSContainer.RegisterMeasurements();
                    registerMeasurements.setOpened(true);
                }
                FileUtil.clearInfoForFile(getActivity().getExternalFilesDir(null)+"/PosData"+".txt");
                listener.onDiffLocateSwitch(true);
                handler = new DataReceiveHandler(new DataReceiveHandler.DataReceiveListener() {
                    @Override
                    public void onError() {
                        Toast.makeText(getContext(), "无法连接基站", Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        view.toggleSwitch(false);
                    }

                    @Override
                    public void onConnect() {
                        Toast.makeText(getContext(), "成功连接基站", Toast.LENGTH_SHORT).show();
                    }
                });
                navigationDataReceiver=new NavigationDataReceiver(getContext(),handler);
                navigationDataReceiver.start();
            }

            @Override
            public void toggleToOff(SwitchView view) {
                view.toggleSwitch(false);
                listener.onDiffLocateSwitch(false);
                navigationDataReceiver.interrupt();
            }
        });


        final SwitchView need_clear_btn=view.findViewById(R.id.can_clear_map);
        need_clear_btn.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                view.toggleSwitch(true);
                listener.onMapDrawModChange(true);
            }

            @Override
            public void toggleToOff(SwitchView view) {
                view.toggleSwitch(false);
                listener.onMapDrawModChange(false);
            }
        });

        final RelativeLayout share_pos=view.findViewById(R.id.share_pos);
        share_pos.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_UP:
                        share_pos.setBackgroundColor(Color.WHITE);
                        //click
                        listener.onLaunchSharePage();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        share_pos.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.click_gray));
                        break;
                    default:
                }
                return true;
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        if (context instanceof SettingChangeListener)listener= (SettingChangeListener) context;
        super.onAttach(context);
    }

}

