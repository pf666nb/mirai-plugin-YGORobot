package com.happysnaker.api;

import com.happysnaker.utils.NetUtil;

import java.net.URL;
import java.util.Map;

/**
 * @author Happysnaker
 * @description
 * @date 2022/1/29
 * @email happysnaker@foxmail.com
 */
public class BingApi {
    static String url = "https://bing.ioliu.cn/v1/rand?type=json";

    public static String getRandomImageUrl() {
        try {
            Map<String, Object> map = (Map<String, Object>) NetUtil.sendAndGetResponseMap(new URL(url), "GET", null, null).get("data");
            return (String) map.get("url");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "http://h1.ioliu.cn/bing/HallesWood_ZH-CN9790575479_1920x1080.jpg?imageslim";
    }
}
