package com.SRTP.strplocation.GnssRawData;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GnssContainer {
    /**
     * 此类非常重要！！
     * 用于获取各种GNSS测量数据
     * 步骤为：先注册注册一个测量器（registerXXX）
     *         再利用监听获取测量数据（XXXListener）
     **/
    //常量
    public static final String TAG = "GnssLogger";
    private static final long LOCATION_RATE_GPS_MS = TimeUnit.SECONDS.toMillis(1L);//1秒钟（转换为1000毫秒），位置更新之间的最小时间间隔
    private static final long LOCATION_RATE_NETWORK_MS = TimeUnit.SECONDS.toMillis(60L);//60秒（转换为60000毫秒）
    //标记
    private boolean mLogLocations = true;//是否允许记录位置坐标
    private boolean mLogMeasurements = true;//是否允许记录gnss测量值
    private boolean firstTime = true;//是否是第一次记录

    //定位时间记录
    private Long registrationTimeNanos = 0L;//注册位置监听的时刻（纳秒）
    private Long firstLocationTimeNanos = 0L;//第一次获取位置信息的时刻（纳秒）
    private Long ttff = 0L;//首次定位的时间

    //
    private List<GnssListener> mLoggers;//申明一组记录器
    private LocationManager mLocationManager;//申明一个位置管理器，可用于定位和注册gnssmeasurementcallback

    //构造函数
    public GnssContainer(Context context, GnssListener... loggers) {
        //此处GnssListener...是可变参数可传参也可不传
        this.mLoggers = Arrays.asList(loggers);//数组转集合
        //这里的mLoggers可能是UILogger，也可能是fileLogger、agnssLogger
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);//获取位置服务
    }

    //位置变化监听器
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //是否是第一次记录,且当前返回的位置是通过GPS得到的
            if (firstTime && location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                if (mLogLocations) {
                    for (GnssListener logger : mLoggers) {
                        firstLocationTimeNanos = SystemClock.elapsedRealtimeNanos();//返回当前系统时间
                        ttff = firstLocationTimeNanos - registrationTimeNanos;//计算出首次定位所用时间
                        logger.onTTFFReceived(ttff);//这里主要注意AGnssUiLogger中的onTTFFReceived()
                    }
                }
                firstTime = false;
            }
            if (mLogLocations) {
                //遍历mLoggers中的每个元素，记录位置信息
                for (GnssListener logger : mLoggers) {
//                    if (logger instanceof AgnssUiLogger && !firstTime) {
//                        continue;
//                    }
                    logger.onLocationChanged(location);//这里主要注意UILogger中的onLocationChanged()
                    Log.d("locationLong", String.valueOf(location.getLongitude()));
                    Log.d("locationLat", String.valueOf(location.getLatitude()));
                    Log.d("locationAlti", String.valueOf(location.getAltitude()));
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (mLogLocations) {
                //遍历mLoggers中的每个元素，记录状态变动信息
                for (GnssListener logger : mLoggers) {
                    logger.onLocationStatusChanged(provider, status, extras);//这里主要注意UILogger中的onLocationStatusChanged()
                }
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            if (mLogLocations) {
                //遍历mLoggers中的每个元素，记录位置提供者的信息
                for (GnssListener logger : mLoggers) {
//                    if (logger instanceof AgnssUiLogger && !firstTime) {
//                        continue;
//                    }
                    logger.onProviderEnabled(provider);//这里主要注意UILogger中的onProviderEnabled()
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            if (mLogLocations) {
                for (GnssListener logger : mLoggers) {
//                    if (logger instanceof AgnssUiLogger && !firstTime) {
//                        continue;
//                    }
                    logger.onProviderDisabled(provider);
                }
            }
        }
    };
    //位置变化监听器函数重写结束

    //Gnss测量值监听器
    //用于从GNSS引擎接收GNSS卫星测量值
    private GnssMeasurementsEvent.Callback gnssMeasurementEventListener =
            new GnssMeasurementsEvent.Callback() {
                @Override
                public void onGnssMeasurementsReceived(GnssMeasurementsEvent eventArgs) {
                    super.onGnssMeasurementsReceived(eventArgs);
                    if (mLogMeasurements) {
                        //记录gnss测量信息
                        for (GnssListener logger : mLoggers) {
                            logger.onGnssMeasurementsReceived(eventArgs);//主要注意UILogger中的onGnssMeasurementsReceived()
                        }
                    }
                }

                @Override
                public void onStatusChanged(int status) {
                    super.onStatusChanged(status);
                }
            };

    //星历数据监听器
    private final GnssNavigationMessage.Callback gnssNavigationMessageListener =
            new GnssNavigationMessage.Callback() {
                @Override
                public void onGnssNavigationMessageReceived(GnssNavigationMessage event) {
                    super.onGnssNavigationMessageReceived(event);
                    for (GnssListener logger : mLoggers) {
                        logger.onGnssNavigationMessageReceived(event);//写入接口
                    }
                }

                @Override
                public void onStatusChanged(int status) {
                    super.onStatusChanged(status);
                    for (GnssListener logger : mLoggers) {
                        logger.onGnssNavigationMessageStatusChanged(status);
                    }
                }
            };

    //开启定位

    @SuppressLint("MissingPermission")
    public void registerLocation() {
        boolean isGpsProviderEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);//网络定位是否已经启用
        if (isGpsProviderEnabled) {
            //获取位置更新
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,//给出位置提供者
                    LOCATION_RATE_NETWORK_MS,//每60秒更新一次位置
                    0.0f,//距离变化0.0m更新一次位置
                    mLocationListener//位置更新时由mLocationListener来监听
            );
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_RATE_GPS_MS,//每1秒更新一次位置
                    0.0f ,
                    mLocationListener
            );
        }
        logRegistration("位置更新",isGpsProviderEnabled);
    }
    public void unRegisterLocation(){
        mLocationManager.removeUpdates(mLocationListener);
    }

    //开启gnss测量
    public void RegisterMeasurements(){
        @SuppressLint("MissingPermission")
        boolean is_register_success=mLocationManager.registerGnssMeasurementsCallback(gnssMeasurementEventListener);//注册GNSSmeasurement回调监听，返回是否注册成功的信息
        //测量信息将在GnssMeasurementsEvent.Callback中接收
        logRegistration("GNSS测量",is_register_success);
    }
    public void unRegisterMeasurements(){
        mLocationManager.unregisterGnssMeasurementsCallback(gnssMeasurementEventListener);
    }
    public void registerNavigation() {
        boolean is_register_success=mLocationManager.registerGnssNavigationMessageCallback(gnssNavigationMessageListener);
        logRegistration("GpsNavigationMessage", is_register_success);
    }

    public void unregisterNavigation() {
        mLocationManager.unregisterGnssNavigationMessageCallback(gnssNavigationMessageListener);
    }

    private void logRegistration(String listener, boolean result) {
        //记录定位是否开启的信息，与registerLocation合用
        for (GnssListener logger : mLoggers) {
//            if (logger instanceof AgnssUiLogger && !firstTime) {
//                continue;
//            }
            logger.onListenerRegistration(listener, result);
        }
    }
}
