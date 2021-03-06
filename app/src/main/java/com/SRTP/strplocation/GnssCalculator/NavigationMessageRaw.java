package com.SRTP.strplocation.GnssCalculator;

import com.SRTP.strplocation.Room.NavigationMessageForDB;

//GPS和BDS星历的数据结构（文件名后缀***.**p）
public class NavigationMessageRaw
{
        int PRN;
        int year;
        int month;
        int day;
        int hour;
        int minute;
        double second;
        double GPSTIME;
        double TTLSEC;
        double a0;
        double a1;
        double a2;
        double IDOE;
        double Crs;
        double delta_n;
        double M0;
        double Cuc;
        double e;
        double Cus;
        double sqrtA;
        double TOE;
        double Cic;
        double OMEGA;
        double Cis;
        double i0;
        double Crc;
        double w;
        double OMEGA_DOT;
        double i_DOT;
        double code_L2;
        double gps_week;
        double mark_code_L2;
        double pre_sat;//精度
        double hel_sat;//健康状态
        double TGD;
        double IODC;
        double time_sig_send;//电文发送时刻
        int doy;  //年积日（备用）
        double PRC;//改正数

        public NavigationMessageRaw(int PRN, int year, int month, int day, int hour, int minute, double second, double GPSTIME, double TTLSEC, double a0, double a1, double a2, double IDOE, double crs, double delta_n, double m0, double cuc, double e, double cus, double sqrtA, double TOE, double cic, double OMEGA, double cis, double i0, double crc, double w, double OMEGA_DOT, double i_DOT) {
                this.PRN = PRN;
                this.year = year;
                this.month = month;
                this.day = day;
                this.hour = hour;
                this.minute = minute;
                this.second = second;
                this.GPSTIME = GPSTIME;
                this.TTLSEC = TTLSEC;
                this.a0 = a0;
                this.a1 = a1;
                this.a2 = a2;
                this.IDOE = IDOE;
                Crs = crs;
                this.delta_n = delta_n;
                M0 = m0;
                Cuc = cuc;
                this.e = e;
                Cus = cus;
                this.sqrtA = sqrtA;
                this.TOE = TOE;
                Cic = cic;
                this.OMEGA = OMEGA;
                Cis = cis;
                this.i0 = i0;
                Crc = crc;
                this.w = w;
                this.OMEGA_DOT = OMEGA_DOT;
                this.i_DOT = i_DOT;
        }

        public NavigationMessageRaw(NavigationMessageForDB navigationMessageForDB) {
                PRN=navigationMessageForDB.getPrn();
                year=navigationMessageForDB.getYear();
                month=navigationMessageForDB.getMonth();
                day=navigationMessageForDB.getDay();
                hour=navigationMessageForDB.getHour();
                minute=navigationMessageForDB.getMinute();
                second=navigationMessageForDB.getSecond();
                a0=navigationMessageForDB.getA0();
                a1=navigationMessageForDB.getA1();
                a2=navigationMessageForDB.getA2();
                IDOE=navigationMessageForDB.getIDOE();
                Crs=navigationMessageForDB.getCrs();
                delta_n=navigationMessageForDB.getDelta_n();
                M0=navigationMessageForDB.getM0();
                Cuc=navigationMessageForDB.getCuc();
                e=navigationMessageForDB.getE();
                Cus=navigationMessageForDB.getCus();
                sqrtA=navigationMessageForDB.getSqrtA();
                TOE=navigationMessageForDB.getTOE();
                Cic=navigationMessageForDB.getCic();
                OMEGA=navigationMessageForDB.getOMEGA();
                Cis=navigationMessageForDB.getCis();
                i0=navigationMessageForDB.getI0();
                Crc=navigationMessageForDB.getCrc();
                w=navigationMessageForDB.getW();
                OMEGA_DOT=navigationMessageForDB.getOMEGA_DOT();
                i_DOT=navigationMessageForDB.getIDOT();
                code_L2=0;
                gps_week=0;
                mark_code_L2=0;
                pre_sat=0;//精度
                hel_sat=0;//健康状态
                TGD=0;
                IODC=0;
                time_sig_send=0;//电文发送时刻
                doy=0;
                PRC=navigationMessageForDB.getPRC();
        }
};
