package com.happysnaker.api;

import com.happysnaker.utils.IOUtil;
import com.happysnaker.utils.MapGetter;

import java.net.URL;

/**
 * Bing 壁纸 API
 * @author Happysnaker
 * @description
 * @date 2022/1/29
 * @email happysnaker@foxmail.com
 */
public class BingWallpaperAPI {
    public static String url = "https://bing.ioliu.cn/v1/rand?type=json";

    public static String getRandomImageUrl() {
        String def = "http://h1.ioliu.cn/bing/HallesWood_ZH-CN9790575479_1920x1080.jpg?imageslim";
        try {
            MapGetter mg = IOUtil.sendAndGetResponseMapGetter(new URL(url), "GET", null, null).getMapGetter("data");
            return mg.getStringOrDefault("url", def  , true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
