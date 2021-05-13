package com.SRTP.strplocation;

import android.annotation.SuppressLint;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.GnssStatus;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ZoomControls;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.SRTP.strplocation.GnssCalculator.SatSentMsg;
import com.SRTP.strplocation.GnssCalculator.NavigationMessageRaw;
import com.SRTP.strplocation.GnssCalculator.SinglePositioning;
import com.SRTP.strplocation.GnssRawData.GnssListener;
import com.SRTP.strplocation.Room.NavigationInfoDBTools;
import com.SRTP.strplocation.Room.NavigationMessageForDB;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
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

public class MapFragment extends Fragment implements GnssListener {

    private MapView mMapview = null;
    private BaiduMap mbaidumap;
    private Button center;
    private TextView data;
    private BitmapDescriptor point_with_diff;
    private BitmapDescriptor point_without_diff;
    //
    private LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    //
    private NavigationInfoDBTools navigationInfoDBTools;
    private ArrayList<NavigationMessageForDB> NaviMessageList;
    private double currentLongtitude = 0;
    private double currentLatitude = 0;
    private double pastLongtitude = 0;
    private double Longtitude_without_diff = 0;
    private double Latitude_without_diff = 0;
    private double pastLongtitude_without_diff = 0;
    private int counter = 0;
    private boolean btnclick = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //定位
        mLocationClient = new LocationClient(getActivity().getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        initLocation();
        mLocationClient.start();
        //使用BaiduMap  SDK
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(getActivity().getApplicationContext());
        navigationInfoDBTools = new ViewModelProvider((ViewModelStoreOwner) Objects.requireNonNull(getContext())).get(NavigationInfoDBTools.class);
        navigationInfoDBTools.getAllNavigationInfoLD().observe((LifecycleOwner) getContext(), new Observer<List<NavigationMessageForDB>>() {
            @Override
            public void onChanged(List<NavigationMessageForDB> navigationMessageForDBS) {
                NaviMessageList = (ArrayList<NavigationMessageForDB>) navigationMessageForDBS;
            }
        });
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_map, container, false /* attachToRoot */);
        center = view.findViewById(R.id.center);
        data = view.findViewById(R.id.show_data);
        //地图显示控件
        mMapview = (MapView) view.findViewById(R.id.bmapview);
        mbaidumap = mMapview.getMap();
        //
        //设置图标样式
        point_with_diff = BitmapDescriptorFactory.fromResource(R.drawable.mypoint);
        point_without_diff = BitmapDescriptorFactory.fromResource(R.drawable.point_normal);
        //隐藏baidulogo
        View child = mMapview.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }


        return view;
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {
//        pastLongtitude_without_diff=Longtitude_without_diff;
//        Longtitude_without_diff=location.getLongitude();
//        Latitude_without_diff=location.getLatitude();
//        if(pastLongtitude_without_diff!=Longtitude_without_diff)
//        {
//            mbaidumap.clear();
//            addMypoint(currentLatitude,currentLongtitude,point_with_diff);
//            addMypoint(Latitude_without_diff,Longtitude_without_diff,point_without_diff);
//        }
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
            if (currentData.getPseudorange() != -1 && currentData.getSVType() == GnssStatus.CONSTELLATION_GPS) {
                Data_in_SingleEpoch.add(currentData);
                PRN.add(currentData.getPRN());
            }
        }
        ArrayList<NavigationMessageRaw> matchedList = findMatched(GPSTime, PRN);
        //double[] BLH = SinglePositioning.Position_Calculator(matchedList, Data_in_SingleEpoch, GPSTime, matchedList.size());
//        Log.d("BLH", "B=" + BLH[0] + "\t L=" + BLH[1]);
//        paintOnMap(BLH);
    }

    private void paintOnMap(double[] BLH) {
        pastLongtitude = currentLongtitude;
        currentLongtitude = BLH[0];
        currentLatitude = BLH[1];
        //Log.d("main2",""+currentLatitude+";"+currentLongtitude);
        center.bringToFront();
        if (pastLongtitude != currentLongtitude) {
            mbaidumap.clear();
            addMypoint(currentLatitude, currentLongtitude, point_with_diff);
        }
        center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnclick = true;
            }
        });
        counter += 1;
        if (counter == 1 || btnclick) {
            //设置自己的位置坐标
            final LatLng myPoint = new LatLng(currentLatitude, currentLongtitude);
            //将自己的坐标显示在地图中心
            MapStatus mapStatus = new MapStatus.Builder().target(myPoint).zoom(20).build();
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
            mbaidumap.setMapStatus(mapStatusUpdate);
            btnclick = false;
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

    class MyLocationListener extends BDAbstractLocationListener {

        @SuppressLint("SetTextI18n")
        @Override
        public void onReceiveLocation(BDLocation location) {
//            double delta_x=0.000008983152841195214;
//            double delta_y=0.000008983152841195214;
//            if (location.getLocType() == BDLocation.TypeServerError) {
//                Toast.makeText(getContext(),"服务器异常",Toast.LENGTH_SHORT).show();
//            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
//                Toast.makeText(getContext(),"请检查网络连接",Toast.LENGTH_SHORT).show();
//            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
//                Toast.makeText(getContext(),"请打开位置权限和启用手机位置信息",Toast.LENGTH_SHORT).show();
//            } else {
//                pastLongtitude=currentLongtitude;
//                currentLongtitude = location.getLongitude();
//                currentLatitude = location.getLatitude();
//                Log.d("main2",""+currentLatitude+";"+currentLongtitude);
//                center.bringToFront();
//                if(pastLongtitude!=currentLongtitude)
//                {
//                    data.setText(currentLatitude+" , "+currentLongtitude);
//                    mbaidumap.clear();
//                    addMypoint(currentLatitude,currentLongtitude,point_with_diff);
//                    addMypoint(currentLatitude+delta_x*10,currentLongtitude+delta_y*7,point_without_diff);
//                }
//                center.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        btnclick=true;
//                    }
//                });
//                counter+=1;
//                if(counter==1||btnclick) {
//                    //设置自己的位置坐标
//                    final LatLng myPoint = new LatLng(currentLatitude, currentLongtitude);
//                    //将自己的坐标显示在地图中心
//                    MapStatus mapStatus = new MapStatus.Builder().target(myPoint).zoom(20).build();
//                    MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
//                    mbaidumap.setMapStatus(mapStatusUpdate);
//                    btnclick=false;
//                }

//        }
        }

    }

    private void addMypoint(double x,double y,BitmapDescriptor bitmap)
    {
        List<OverlayOptions> options = new ArrayList<OverlayOptions>();
        LatLng point=new LatLng(x,y);
        //加载图标
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        options.add(option);
        //显示在地图上
        mbaidumap.addOverlays(options);
    }

    private ArrayList<NavigationMessageRaw> findMatched(double GpsTime, ArrayList<Integer>PRN){
        ArrayList<NavigationMessageForDB>Time_matchedList=new ArrayList<>();
        for (NavigationMessageForDB navi : NaviMessageList) {
            if (navi.getGPS_time()==GpsTime)
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
