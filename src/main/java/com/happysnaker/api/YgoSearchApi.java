package com.happysnaker.api;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.happysnaker.config.Logger;
import com.happysnaker.entry.CardBeanByBaige;
import com.happysnaker.entry.CardEntry;
import com.happysnaker.utils.IOUtil;
import com.happysnaker.utils.MapGetter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * plugin
 * 游戏王卡片检索Api
 *
 * @author : wpf
 * @date : 2023-08-01 08:46
 **/
public class YgoSearchApi {


    //请求该接口获取随机的一张卡
    public static final  String RandomApi ="https://api.wpfzhy.cn/api/yugioh/random-card?lang=sc&source=";


    //请求该接口获取相关对应的卡片集合
    public static final  String SearchCardApi ="https://ygocdb.com/api/v0/?search=";

    /**
     * @return
     * @throws IOException
     */
    public static CardEntry RandomImage() throws IOException {
        Logger.debug("randomImage get "+ RandomApi);
        String s = HttpUtil.get(RandomApi, CharsetUtil.CHARSET_UTF_8);
        return JSONUtil.toBean(JSONUtil.parseObj(s).getStr("data"), CardEntry.class);
    }


    public static CardBeanByBaige getImageByKeyWord(List<String> tags) throws IOException {
        String s = HttpUtil.get(SearchCardApi+tags.get(0), CharsetUtil.CHARSET_UTF_8);
        final JSONArray result1 = JSONUtil.parseObj(s).getJSONArray("result");

        return result1.get(0, CardBeanByBaige.class);
    }
    public static void main(String[] args) {
        //测试随机api的使用
//        final String s = HttpUtil.get(RandomApi, CharsetUtil.CHARSET_UTF_8);
//        final CardEntry bean = JSONUtil.toBean(JSONUtil.parseObj(s).getStr("data"), CardEntry.class);
//        System.out.println(bean.getName());
//        System.out.println(bean.getId());
        String s = HttpUtil.get(SearchCardApi+"青眼", CharsetUtil.CHARSET_UTF_8);
        System.out.println(s);
        final JSONArray result1 = JSONUtil.parseObj(s).getJSONArray("result");
        final CardBeanByBaige cardBeanByBaige = result1.get(0, CardBeanByBaige.class);
        System.out.println(cardBeanByBaige.getId());

    }

}
