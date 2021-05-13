package com.SRTP.strplocation.Adapters;

import android.content.Context;
import android.location.GnssStatus;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.SRTP.strplocation.GnssCalculator.SatSentMsg;
import com.SRTP.strplocation.R;

import java.util.ArrayList;

public class SatelliteShowAdapter extends BaseAdapter {
    private ArrayList<SatSentMsg>sat_list;
    private Context context;

    public SatelliteShowAdapter(Context context,ArrayList<SatSentMsg> sat_list) {
        this.sat_list = sat_list;
        this.context=context;
    }
    public void updateList(ArrayList<SatSentMsg> sat_list){
        this.sat_list=sat_list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return sat_list.size();
    }

    @Override
    public Object getItem(int position) {
        return sat_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout Layout=null;
//        if(convertView!=null) {
//            Layout= (RelativeLayout) convertView;
//        }
//        else {
            Layout= (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.satellite_item,null);
            TextView prn=Layout.findViewById(R.id.prn);
            ImageView icon=Layout.findViewById(R.id.sat_icon);
            SatSentMsg data=sat_list.get(position);
            String Head="";
            switch(data.getSVType()){
                case GnssStatus.CONSTELLATION_GPS:
                    Head="G";
                    icon.setImageResource(R.mipmap.gps);
                    break;
                case GnssStatus.CONSTELLATION_BEIDOU:
                    Head="C";
                    icon.setImageResource(R.mipmap.bds);
                    break;
                default:
            }
            prn.setText(Head+data.getPRN());
       // }
        return Layout;
    }
}
