package com.SRTP.strplocation;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.SRTP.strplocation.Adapters.SatelliteShowAdapter;
import com.SRTP.strplocation.GnssCalculator.SatSentMsg;
import com.SRTP.strplocation.GnssRawData.UiLogger;
import com.SRTP.strplocation.Listeners.MainViewListener;
import com.SRTP.strplocation.Views.customButton;

import java.util.ArrayList;

public class LoggerFragment extends Fragment {
    TextView mLogView;
    ScrollView mScrollView;
    GridView sat_show;
    TextView tv_gps_time;
    private UiLogger mUiLogger;
    private SatelliteShowAdapter adapter;
    private final UIFragmentComponent mUiComponent=new UIFragmentComponent();

    //
    public void setUiLogger(UiLogger mUiLogger) {
        //负责将数据显示在UI上的记录器
        this.mUiLogger = mUiLogger;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View newview=inflater.inflate(R.layout.fragment_log,container,false);
        mLogView=newview.findViewById(R.id.log_view);
        mScrollView=newview.findViewById(R.id.log_scroll);
        sat_show=newview.findViewById(R.id.sat_show);
        tv_gps_time=newview.findViewById(R.id.gps_time);
        customButton cleanAll=newview.findViewById(R.id.clean_all);
        cleanAll.setClickListener(new customButton.OnBtnClickListener() {
            @Override
            public void onClick() {
                mLogView.setText("");
            }
        });
        adapter=new SatelliteShowAdapter(getContext(),new ArrayList<SatSentMsg>());
        sat_show.setAdapter(adapter);
        UiLogger currentUiLogger = mUiLogger;
        if (currentUiLogger != null) {
            currentUiLogger.setUiFragmentComponent(mUiComponent);//将记录器中的信息打印在屏幕上
        }
        return newview;
    }

    public class UIFragmentComponent {

        private static final int MAX_LENGTH = 21000;
        private static final int LOWER_THRESHOLD = (int) (MAX_LENGTH * 0.5);

        public synchronized void logTextFragment(final String tag, final String text, int color) {
            final SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(tag).append(" | ").append(text).append("\n");//构造字符串
            builder.setSpan(
                    new ForegroundColorSpan(color),
                    0 /* start */,
                    builder.length(),
                    SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);//上色

            Activity activity = getActivity();
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(
                    //在主线程中运行 注：修改UI必须在主线程中
                    new Runnable() {
                        @Override
                        public void run() {
                            mLogView.append(builder);
                            Editable editable = mLogView.getEditableText();
                            int length = editable.length();
                            if (length > MAX_LENGTH) {
                                editable.delete(0, length - LOWER_THRESHOLD);//把过长的文本截掉
                            }
                        }
                    });
        }

        public synchronized void logSatInfo(final ArrayList<SatSentMsg> data) {

            Activity activity = getActivity();
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            adapter.updateList(data);
                        }
                    });
        }
        public synchronized void logGPS_Time(final double gps_time) {

            Activity activity = getActivity();
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            tv_gps_time.setText(Long.toString(Math.round(gps_time)));
                        }
                    });
        }

    }
}
