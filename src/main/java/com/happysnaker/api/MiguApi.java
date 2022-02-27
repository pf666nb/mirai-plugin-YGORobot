package com.happysnaker.api;

import com.happysnaker.utils.NetUtil;
import net.mamoe.mirai.message.data.MusicKind;
import net.mamoe.mirai.message.data.MusicShare;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * @author Happysnaker
 * @description
 * @date 2022/2/26
 * @email happysnaker@foxmail.com
 */
public class MiguApi {
    public static final String api = "http://pd.musicapp.migu.cn/MIGUM3.0/v1.0/content/search_all.do?&ua=Android_migu&version=5.0.1&text=SONGNAME&pageNo=1&pageSize=1&searchSwitch={%22song%22:1,%22album%22:0,%22singer%22:0,%22tagSong%22:0,%22mvSong%22:0,%22songlist%22:0,%22bestShow%22:1}";


    public static final String url = "http://app.pd.nf.migu.cn/MIGUM2.0/v1.0/content/sub/listenSong.do?toneFlag={formatType}&netType=00&userId=15548614588710179085069&ua=Android_migu&version=5.1&copyrightId=0&contentId={contentId}&resourceType={resourceType}&channel=0";

    public static final MusicShare search(String name) throws IOException {
        String u = api.replace("SONGNAME", URLEncoder.encode(name));
        Map<String, Object> map = NetUtil.sendAndGetResponseMap(new URL(u), "GET", null, null);

        System.out.println("map = " + map);
        Map<String, Object> m0 = (Map<String, Object>) map.get("songResultData");
        Map<String, Object> m1 = (Map<String, Object>) ((List)m0.get("result")).get(0);
        String id = (String) m1.get("id");
        String resourceType = (String) m1.get("resourceType");
        String contentId = (String) m1.get("contentId");
        String songName = (String) m1.get("name");
        String singer = (String) ((Map)((List)m1.get("singers")).get(0)).get("name");
        String picUrl = (String)m1.get("lyricUrl");
        String u0 = url.replace("{formatType}", "SQ")
                .replace("{contentId}", contentId)
                .replace("{resourceType}", resourceType);
        return new MusicShare(
                MusicKind.MiguMusic,
                songName,
                singer  + "(请点击右侧按钮直接播放)",
                "http://music.migu.cn/v2/music/song/" + id,
                picUrl,
                u0
        );
    }
}
