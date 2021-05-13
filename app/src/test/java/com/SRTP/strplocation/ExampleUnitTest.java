package com.SRTP.strplocation;

import com.SRTP.strplocation.GnssCalculator.NavigationMessageRaw;
import com.SRTP.strplocation.GnssCalculator.Sat;
import com.SRTP.strplocation.GnssCalculator.SinglePositioning;
import com.SRTP.strplocation.Utils.AxesTransferUtil;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        //assertEquals(4, 2 + 2);
//        final double C_light=299792458.458;
////        double SpreadTime = 2.129578573431351E7 / C_light;
////        Sat sat=new Sat();
////        sat.setPseudo(2.129578573431351E7);
////        sat.setSattype(Sat.SatType.GPS);
////        sat.setPRN(2);
////        double[] XYZ={0,0,0};
////        NavigationMessageRaw navigationMessageRaw=new NavigationMessageRaw(2,2021, 04, 29, 14, 00, 00,0,0,-0.599943567067e-03,-0.329691829393e-11, 0.000000000000e+00,
////        0.370000000000e+02,-0.143093750000e+03, 0.413517224652e-08,-0.177672003888e+01,
////                -0.719353556633e-05,0.202166513773e-01,0.910833477974e-05, 0.515366916656e+04,
////        0.396000000000e+06, 0.281259417534e-06,-0.302055994178e+01,0.480562448502e-06,
////        0.963122717790e+00, 0.203281250000e+03,-0.152227151404e+01,-0.781246827781e-08,
////                -0.229652423088e-09);
////
////        SinglePositioning.Calc_Eph_GCEJ(393609.43388723,
////                SpreadTime,
////                sat,
////                XYZ,
////                navigationMessageRaw);
////        System.out.println("x="+sat.getPOS_X());
////        System.out.println("y="+sat.getPOS_Y());
////        System.out.println("z="+sat.getPOS_Z());
        double[] bd09 = AxesTransferUtil.gps84_To_bd09(31.89288369166322, 118.82080978249886);
        System.out.println("bd09 = " + bd09[0]+","+bd09[1]);
        double distanceFromXtoY = AxesTransferUtil.getDistanceFromXtoY(bd09[0], bd09[1], 31.896972, 118.832609);
        System.out.println("distanceFromXtoY = " + distanceFromXtoY);
    }
}