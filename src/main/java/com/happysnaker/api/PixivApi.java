package com.happysnaker.api;

import com.happysnaker.utils.NumUtil;
import com.happysnaker.utils.IOUtil;

import java.io.IOException;
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
    /**
     * 能用 HTTP 的就用 HTTP，HTTPS 性能比较低，实验比较大
     */
    public static final String pidApi = "http://api.lolicon.app/setu/v2";
    public static final String beautifulImageUrl = "https://api.sunweihu.com/api/sjbz/api.php";
    public static final String chickenSoupUrl = "https://api.shadiao.app/chp/";
    public static final String duChickenSoupUrl = "https://api.shadiao.app/du";
    public static final String pixivSearchApi = "http://pximg.rainchan.win/img?img_id=IMGID";
    public static final String randomUrl = "http://pximg.rainchan.win/img";




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
        List<Map<String, Object>> map = (List<Map<String, Object>>) IOUtil.sendAndGetResponseMap(new URL(url), "GET", null, null).get("data");
        if (map == null || map.isEmpty()) {
            return -1;
        }
        try {
            return (long) map.get(0).get("pid");
        } catch (ClassCastException e) {
            return (long) NumUtil.objectToDouble(map.get(0).get("pid"));
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
        List<Map<String, Object>> map = (List<Map<String, Object>>) IOUtil.sendAndGetResponseMap(new URL(url), "GET", null, null).get("data");
        if (map == null || map.isEmpty()) {
            return -1;
        }
        try {
            return (long) map.get(0).get("pid");
        } catch (ClassCastException e) {
            return (long) NumUtil.objectToDouble(map.get(0).get("pid"));
        }
    }

}

