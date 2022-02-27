package com.happysnaker.utils;

import java.text.NumberFormat;

/**
 * 一些基础的实用程序
 * @author Happysnaker
 * @description
 * @date 2022/1/17
 * @email happysnaker@foxmail.com
 */
public class NumUtil {

    public static double intToDouble(Object val) {
        int v1 = (int) val;
        return Double.parseDouble(String.valueOf(v1));
    }

    public static double intToDouble(int val) {
        return intToDouble(val);
    }

    public static long intToLong(Object val) {
//        int v1 = (int) val;
        return Long.parseLong(String.valueOf(val));
    }

    public static long objectToLong(Object val) {
        return Long.parseLong(val.toString());
    }

    public static long intToLong(int val) {
        return intToLong(val);
    }

    public static String getPercentage(long p, long t) {
        double s =  (double) p * 1.0/ t * 1.0;
        System.out.println("s = " + s);
        NumberFormat nf = java.text.NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);// 小数点后保留几位
        String str = nf.format(s);
        return str;
    }
}
