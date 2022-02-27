package com.happysnaker.api;

import com.happysnaker.handler.impl.AbstractMessageHandler;
import com.happysnaker.utils.NetUtil;
import net.mamoe.mirai.message.data.MessageChain;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author Happysnaker
 * @description
 * @date 2022/2/26
 * @email happysnaker@foxmail.com
 */
public class BaiduBaikeApi {
    public static final String api = "http://jiuli.xiaoapi.cn/i/baidu_baike.php?msg=";

    public static Map<String, Object> search(String msg) {
        try {
            URL url = new URL(api + URLEncoder.encode(msg, "UTF-8"));
            Map<String, Object> map = NetUtil.sendAndGetResponseMap(url, "GET", null, null);
            return map;
        } catch (Exception e) {
            AbstractMessageHandler.failApi(null, e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
