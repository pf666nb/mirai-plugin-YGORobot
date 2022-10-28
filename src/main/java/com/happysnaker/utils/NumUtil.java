package com.happysnaker.utils;

import java.text.NumberFormat;

/**
 * 一些基础的数字处理程序
 *
 * @author Happysnaker
 * @description
 * @date 2022/1/17
 * @email happysnaker@foxmail.com
 */
@Deprecated
public class NumUtil {

    /**
     * 对于调用 JSON 解析程序将 JSON 转换为 MAP 后，原先的整数会被解释为 Integer，而 Integer 是无法转换为 Long 和 Double 的，因此提供两个方法进行简单处理
     * @param val
     * @return
     */
    public static double objectToDouble(Object val) {
        return Double.parseDouble(val.toString());
    }

    public static long objectToLong(Object val) {
        return Long.parseLong(val.toString());
    }


    /**
     * 将 p/t 以百分比形式返回（保留两位小数）
     * @param p
     * @param t
     * @return
     */
    public static String getPercentage(long p, long t) {
        double s = (double) p * 1.0 / t * 1.0;
        NumberFormat nf = java.text.NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);// 小数点后保留几位
        String str = nf.format(s);
        return str;
    }
}
