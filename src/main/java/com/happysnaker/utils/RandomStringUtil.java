package com.happysnaker.utils;

import java.util.Random;

public class RandomStringUtil {
    private static int length = 6;


    private static final String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String getRandomString(int length){
        RandomStringUtil.length = length;
        Random random = new Random();
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(random.nextInt(characters.length()));
        }
       return new String(text);

    }
}
