package com.happysnaker.api;

import com.happysnaker.config.Logger;
import com.happysnaker.entry.CardEntry;
import com.happysnaker.utils.IOUtil;
import com.happysnaker.utils.MapGetter;

import java.io.IOException;
import java.net.URL;
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
        StringBuilder url = new StringBuilder(RandomApi);
        Logger.debug("randomImage get "+ url);
        List<MapGetter> map = IOUtil.sendAndGetResponseMapGetter(new URL(url.toString()), "GET", null, null).getMapGetterList("data");
        if (map == null || map.isEmpty()) {
            return null;
        }
        //TODO 待优化，暂时冗余
        CardEntry cardEntry = new CardEntry();
        cardEntry.setId(map.get(0).getString("id"));
        cardEntry.setName(map.get(0).getString("name"));
        cardEntry.setDesc(map.get(0).getString("desc"));
        cardEntry.setAtk(Integer.parseInt(map.get(0).getString("atk")));
        cardEntry.setDef(Integer.parseInt(map.get(0).getString("def")));
        cardEntry.setType(Integer.parseInt(map.get(0).getString("type")));
        cardEntry.setRace(Integer.parseInt(map.get(0).getString("race")));
        cardEntry.setLevel(Integer.parseInt(map.get(0).getString("level")));
        cardEntry.setAttribute(Integer.parseInt(map.get(0).getString("attribute")));

        return cardEntry;
    }


    public static String getImageByKeyWord(List<String> tags) {

        return null;
    }
}
