package com.happysnaker.api;

import com.happysnaker.config.Logger;
import com.happysnaker.utils.IOUtil;
import com.happysnaker.utils.MapGetter;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * P 站 API
 * @author Happysnaker
 * @description
 * @date 2022/1/20
 * @email happysnaker@foxmail.com
 */
public class PixivApi {
    public static final String pidApi = "https://api.lolicon.app/setu/v2?size=original&size=small";

    // ---------------- 一些其他直接使用的 API ---------------
    /**
     * 二次元图片 API
     */
    public static final String beautifulImageUrl = "https://api.vvhan.com/api/acgimg";
    /**
     * 鸡汤与毒鸡汤 API
     */
    public static final String chickenSoupUrl = "https://api.shadiao.pro/chp/";
    public static final String duChickenSoupUrl = "https://api.shadiao.pro/du";



    public static String searchImage(List<String> tags, boolean r18, boolean small) throws IOException {
        StringBuilder url = new StringBuilder(pidApi);
        if (r18) {
            url.append("&r18=1");
        }
        if (tags != null) {
            url.append("&");
            for (String tag : tags) {
                url.append("tag=").append(URLEncoder.encode(tag, "UTF-8")).append("&");
            }
            url = new StringBuilder(url.substring(0, url.length() - 1));
        }
        Logger.debug("pixiv search url = " + url);
        List<MapGetter> map = IOUtil.sendAndGetResponseMapGetter(new URL(url.toString()), "GET", null, null).getMapGetterList("data");
        if (map == null || map.isEmpty()) {
            return null;
        }
        return small ?
                map.get(0).getMapGetter("urls").getString("small") :
                map.get(0).getMapGetter("urls").getString("original");
    }

}

