package com.happysnaker.api;

import com.happysnaker.utils.IOUtil;
import com.happysnaker.utils.MapGetter;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * P 站
 * @author Happysnaker
 * @description
 * @date 2022/1/20
 * @email happysnaker@foxmail.com
 */
public class PixivApi {
    /**
     * 能用 HTTP 的就用 HTTP，HTTPS 性能比较低，延迟比较大
     */
    public static final String pidApi = "https://api.lolicon.app/setu/v2?size=original&size=small";
    public static final String beautifulImageUrl = "https://tenapi.cn/acg";
    public static final String chickenSoupUrl = "https://api.shadiao.app/chp/";
    public static final String duChickenSoupUrl = "https://api.shadiao.app/du";
    @Deprecated
    public static final String pixivSearchApi = "http://pximg.rainchan.win/img?img_id=IMGID";



    /**
     * 根据 tag 获取 p 站图片 url 地址
     * @param tags
     * @return
     * @throws IOException
     */
    @Deprecated
    public static String getImagePid(List<String> tags) throws IOException {
        return searchImage(tags, false, false);
    }


    /**
     * 根据 tag 获取 p '涩' 图片 pid
     * @param tags
     * @return
     * @throws IOException
     */
    @Deprecated
    public static String getSeImagePid(List<String> tags, boolean small) throws IOException {
        return searchImage(tags, true, small);
    }

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
        System.out.println("url = " + url);
        List<MapGetter> map = IOUtil.sendAndGetResponseMapGetter(new URL(url.toString()), "GET", null, null).getMapGetterList("data");
        if (map == null || map.isEmpty()) {
            return null;
        }
        return small ?
                map.get(0).getMapGetter("urls").getString("small") :
                map.get(0).getMapGetter("urls").getString("original");
    }

}

