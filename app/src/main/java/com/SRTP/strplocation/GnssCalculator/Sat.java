package com.SRTP.strplocation.GnssCalculator;

import android.location.GnssStatus;

public class Sat {
        public enum SatType{GPS,BDS}
        double pseudo;                   //卫星的伪距
        SatType sattype;                  //卫星的类型
        int PRN;                    //卫星的序号（PRN）

        double POS_X;                    //卫星位置X
        double POS_Y;                    //卫星位置Y
        double POS_Z;                    //卫星位置Z

        double r;                        //卫星与测站间距离
        double A;                        //方位角
        double E;                        //高度角


        double xdl_t;                    //相对论效应的影响（时间 s）
        double trop;                     //对流层延迟1

        //误差项
        double Sat_clock;                //卫星钟差
        double Trop_Delay;               //对流层延迟2
        double Trop_Map;                 //对流层湿延迟投影
        double Sagnac;                   //地球自转
        double E_weight =0;
        double PRC;


        public double getPseudo() {
                return pseudo;
        }

        public void setPseudo(double pseudo) {
                this.pseudo = pseudo;
        }

        public SatType getSattype() {
                return sattype;
        }

        public void setSattype(SatType sattype) {
                this.sattype = sattype;
        }

        public int getPRN() {
                return PRN;
        }

        public void setPRN(int PRN) {
                this.PRN = PRN;
        }

        public double getPOS_X() {
                return POS_X;
        }

        public void setPOS_X(double POS_X) {
                this.POS_X = POS_X;
        }

        public double getPOS_Y() {
                return POS_Y;
        }

        public void setPOS_Y(double POS_Y) {
                this.POS_Y = POS_Y;
        }

        public double getPOS_Z() {
                return POS_Z;
        }

        public void setPOS_Z(double POS_Z) {
                this.POS_Z = POS_Z;
        }

        public double getR() {
                return r;
        }

        public void setR(double r) {
                this.r = r;
        }

        public double getA() {
                return A;
        }

        public void setA(double a) {
                A = a;
        }

        public double getE() {
                return E;
        }

        public void setE(double e) {
                E = e;
        }

        public double getXdl_t() {
                return xdl_t;
        }

        public void setXdl_t(double xdl_t) {
                this.xdl_t = xdl_t;
        }

        public double getTrop() {
                return trop;
        }

        public void setTrop(double trop) {
                this.trop = trop;
        }

        public double getSat_clock() {
                return Sat_clock;
        }

        public void setSat_clock(double sat_clock) {
                Sat_clock = sat_clock;
        }

        public double getTrop_Delay() {
                return Trop_Delay;
        }

        public void setTrop_Delay(double trop_Delay) {
                Trop_Delay = trop_Delay;
        }

        public double getTrop_Map() {
                return Trop_Map;
        }

        public void setTrop_Map(double trop_Map) {
                Trop_Map = trop_Map;
        }

        public double getSagnac() {
                return Sagnac;
        }

        public void setSagnac(double sagnac) {
                Sagnac = sagnac;
        }

        public double getE_weight() {
                return E_weight;
        }

        public void setE_weight(double e_weight) {
                E_weight = e_weight;
        }

        public double getPRC() {
                return PRC;
        }

        public void setPRC(double PRC) {
                this.PRC = PRC;
        }

        public boolean equals(SatType type, int prn){
            return this.sattype==type && this.PRN==prn;
        }

        public static SatType getSatType(int svTypeNum){
                switch(svTypeNum){
                        case GnssStatus.CONSTELLATION_GPS:
                                return Sat.SatType.GPS;
                        case GnssStatus.CONSTELLATION_BEIDOU:
                                return Sat.SatType.BDS;
                        default:
                                return SatType.GPS;
                }
        }
        public static String satType_toString(SatType satType){
                switch(satType){
                        case GPS:return "G";
                        case BDS:return "B";
                    default:return "null";
                }
        }

    }
