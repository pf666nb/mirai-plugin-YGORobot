package com.happysnaker.api;

import com.happysnaker.utils.BaseUtils;
import com.happysnaker.utils.NetUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * P 站
 * @author Happysnaker
 * @description
 * @date 2022/1/20
 * @email happysnaker@foxmail.com
 */
public class PixivApi {
    public static final String pidApi = "https://api.lolicon.app/setu/v2";
    public static final String beautifulImageUrl = "https://api.sunweihu.com/api/sjbz/api.php";
    public static final String chickenSoupUrl = "https://chp.shadiao.app/api.php";
    public static final String pixivSearchApi = "https://pximg2.rainchan.win/img?img_id=IMGID";

    /**
     * 根据 tag 获取 p 站图片 pid
     * @param tags
     * @return
     * @throws IOException
     */
    public static long getImagePid(List<String> tags) throws IOException {
        String url = pidApi;
        if (tags != null) {
            url += "?";
            for (String tag : tags) {
                url += "tag=" + URLEncoder.encode(tag, "UTF-8") + "&";
            }
            url = url.substring(0, url.length() - 1);
        }
        List<Map<String, Object>> map = (List<Map<String, Object>>) NetUtils.sendAndGetResponseMap(new URL(url), "GET", null, null).get("data");
        try {
            return (long) map.get(0).get("pid");
        } catch (ClassCastException e) {
            return (long) BaseUtils.intToDouble(map.get(0).get("pid"));
        }
    }


    /**
     * 根据 tag 获取 p '涩' 图片 pid
     * @param tags
     * @return
     * @throws IOException
     */
    public static long getSeImagePid(List<String> tags) throws IOException {
        String url = pidApi + "?r18=1";
        if (tags != null) {
            url += "&";
            for (String tag : tags) {
                url += "tag=" + URLEncoder.encode(tag, "UTF-8") + "&";
            }
            url = url.substring(0, url.length() - 1);
        }
        List<Map<String, Object>> map = (List<Map<String, Object>>) NetUtils.sendAndGetResponseMap(new URL(url), "GET", null, null).get("data");
        try {
            return (long) map.get(0).get("pid");
        } catch (ClassCastException e) {
            return (long) BaseUtils.intToDouble(map.get(0).get("pid"));
        }
    }

}

