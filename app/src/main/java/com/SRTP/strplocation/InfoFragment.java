package com.SRTP.strplocation;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.SRTP.strplocation.GnssRawData.GnssContainer;
import com.SRTP.strplocation.GnssRawData.GnssListener;
import com.SRTP.strplocation.GnssRawData.UiLogger;
import com.SRTP.strplocation.GnssRawData.txtLogger;
import com.SRTP.strplocation.Listeners.MainViewListener;
import com.SRTP.strplocation.Listeners.SettingChangeListener;
import com.SRTP.strplocation.Utils.ScreenUtil;
import com.yinglan.scrolllayout.ScrollLayout;


public class InfoFragment extends Fragment implements MainViewListener{

    private FragmentTransaction transaction;
    private SettingsFragment settingsFragment;
    private LoggerFragment loggerFragment;
    private SharePosFragment sharePosFragment;
    private UiLogger logger_listener=new UiLogger();
    private NavigationActivity map_listener;
    private txtLogger txtLogger=new txtLogger();

    public InfoFragment() {
        // Required empty public constructor
    }

    public ScrollLayout mScrollLayout;

    private ScrollLayout.OnScrollChangedListener mOnScrollChangedListener = new ScrollLayout.OnScrollChangedListener() {
        @Override
        public void onScrollProgressChanged(float currentProgress) {
            if (currentProgress >= 0) {
                float precent = 200 * currentProgress;
                if (precent > 200) {
                    precent = 200;
                } else if (precent < 0) {
                    precent = 0;
                }
                System.out.println(currentProgress);
                mScrollLayout.getBackground().setAlpha(200 - (int) precent);
            }
        }

        @Override
        public void onScrollFinished(ScrollLayout.Status currentStatus) {
            if (currentStatus.equals(ScrollLayout.Status.EXIT)) {
                NavigationActivity navigationActivity= (NavigationActivity) getActivity();
                navigationActivity.logger.setIs_selected(false);
                navigationActivity.setting.setIs_selected(false);
            }
        }

        @Override
        public void onChildScroll(int top) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.info_fragment, container, false);
        mScrollLayout = view.findViewById(R.id.scroll_down_layout);
        initView();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        txtLogger.setDir(getActivity().getExternalFilesDir(null));
        transaction=getChildFragmentManager().beginTransaction();
        initAllFragments();
        hideAllFragments(transaction);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        if (context instanceof GnssListener)map_listener= (NavigationActivity) context;
        super.onAttach(context);
    }

    private void initView() {

        /**设置 setting*/
        mScrollLayout.setMinOffset(-60);
        mScrollLayout.setMaxOffset((int) (ScreenUtil.getScreenHeight(getActivity()) * 0.4));
        mScrollLayout.setExitOffset(ScreenUtil.dip2px(getContext(), -60));
        mScrollLayout.setIsSupportExit(true);
        mScrollLayout.setAllowHorizontalScroll(true);
        mScrollLayout.setOnScrollChangedListener(mOnScrollChangedListener);
        mScrollLayout.setToExit();

        mScrollLayout.getBackground().setAlpha(0);

    }

    private void hideAllFragments(FragmentTransaction transaction) {
        if (settingsFragment!=null){
            transaction.hide(settingsFragment);
        }
        if (loggerFragment!=null){
            transaction.hide(loggerFragment);
        }
        if (sharePosFragment!=null){
            transaction.hide(sharePosFragment);
        }
    }

    private void initAllFragments() {
        if (settingsFragment==null){
            settingsFragment=new SettingsFragment();
            settingsFragment.setGPSContainer(new GnssContainer(getActivity().getApplicationContext(),
                    logger_listener,map_listener));
            transaction.add(R.id.info_frame,settingsFragment);
        }
        if (loggerFragment==null){
            loggerFragment=new LoggerFragment();
            loggerFragment.setUiLogger(logger_listener);
            transaction.add(R.id.info_frame,loggerFragment);
        }
        if (sharePosFragment==null){
            sharePosFragment=new SharePosFragment();
            transaction.add(R.id.info_frame,sharePosFragment);
        }
    }

    @Override
    public void onBtnSettingClick() {
        FragmentTransaction transaction=getChildFragmentManager().beginTransaction();
        hideAllFragments(transaction);
        transaction.show(settingsFragment).commit();
    }

    @Override
    public void onBtnLoggerClick() {
        FragmentTransaction transaction=getChildFragmentManager().beginTransaction();
        hideAllFragments(transaction);
        transaction.show(loggerFragment).commit();
    }

    @Override
    public void onLaunchShareFragment() {
        FragmentTransaction transaction=getChildFragmentManager().beginTransaction();
        hideAllFragments(transaction);
        transaction.show(sharePosFragment).commit();
    }

    @Override
    public void onPause() {
        mScrollLayout.setToExit();
        super.onPause();
    }

}
