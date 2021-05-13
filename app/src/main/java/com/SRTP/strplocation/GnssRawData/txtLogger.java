package com.SRTP.strplocation.GnssRawData;

import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.GnssStatus;
import android.location.Location;
import android.os.Bundle;

import com.SRTP.strplocation.GnssCalculator.SatSentMsg;
import com.SRTP.strplocation.Utils.FileUtil;

import java.io.File;

public class txtLogger implements GnssListener {
    private File Dir;

    public void setDir(File dir) {
        Dir = dir;
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onLocationStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onGnssMeasurementsReceived(GnssMeasurementsEvent event) {
        StringBuilder txt_writer=new StringBuilder("======================新历元======================\n\n");
        FileUtil.table_writer(txt_writer,"%-12s","GPS_TIME","PRN","伪距");
        for (GnssMeasurement measurement : event.getMeasurements()) {
            if (measurement.getConstellationType()==GnssStatus.CONSTELLATION_GPS) {
                SatSentMsg data=new SatSentMsg(measurement,event.getClock());
                FileUtil.table_writer(txt_writer,"%-12s",
                        String.valueOf(Math.round(SatSentMsg.getGPSTime(event.getClock()))),
                        String.valueOf(measurement.getSvid()),
                        String.valueOf(data.getPseudorange()));
            }
        }
        FileUtil.WriteTXT(Dir+"/PseuData"+".txt",txt_writer.toString(),true);
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
}
