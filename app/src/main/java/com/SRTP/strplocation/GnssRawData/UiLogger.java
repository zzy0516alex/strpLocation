package com.SRTP.strplocation.GnssRawData;

import android.graphics.Color;
import android.location.GnssClock;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;

import com.SRTP.strplocation.GnssCalculator.SatSentMsg;
import com.SRTP.strplocation.LoggerFragment.UIFragmentComponent;

import java.text.DecimalFormat;
import java.util.ArrayList;

//GnssListener接口的UI实例
public class UiLogger implements GnssListener {
    /**
     * 此类用于记录GNSSContainer获取到的测量数据
     * 并把它们显示在LoggerFragment上
     */
    private static final int COLOR_GRAY = Color.rgb(0x4a, 0x5f, 0x70);
    private static final int COLOR_RAD = Color.rgb(0xC7, 0x15, 0x85);
    private static final int COLOR_BLUE = Color.rgb(0x00, 0x00, 0xCD);
    private static final int COLOR_BLACK = Color.rgb(0x00, 0x00, 0x00);

    private UIFragmentComponent mUiFragmentComponent;//UI显示内容类

    private ArrayList<SatSentMsg>sat_list=new ArrayList<>();

    public UiLogger(){}
    //get & set

    public UIFragmentComponent getUiFragmentComponent() {
        return mUiFragmentComponent;
    }

    public void setUiFragmentComponent(UIFragmentComponent mUiFragmentComponent) {
        this.mUiFragmentComponent = mUiFragmentComponent;
    }

    //用于记录的函数
    private void LogText(String tag,String text,int color){
        //记录具体内容，被LogEvent调用
        UIFragmentComponent component=getUiFragmentComponent();
        if(component!=null){
            component.logTextFragment(tag,text,color);//详见LoggerFragment.java-class UIFragmentComponent类，在UI上显示text
        }
    }
    private void LogSatInfo(ArrayList<SatSentMsg> sat_list){
        UIFragmentComponent component=getUiFragmentComponent();
        if(component!=null){
            component.logSatInfo(sat_list);
        }
    }private void LogGPS_Time(double gps_time){
        UIFragmentComponent component=getUiFragmentComponent();
        if(component!=null){
            component.logGPS_Time(gps_time);
        }
    }
    private void LogEvent(String tag,String message,int color){
        //记录各种回调事件，被logXXXEvent调用
        String composedTag= GnssContainer.TAG+tag;
        LogText(tag, message, color);
    }
    private void LogLocationEvent(String Event) {
        //记录位置信息
        LogEvent("定位信息", Event, COLOR_BLUE);
    }
    private void LogMeasurementEvent(String Event){
        //记录测量值信息
        LogEvent("GNSS测量信息",Event, COLOR_GRAY);
    }

    private void LogPseudorange(SatSentMsg data){
        String Head="?";
        switch(data.getSVType()){
            case GnssStatus.CONSTELLATION_GPS:Head="G";
                break;
            case GnssStatus.CONSTELLATION_BEIDOU:Head="C";
                break;
            default:
        }
        LogEvent("GNSS伪距计算数据",Head+data.getPRN()+": "+data.getPseudorange(),COLOR_RAD);
    }
    //

    //字符串转换函数
    private String locationStatusToString(int status) {
        //将定位状态转换为字符串
        switch (status) {
            case LocationProvider.AVAILABLE:
                return "AVAILABLE";//定位可获取
            case LocationProvider.OUT_OF_SERVICE:
                return "OUT_OF_SERVICE";//不再服务区内
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                return "TEMPORARILY_UNAVAILABLE";//定位暂时不可获取
            default:
                return "<Unknown>";
        }
    }
    //@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    private String toStringClock(GnssClock gnssClock){
        //将GPS接收器时钟的值转换为字符串
        final String format = "   %-4s = %s\n";//定义数据显示格式，“%-4”表示左对齐、不足四位补足四位
        StringBuilder builder=new StringBuilder("GNSS时钟:\n");
        DecimalFormat numberFormat = new DecimalFormat("#0.000");//定义格式化数字
        if (gnssClock.hasLeapSecond()) {
            //如果闰秒存在则显示闰秒
            builder.append(String.format(format, "闰秒", gnssClock.getLeapSecond()));
        }
        builder.append(String.format(format, "硬件时钟", gnssClock.getTimeNanos()));//获取以毫秒为单位的GNSS接收器内部硬件时钟值
        if (gnssClock.hasTimeUncertaintyNanos()) {
            //获取硬件时钟的误差估计（不确定度）
            builder.append(String.format(format, "时钟误差估计", gnssClock.getTimeUncertaintyNanos()));
        }

