package com.SRTP.strplocation.GnssRawData;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.SRTP.strplocation.Room.NavigationInfoDBTools;
import com.SRTP.strplocation.Room.NavigationMessageForDB;
import com.SRTP.strplocation.SettingsFragment;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NavigationDataReceiver extends Thread {
    private final String ip = "10.193.99.148";
    private final int port = 6011;
    private InputStreamReader reader;
    private InputStream in;
    private Context context;
    private char []buff=new char[4068];
    private boolean findA=false;
    private boolean findB=false;
    private int count=0;
    private List<NavigationMessageForDB> navigationMessageForDB =new ArrayList<>();
    private NavigationInfoDBTools navigationInfoDBTools;
    private final double  P2_29 = 1.862645149230957E-09;
    private final double  RANGE_MS = (299792458.0)*0.001;
    private final double divisor10=Math.pow(2,10);
    private final double divisor16=Math.pow(2,16);
    private final double divisor24=Math.pow(2,24);
    private final double divisor30=Math.pow(2,30);
    private final double divisor38=Math.pow(2,38);
    private final double divisor40=Math.pow(2,40);
    private DataReceiveHandler handler;
    public static final int CONNECT_ERROR=0;
    public static final int CONNECT_ONLINE=1;

    public NavigationDataReceiver(Context context, DataReceiveHandler handler) {
        this.context=context;
        this.handler=handler;
        //数据库初始化
        this.navigationInfoDBTools= new ViewModelProvider((ViewModelStoreOwner) context).get(NavigationInfoDBTools.class);
    }

    @Override
    public void run() {
        super.run();
        try {
            //连接服务器ip
            Socket socket = new Socket(ip, port);
            socket.setSoTimeout(15000);
            navigationInfoDBTools.deleteAll();
            in = socket.getInputStream();//获取输入流
            Message msg = Message.obtain();
            msg.what=CONNECT_ONLINE;
            handler.sendMessage(msg);
            //处理数据
            //InputStreamTOByte(in);
            InputStreamProcess(in);
        } catch (IOException e) {
            e.printStackTrace();
            Message msg = handler.obtainMessage();
            msg.what=CONNECT_ERROR;
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void InputStreamProcess(InputStream in) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String line;
        while ((line=br.readLine())!=null && !Thread.currentThread().isInterrupted()){
            if ("start ".equals(line)){
                NavigationMessageForDB currentNavigationMessage = new NavigationMessageForDB();
                //todo start read
                String[] s = getData(br);
                if (s.length!=7)throw new Exception("数据格式错误");
                currentNavigationMessage.setPrn(Integer.parseInt(s[0]));
                currentNavigationMessage.setYear(Integer.parseInt(s[1]));
                currentNavigationMessage.setMonth(Integer.parseInt(s[2]));
                currentNavigationMessage.setDay(Integer.parseInt(s[3]));
                currentNavigationMessage.setHour(Integer.parseInt(s[4]));
                currentNavigationMessage.setMinute(Integer.parseInt(s[5]));
                currentNavigationMessage.setSecond(Integer.parseInt(s[6]));
                s=getData(br);
                currentNavigationMessage.setA0(new BigDecimal(s[0]).doubleValue());
                currentNavigationMessage.setA1(new BigDecimal(s[1]).doubleValue());
                currentNavigationMessage.setA2(new BigDecimal(s[2]).doubleValue());
                s=getData(br);
                currentNavigationMessage.setIDOE(new BigDecimal(s[0]).doubleValue());
                currentNavigationMessage.setCrs(new BigDecimal(s[1]).doubleValue());
                currentNavigationMessage.setDelta_n(new BigDecimal(s[2]).doubleValue());
                currentNavigationMessage.setM0(new BigDecimal(s[3]).doubleValue());
                s=getData(br);
                currentNavigationMessage.setCuc(new BigDecimal(s[0]).doubleValue());
                currentNavigationMessage.setE(new BigDecimal(s[1]).doubleValue());
                currentNavigationMessage.setCus(new BigDecimal(s[2]).doubleValue());
                currentNavigationMessage.setSqrtA(new BigDecimal(s[3]).doubleValue());
                s=getData(br);
                currentNavigationMessage.setTOE(new BigDecimal(s[0]).doubleValue());
                currentNavigationMessage.setCic(new BigDecimal(s[1]).doubleValue());
                currentNavigationMessage.setOMEGA(new BigDecimal(s[2]).doubleValue());
                currentNavigationMessage.setCis(new BigDecimal(s[3]).doubleValue());
                s=getData(br);
                currentNavigationMessage.setI0(new BigDecimal(s[0]).doubleValue());
                currentNavigationMessage.setCrc(new BigDecimal(s[1]).doubleValue());
                currentNavigationMessage.setW(new BigDecimal(s[2]).doubleValue());
                currentNavigationMessage.setOMEGA_DOT(new BigDecimal(s[3]).doubleValue());
                s=getData(br);
                currentNavigationMessage.setIDOT(new BigDecimal(s[0]).doubleValue());
                currentNavigationMessage.setPRC(new BigDecimal(s[1]).doubleValue());
                currentNavigationMessage.setGPS_time(new BigDecimal(s[2]).doubleValue());
                navigationInfoDBTools.insertNavigationInfo(currentNavigationMessage);
                System.out.println("prn="+currentNavigationMessage.getPrn());
            }
        }
    }

    private String[] getData(BufferedReader br) throws IOException {
        String temp = br.readLine();
        return temp.split(" ");
    }

//    private void InputStreamTOByte(InputStream in) throws IOException{
//
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        byte[] buffer=new byte[1];
//        int count = -1;
//        while((count = in.read(buffer,0,buffer.length)) != -1 && !Thread.currentThread().isInterrupted()) {
//            Log.d("connected","data writing:count= "+count);
//            char data=(char) ((buffer[0] & 0xFF));
//            processData(data);
//        }
//
//    }

    //数据处理函数
//    private void processData(char data){
//        buff[count] = data;
//        count++;
//        if (count >= 2)
//        {
//            if (buff[count - 1] == 170 && buff[count - 2] == 170)
//            {
//                buff[0] = 0xaa;
//                buff[1] = 0xaa;
//                count = 2;
//                findA = true;
//                return;
//            }
//            if (findA && buff[count - 1] == 187 && buff[count - 2] == 187)
//            {
//                findB = true;
//            }
//        }
//        if (findA && findB)
//        {
//            int GPS_time=getbitu(buff,16,24);
//            int sat_num = getbitu(buff, 40, 8);//AAAA +GPS time
//            if (sat_num == 0)
//            {
//                Looper.prepare();
//                Toast.makeText(context, "出错", Toast.LENGTH_SHORT).show();
//                Looper.loop();
//            }else {
//                int unit=(NavigationMessageForDB.param_num-1)*24+8;
//                int step=24;
//                int start=48;//从prn开始
//                int normal=start+8;//start+prn
//                for (int i = 0; i < sat_num; i++) {
//                    NavigationMessageForDB currentNavigationMessage = new NavigationMessageForDB();
//                    currentNavigationMessage.setPrn(getbitu(buff, start + unit * i, 8));
//                    currentNavigationMessage.setYear(getbitu(buff, normal + unit * i, 24));
//                    currentNavigationMessage.setMonth(getbitu(buff, normal+ step + unit * i, 24));
//                    currentNavigationMessage.setDay(getbitu(buff, normal+ step*2 + unit * i, 24));
//                    currentNavigationMessage.setHour(getbitu(buff, normal+ step*3 + unit * i, 24));
//                    currentNavigationMessage.setMinute(getbitu(buff, normal+ step*4 + unit * i, 24));
//                    currentNavigationMessage.setSecond(getbitu(buff, normal+ step*5 + unit * i, 24));
//                    currentNavigationMessage.setA0(getbits(buff, normal+ step*6 + unit * i, 24) / divisor24);
//                    currentNavigationMessage.setA1(getbits(buff, normal+ step*7 + unit * i, 24) / divisor40);
//                    currentNavigationMessage.setA2(getbits(buff, normal+ step*8 + unit * i, 24) / divisor24);
//                    currentNavigationMessage.setIDOE(getbits(buff, normal+ step*9 + unit * i, 24));
//                    currentNavigationMessage.setCrs(getbits(buff, normal+ step*10 + unit * i, 24) / divisor16);
//                    currentNavigationMessage.setDelta_n(getbits(buff, normal+ step*11 + unit * i, 24) / divisor30);
//                    currentNavigationMessage.setM0(getbits(buff, normal+ step*12 + unit * i, 24) / divisor16);
//                    currentNavigationMessage.setCuc(getbits(buff, normal+ step*13 + unit * i, 24) / divisor24);
//                    currentNavigationMessage.setE(getbits(buff, normal+ step*14 + unit * i, 24) / divisor24);
//                    currentNavigationMessage.setCus(getbits(buff, normal+ step*15 + unit * i, 24) / divisor24);
//                    currentNavigationMessage.setSqrtA(getbits(buff, normal+ step*16 + unit * i, 24) / divisor10);
//                    currentNavigationMessage.setTOE(getbits(buff, normal+ step*17 + unit * i, 24));
//                    currentNavigationMessage.setCic(getbits(buff, normal+ step*18 + unit * i, 24) / divisor30);
//                    currentNavigationMessage.setOMEGA(getbits(buff, normal+ step*19 + unit * i, 24) / divisor16);
//                    currentNavigationMessage.setCis(getbits(buff, normal+ step*20 + unit * i, 24) / divisor30);
//                    currentNavigationMessage.setI0(getbits(buff, normal+ step*21 + unit * i, 24) / divisor16);
//                    currentNavigationMessage.setCrc(getbits(buff, normal+ step*22 + unit * i, 24) / divisor16);
//                    currentNavigationMessage.setW(getbits(buff, normal+ step*23 + unit * i, 24) / divisor16);
//                    currentNavigationMessage.setOMEGA_DOT(getbits(buff, normal+ step*24 + unit * i, 24) / divisor30);
//                    currentNavigationMessage.setIDOT(getbits(buff, normal+ step*25 + unit * i, 24) / divisor38);
//                    currentNavigationMessage.setPRC(getbits(buff, normal+ step*26 + unit * i, 24) * P2_29 * RANGE_MS);
//                    currentNavigationMessage.setGPS_time(GPS_time);
//                    navigationInfoDBTools.insertNavigationInfo(currentNavigationMessage);
//                    Log.d("prn",""+currentNavigationMessage.getPrn());
//                    Log.d("gpstime",""+currentNavigationMessage.getGPS_time());
//                    //navigationMessageRaw.add(currentNavigationMessage);
//                }
//            }
//            findA=false;
//            findB=false;
//
//        }
//    }
    private int getbitu(char[]buff, int pos, int len)//不区分正负
    {
        int bits = 0;
        int i;
        for (i = pos; i < pos + len; i++) {
            char b = buff[i / 8];
            bits = (bits << 1) + ((buff[i / 8] >> (7 - i % 8)) & 1);
        }
        return bits;
    }
    private int getbits(char[]buff, int pos, int len)//区分正负
    {
        int bits = getbitu(buff, pos, len);
        if (len <= 0 || 32 <= len || (bits & (1 << (len - 1)))!=8388608)//取bits的第一位
            return (int)bits;
        return (int)(((~bits)&16777215)+1)*-1; /* extend sign */
    }

    public static class DataReceiveHandler extends Handler {
        private DataReceiveListener listener;
        //private final WeakReference<T> mActivity;

        public DataReceiveHandler(DataReceiveListener listener) {
            this.listener = listener;
            //this.mActivity= new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch(msg.what){
                case CONNECT_ERROR:
                    listener.onError();
                    break;
                case CONNECT_ONLINE:
                    listener.onConnect();
                    break;
                default:
            }
        }
        public interface DataReceiveListener{
            void onError();
            void onConnect();
        }
    }

}

