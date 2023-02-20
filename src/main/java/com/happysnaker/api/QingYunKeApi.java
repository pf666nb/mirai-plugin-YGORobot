package com.happysnaker.api;

import com.happysnaker.utils.IOUtil;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 青云客 API 类，官网 <a href="http://api.qingyunke.com/">http://api.qingyunke.com/</a>
 * @author Happysnaker
 * @description
 * @date 2022/1/14
 * @email happysnaker@foxmail.com
 */
public class QingYunKeApi {
    public static final String url = "http://api.qingyunke.com/api.php";

    public static String getMessage(String queryContent) {
        try {
            URL obj = new URL(url + "?key=free&appid=0&msg=" + URLEncoder.encode(queryContent, "UTF-8"));
            Map map = IOUtil.sendAndGetResponseMap(obj, "GET", null, null);
            return ((String) map.get("content")).replaceAll("\\{br\\}", "\n");
        } catch (Exception e) {
            e.printStackTrace();
            return "理解不了呢";
        }
    }
}
