package com.SRTP.strplocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.GnssStatus;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.SRTP.strplocation.GnssCalculator.NavigationMessageRaw;
import com.SRTP.strplocation.GnssCalculator.SatSentMsg;
import com.SRTP.strplocation.GnssCalculator.SinglePositioning;
import com.SRTP.strplocation.GnssRawData.GnssContainer;
import com.SRTP.strplocation.GnssRawData.GnssListener;
import com.SRTP.strplocation.GnssRawData.UiLogger;
import com.SRTP.strplocation.Listeners.MainViewListener;
import com.SRTP.strplocation.Listeners.SettingChangeListener;
import com.SRTP.strplocation.Room.NavigationInfoDBTools;
import com.SRTP.strplocation.Room.NavigationMessageForDB;
import com.SRTP.strplocation.Utils.AxesTransferUtil;
import com.SRTP.strplocation.Utils.FileUtil;
import com.SRTP.strplocation.Utils.StatusBarUtil;
import com.SRTP.strplocation.Views.customButton;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.PI;
import static java.security.AccessController.getContext;

public class NavigationActivity extends AppCompatActivity implements GnssListener, SettingChangeListener {

    private static final int LOCATION_REQUEST_ID = 1;//权限获取码
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };//所需权限，用于权限检查

    private MapView mMapview = null;
    private BaiduMap mbaidumap;
    private InfoFragment fragment;
    private NavigationInfoDBTools navigationInfoDBTools;
    private ArrayList<NavigationMessageForDB> NaviMessageList;
    public customButton setting;
    public customButton logger;
    private ImageButton focus;
    private MainViewListener listener;
    private BitmapDescriptor point1;
    private BitmapDescriptor point2;
    private boolean can_focus;
    private boolean can_do_diffLocation=false;
    private boolean need_clear_map=true;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        //SDKInitializer.setCoordType(CoordType.GCJ02);
        setContentView(R.layout.activity_navigation);
        StatusBarUtil.setStatusBarDarkTheme(this,true);
        context=this;
        //地图显示控件
        mMapview = findViewById(R.id.bmapview);
        mbaidumap = mMapview.getMap();
        mMapview.showScaleControl(false);
        mMapview.showZoomControls(false);
        //加载fragment
        if (hasPermissions(this)) {
            //判断是否已经有权限了，如果没有则申请一个
            setupFragment();
        } else {
            //动态申请权限
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, LOCATION_REQUEST_ID);
        }

        navigationInfoDBTools = new ViewModelProvider((ViewModelStoreOwner) this).get(NavigationInfoDBTools.class);
        navigationInfoDBTools.getAllNavigationInfoLD().observe((LifecycleOwner) this, new Observer<List<NavigationMessageForDB>>() {
            @Override
            public void onChanged(List<NavigationMessageForDB> navigationMessageForDBS) {
                NaviMessageList = (ArrayList<NavigationMessageForDB>) navigationMessageForDBS;
            }
        });

        BaiduMap.OnMapTouchListener ls = new BaiduMap.OnMapTouchListener() {

            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (motionEvent.getAction()==MotionEvent.ACTION_MOVE){
                    focus.setImageResource(R.mipmap.focus_blue);
                    can_focus=false;
                }
                if (motionEvent.getAction()==MotionEvent.ACTION_DOWN)fragment.mScrollLayout.setToExit();
            }
        };
        //设置触摸地图事件监听者
        mbaidumap.setOnMapTouchListener(ls);

        point1 = BitmapDescriptorFactory.fromResource(R.drawable.mypoint);
        point2 = BitmapDescriptorFactory.fromResource(R.drawable.point_normal);

        logger=findViewById(R.id.logger);
        setting=findViewById(R.id.setting);
        logger.setOnBtnSelectListener(new customButton.OnBtnSelectListener() {
            @Override
            public void onBtnSelect() {
                if (setting.IsSelected()) setting.setIs_selected(false);
                listener.onBtnLoggerClick();
                fragment.mScrollLayout.setToOpen();
            }

            @Override
            public void onBtnCancel() {
                fragment.mScrollLayout.setToExit();
            }
        });
        setting.setOnBtnSelectListener(new customButton.OnBtnSelectListener() {
            @Override
            public void onBtnSelect() {
                if (logger.IsSelected()) logger.setIs_selected(false);
                listener.onBtnSettingClick();
                fragment.mScrollLayout.setToOpen();
            }

            @Override
            public void onBtnCancel() {
                fragment.mScrollLayout.setToExit();
            }
        });

        focus=findViewById(R.id.focus_btn);
        focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!can_focus)focus.setImageResource(R.mipmap.focus_small);
                else focus.setImageResource(R.mipmap.focus_blue);
                can_focus=!can_focus;
                if (pastLatitude>0 && pastLongitude>0)FocusTo(pastLatitude,pastLongitude);
            }
        });

    }

    private void setupFragment() {
        fragment= new InfoFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.info_viewer,fragment).commitAllowingStateLoss();
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        if (fragment instanceof MainViewListener)listener= (MainViewListener) fragment;
        super.onAttachFragment(fragment);
    }
    @Override
    public void onPause() {
        super.onPause();
        mMapview.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapview.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapview.onResume();
    }

    //动态申请权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_ID) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupFragment();
            }
        }
    }

    private boolean hasPermissions(Activity activity) {
        //判断是否获取了权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Permissions granted at install time.
            return true;
        }
        for (String p : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void addMypoint(double x, double y, BitmapDescriptor bitmap)
    {
        List<OverlayOptions> options = new ArrayList<OverlayOptions>();
        LatLng point=new LatLng(x,y);
        //加载图标
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        options.add(option);
        //显示在地图上
        mbaidumap.addOverlays(options);
    }

    private void FocusTo(double TargetLatitude,double TargetLongtitude) {
        LatLng myPoint = new LatLng(TargetLatitude, TargetLongtitude);
        MapStatus mapStatus = new MapStatus.Builder().target(myPoint).zoom(18).build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        mbaidumap.setMapStatus(mapStatusUpdate);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private double pastLongitude;
    private double pastLatitude;
    private boolean first_add=true;
    @Override
    public void onLocationChanged(Location location) {
        //if (pastLongitude != location.getLongitude()) {
            if (need_clear_map)mbaidumap.clear();
            double[] bl=AxesTransferUtil.gps84_To_bd09(location.getLatitude(),location.getLongitude());
            pastLongitude=bl[1];
            pastLatitude=bl[0];
            addMypoint(bl[0],bl[1],point2);
            String content="B"+bl[0]+"\n"+"L"+bl[1]+"\n";
            FileUtil.WriteTXT(context.getExternalFilesDir(null)+"/google.txt",content,true);
        //}

        if (first_add||can_focus)FocusTo(bl[0],bl[1]);
        first_add=false;
    }

    @Override
    public void onLocationStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onGnssMeasurementsReceived(GnssMeasurementsEvent event) {
        ArrayList<SatSentMsg> Data_in_SingleEpoch = new ArrayList<>();
        ArrayList<Integer> PRN = new ArrayList<>();
        double GPSTime = SatSentMsg.getGPSTime(event.getClock());
        for (GnssMeasurement measurement : event.getMeasurements()) {
            //
            SatSentMsg currentData = new SatSentMsg(measurement, event.getClock());
            currentData.getPseudorange();
            currentData.getSVType();
            currentData.getPRN();
            if (currentData.getPseudorange() != -1 &&
                    (currentData.getSVType() == GnssStatus.CONSTELLATION_GPS || currentData.getSVType() == GnssStatus.CONSTELLATION_BEIDOU) &&
                    !currentData.inList(Data_in_SingleEpoch)) {
                Data_in_SingleEpoch.add(currentData);
                if(currentData.getSVType()==GnssStatus.CONSTELLATION_GPS)PRN.add(currentData.getPRN());
                if (currentData.getSVType()==GnssStatus.CONSTELLATION_BEIDOU)PRN.add(currentData.getPRN()+100);
            }
        }
        if (can_do_diffLocation) {
            SinglePositioning.setContext(context);
            ArrayList<NavigationMessageRaw> matchedList = findMatched(GPSTime, PRN);
            double[] BLH={-1,-1,-1};
            if(matchedList.size()!=0)BLH = SinglePositioning.Position_Calculator(matchedList, Data_in_SingleEpoch, GPSTime, Data_in_SingleEpoch.size(),true);
            Log.d("BLH", "B=" + BLH[0] + "\t L=" + BLH[1]);
            if (BLH[0] > 1 && BLH[1] > 1) {
                double[] blh = AxesTransferUtil.gps84_To_bd09(BLH[0],BLH[1]);
                if (need_clear_map)mbaidumap.clear();
                pastLatitude=blh[0];
                pastLongitude=blh[1];
                addMypoint(blh[0], blh[1], point1);
                if (first_add||can_focus) FocusTo(blh[0], blh[1]);
                first_add=false;
            }
        }
    }

    @Override
    public void onGnssMeasurementsStatusChanged(int status) {

    }

    @Override
    public void onGnssNavigationMessageReceived(GnssNavigationMessage event) {

    }

    @Override
    public void onGnssNavigationMessageStatusChanged(int status) {

    }

    @Override
    public void onGnssStatusChanged(GnssStatus gnssStatus) {

    }

    @Override
    public void onListenerRegistration(String listener, boolean result) {

    }

    @Override
    public void onNmeaReceived(long l, String s) {

    }

    @Override
    public void onTTFFReceived(long l) {

    }

    @Override
    public void onDiffLocateSwitch(boolean on) {
        can_do_diffLocation=on;
    }

    @Override
    public void onMapDrawModChange(boolean on) {
        need_clear_map=!on;
    }

    @Override
    public void onLaunchSharePage() {
        System.out.println("launch");
        listener.onLaunchShareFragment();
    }

    private ArrayList<NavigationMessageRaw> findMatched(double GpsTime, ArrayList<Integer>PRN){
        ArrayList<NavigationMessageForDB>Time_matchedList=new ArrayList<>();
        for (NavigationMessageForDB navi : NaviMessageList) {
            if (Math.abs(navi.getGPS_time()-GpsTime)<1.5)
                Time_matchedList.add(navi);
        }
        ArrayList<NavigationMessageRaw>PRN_matchedList=new ArrayList<>();
        for (NavigationMessageForDB navi:Time_matchedList) {
            if (PRN.contains(navi.getPrn())){
                PRN_matchedList.add(new NavigationMessageRaw(navi));
            }
        }
        return PRN_matchedList;
    }
}