        if (gnssClock.hasFullBiasNanos()) {
            //如果存在接收机本地时钟总偏差，则显示
            builder.append(String.format(format, "总时钟偏差", gnssClock.getFullBiasNanos()));
        }
        if (gnssClock.hasBiasNanos()) {
            //亚纳秒偏差
            builder.append(String.format(format, "亚偏差", gnssClock.getBiasNanos()));
        }
        if (gnssClock.hasBiasUncertaintyNanos()) {
            //FullBiasNanos和BiasNanos的误差估计
            builder.append(String.format(format, "时钟偏差估计", numberFormat.format(gnssClock.getBiasUncertaintyNanos())));
        }
        /**
         * 注意：以上五个数据用于计算GPS时钟
         * notice 具体计算方法为：local estimate of GPS time = TimeNanos - (FullBiasNanos + BiasNanos)
         *     世界标准时：UtcTimeNanos = TimeNanos - (FullBiasNanos + BiasNanos) - LeapSecond * 1,000,000,000
         */
        if (gnssClock.hasDriftNanosPerSecond()) {
            //以每秒纳秒为单位获取时钟的漂移
            builder.append(String.format(format, "时钟漂移", numberFormat.format(gnssClock.getDriftNanosPerSecond())));
        }
        if (gnssClock.hasDriftUncertaintyNanosPerSecond()) {
            //时钟偏差的估计
            builder.append(String.format(format, "时钟漂移估计", numberFormat.format(gnssClock.getDriftUncertaintyNanosPerSecond())));
        }
        //获取硬件时钟不连续的计数,即：每当gnssclock中断时，该值+1
        builder.append(String.format(format, "中断计数", gnssClock.getHardwareClockDiscontinuityCount()));
        return builder.toString();
    }
    //@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    private String toStringMeasurement(GnssMeasurement measurement){
        //将GNSS测量结果转换为字符串
        //定义显示格式
        final String format = "   %-4s = %s\n";
        StringBuilder builder = new StringBuilder("GNSS测量结果:\n");
        DecimalFormat numberFormat = new DecimalFormat("#0.000");
        DecimalFormat numberFormat1 = new DecimalFormat("#0.000E00");

        //获取卫星ID
            /**
             * 取决于卫星类型
             * GPS：1-32
             * SBAS：120-151、183-192
             * GLONASS：OSN或FCN + 100之一
             * 1-24作为轨道槽号（OSN）（首选，如果知道）
             * 93-106作为频道号（FCN）（-7至+6）加100。即将-7的FCN编码为93，0编码为100，+ 6编码为106
             * QZSS：193-200
             * 伽利略：1-36
             * 北斗：1-37
             */
        builder.append(String.format(format, "卫星ID", measurement.getSvid()));

        //获取卫星类型
            /**
             *  1:CONSTELLATION_GPS 使用GPS定位
             *  2:CONSTELLATION_SBAS 使用SBAS定位
             *  3：CONSTELLATION_GLONASS 使用格洛纳斯定位
             *  4：CONSTELLATION_QZSS 使用QZSS定位
             *  5：CONSTELLATION_BEIDOU 使用北斗定位 （^-^）!
             *  6：CONSTELLATION_GALILEO 使用伽利略定位
             *  7：CONSTELLATION_IRNSS 使用印度区域卫星定位
             */
        builder.append(String.format(format, "卫星类型", measurement.getConstellationType()));

        //获取进行测量的时间偏移量（以纳秒为单位）
        builder.append(String.format(format, "测量时间偏移量", measurement.getTimeOffsetNanos()));

        //获取每个卫星的同步状态
        //具体数值含义请查表
        builder.append(String.format(format, "同步状态", measurement.getState()));

        //获取时间戳的伪距速率，以m / s为单位
        builder.append(
                String.format(
                        format,
                        "伪距速率",
                        numberFormat.format(measurement.getPseudorangeRateMetersPerSecond())));
        //获取伪距的速率不确定性（1-Sigma），以m / s为单位
        builder.append(
                String.format(
                        format,
                        "伪距速率不确定度",
                        numberFormat.format(measurement.getPseudorangeRateUncertaintyMetersPerSecond())));
        //
        /*
        if (measurement.getAccumulatedDeltaRangeState() != 0) {
            // 获取“累积增量范围”状态
            // 返回：MULTIPATH_INDICATOR_UNKNOWN（指示器不可用）=0
            // notice 即：指示器可用时，收集数据
            builder.append(
                    String.format(
                            format, "累积增量范围状态", measurement.getAccumulatedDeltaRangeState()));

            //获取自上次重置通道以来的累积增量范围，以米为单位.
            //该值仅在上面的state值为“可用”时有效
            //notice 累积增量范围= -k * 载波相位（其中k为常数）
            builder.append(
                    String.format(
                            format,
                            "累积增量范围",
                            numberFormat.format(measurement.getAccumulatedDeltaRangeMeters())));

            //获取以米为单位的累积增量范围的不确定性（1-Sigma）
            builder.append(
                    String.format(
                            format,
                            "累积增量范围不确定度",
                            numberFormat1.format(measurement.getAccumulatedDeltaRangeUncertaintyMeters())));
        }

        if (measurement.hasCarrierFrequencyHz()) {
            //获取被跟踪信号的载波频率
            builder.append(
                    String.format(format, "信号载波频率", measurement.getCarrierFrequencyHz()));
        }

        if (measurement.hasCarrierCycles()) {
            //卫星和接收器之间的完整载波周期数
            builder.append(String.format(format, "载波周期数", measurement.getCarrierCycles()));
        }

        if (measurement.hasCarrierPhase()) {
            //获取接收器检测到的RF相位
            builder.append(String.format(format, "RF相位", measurement.getCarrierPhase()));
        }

        if (measurement.hasCarrierPhaseUncertainty()) {
            //误差估计
            builder.append(
                    String.format(
                            format, "RF相位不确定度", measurement.getCarrierPhaseUncertainty()));
        }

        //获取一个值，该值指示事件的“多路径”状态,返回0或1或2
        //MULTIPATH_INDICATOR_DETECTED = 1 测量显示有“多路径效应”迹象
        // MULTIPATH_INDICATOR_NOT_DETECTED = 2 测量结果显示没有“多路径效应”迹象
        builder.append(String.format(format, "多路经效应指示器", measurement.getMultipathIndicator()));

        //
        if (measurement.hasSnrInDb()) {
            //获取信噪比（SNR），以dB为单位
            builder.append(String.format(format, "信噪比", measurement.getSnrInDb()));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (measurement.hasAutomaticGainControlLevelDb()) {
                //获取以dB为单位的自动增益控制级别
                builder.append(String.format(format, "自动增益控制级别", measurement.getAutomaticGainControlLevelDb()));
            }
            if (measurement.hasCarrierFrequencyHz()) {
                builder.append(String.format(format, "载波频率", measurement.getCarrierFrequencyHz()));
            }
        }
        */

        return builder.toString();

    }


    //GnssListener接口函数重写
    @Override
    public void onProviderEnabled(String provider) {
        LogLocationEvent("onProviderEnabled:"+provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        LogLocationEvent("onProviderDisabled:"+provider);
    }

    @Override
    public void onLocationChanged(Location location) {
        //LogLocationEvent("onLocationChanged: " + location.getLatitude() + "," + location.getLongitude() + "\n");
    }

    @Override
    public void onLocationStatusChanged(String provider, int status, Bundle extras) {
        String message = String.format("onStatusChanged: provider=%s, status=%s, extras=%s",
                                            provider,
                                            locationStatusToString(status),
                                            extras
                                        );
        LogLocationEvent(message);
    }

    //@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    private int counter=0;
    @Override
    public void onGnssMeasurementsReceived(GnssMeasurementsEvent event) {
        counter=counter%3;
        StringBuilder builder=new StringBuilder("GNSS测量数据：\n\n");
        LogGPS_Time(SatSentMsg.getGPSTime(event.getClock()));
        builder.append(toStringClock(event.getClock()));//写入gnss时钟的数据
        builder.append("\n");
        if (sat_list.size()!=0)sat_list.clear();
        for (GnssMeasurement measurement : event.getMeasurements()) {
            builder.append(toStringMeasurement(measurement));//写入gnss测量数据
            builder.append("\n");
            SatSentMsg data=new SatSentMsg(measurement,event.getClock());
            if (data.getSVType()==GnssStatus.CONSTELLATION_GPS||data.getSVType()==GnssStatus.CONSTELLATION_BEIDOU)
                if (!data.inList(sat_list))
                    sat_list.add(data);
        }
        if (counter==0)LogSatInfo(sat_list);
        counter++;
        builder.append("========================================\n");
        LogMeasurementEvent(builder.toString());
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
        //用于记录注册了哪个监听器
        LogEvent("注册",String.format("添加 %s 监听器: %b",listener,result), COLOR_GRAY);
    }

    @Override
    public void onNmeaReceived(long l, String s) {

    }

    @Override
    public void onTTFFReceived(long l) {

    }
}
