package com.happysnaker.utils;

/**
 * 一些基础的实用程序
 * @author Happysnaker
 * @description
 * @date 2022/1/17
 * @email happysnaker@foxmail.com
 */
public class BaseUtils {

    public static double intToDouble(Object val) {
        int v1 = (int) val;
        return Double.parseDouble(String.valueOf(v1));
    }

    public static double intToDouble(int val) {
        return intToDouble(val);
    }

    public static long intToLong(Object val) {
        int v1 = (int) val;
        return Long.parseLong(String.valueOf(v1));
    }

    public static long intToLong(int val) {
        return intToLong(val);
    }
}
