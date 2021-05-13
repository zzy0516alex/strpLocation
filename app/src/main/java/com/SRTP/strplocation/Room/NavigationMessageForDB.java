package com.SRTP.strplocation.Room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NavigationMessageForDB {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "prn")
    private int prn;

    @ColumnInfo(name = "year")
    private int year;

    @ColumnInfo(name = "month")
    private int month;

    @ColumnInfo(name = "day")
    private int day;

    @ColumnInfo(name = "hour")
    private int hour;

    @ColumnInfo(name = "minute")
    private int minute;

    @ColumnInfo(name = "second")
    private double second;

    @ColumnInfo(name = "a0")
    private double a0;//卫星钟偏差

    @ColumnInfo(name = "a1")
    private double a1;//卫星钟漂移

    @ColumnInfo(name = "a2")
    private double a2;//卫星钟频率漂移

    @ColumnInfo(name = "IDOE")
    private double IDOE;//星历数据有效期=T（用户测量时）-TOE

    @ColumnInfo(name = "Crs")
    private double Crs;//地心距摄动改正项

    @ColumnInfo(name = "delta_n")
    private double delta_n;//卫星角速度偏差

    @ColumnInfo(name = "M0")
    private double M0;//TOE时的平近点角

    @ColumnInfo(name = "Cuc")
    private double Cuc;//升交角距摄动改正项

    @ColumnInfo(name = "e")
    private double e;//卫星轨道偏心率

    @ColumnInfo(name = "Cus")
    private double Cus;//升交角距摄动改正项

    @ColumnInfo(name = "sqrtA")
    private double sqrtA;//卫星轨道长半径平方根

    @ColumnInfo(name = "TOE")
    private double TOE;//星历参考时

    @ColumnInfo(name = "Cic")
    private double Cic;//倾角的摄动改正项

    @ColumnInfo(name = "OMEGA")
    private double OMEGA;//升交点赤径

    @ColumnInfo(name = "Cis")
    private double Cis;//倾角的摄动改正项

    @ColumnInfo(name = "i0")
    private double i0;//轨道倾角

    @ColumnInfo(name = "Crc")
    private double Crc;//地心距摄动改正项

    @ColumnInfo(name = "w")
    private double w;//近地点角距

    @ColumnInfo(name = "OMEGA_DOT")
    private double OMEGA_DOT;//升交点赤径变化率

    @ColumnInfo(name = "iDOT")
    private double IDOT;

    @ColumnInfo(name = "PRC")
    private double PRC;//改正数

    @ColumnInfo(name = "GPS_time")
    private double GPS_time;//prc对应的GPS时

    public static final int param_num=28;//不计GPS时

    public NavigationMessageForDB(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPRC() {
        return PRC;
    }

    public void setPRC(double PRC) {
        this.PRC = PRC;
    }

    public int getPrn() {
        return prn;
    }

    public void setPrn(int prn) {
        this.prn = prn;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public double getSecond() {
        return second;
    }

    public void setSecond(double second) {
        this.second = second;
    }

    public double getA0() {
        return a0;
    }

    public void setA0(double a0) {
        this.a0 = a0;
    }

    public double getA1() {
        return a1;
    }

    public void setA1(double a1) {
        this.a1 = a1;
    }

    public double getA2() {
        return a2;
    }

    public void setA2(double a2) {
        this.a2 = a2;
    }

    public double getIDOE() {
        return IDOE;
    }

    public void setIDOE(double IDOE) {
        this.IDOE = IDOE;
    }

    public double getCrs() {
        return Crs;
    }

    public void setCrs(double crs) {
        Crs = crs;
    }

    public double getDelta_n() {
        return delta_n;
    }

    public void setDelta_n(double delta_n) {
        this.delta_n = delta_n;
    }

    public double getM0() {
        return M0;
    }

    public void setM0(double m0) {
        M0 = m0;
    }

    public double getCuc() {
        return Cuc;
    }

    public void setCuc(double cuc) {
        Cuc = cuc;
    }

    public double getE() {
        return e;
    }

    public void setE(double e) {
        this.e = e;
    }

    public double getCus() {
        return Cus;
    }

    public void setCus(double cus) {
        Cus = cus;
    }

    public double getSqrtA() {
        return sqrtA;
    }

    public void setSqrtA(double sqrtA) {
        this.sqrtA = sqrtA;
    }

    public double getTOE() {
        return TOE;
    }

    public void setTOE(double TOE) {
        this.TOE = TOE;
    }

    public double getCic() {
        return Cic;
    }

    public void setCic(double cic) {
        Cic = cic;
    }

    public double getOMEGA() {
        return OMEGA;
    }

    public void setOMEGA(double OMEGA) {
        this.OMEGA = OMEGA;
    }

    public double getCis() {
        return Cis;
    }

    public void setCis(double cis) {
        Cis = cis;
    }

    public double getI0() {
        return i0;
    }

    public void setI0(double i0) {
        this.i0 = i0;
    }

    public double getCrc() {
        return Crc;
    }

    public void setCrc(double crc) {
        Crc = crc;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    public double getOMEGA_DOT() {
        return OMEGA_DOT;
    }

    public void setOMEGA_DOT(double OMEGA_DOT) {
        this.OMEGA_DOT = OMEGA_DOT;
    }

    public double getGPS_time() {
        return GPS_time;
    }

    public void setGPS_time(double GPS_time) {
        this.GPS_time = GPS_time;
    }

    public double getIDOT() {
        return IDOT;
    }

    public void setIDOT(double IDOT) {
        this.IDOT = IDOT;
    }
}
