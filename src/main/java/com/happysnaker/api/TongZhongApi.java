package com.happysnaker.api;

import com.happysnaker.utils.IOUtil;
import net.mamoe.mirai.message.data.MusicKind;
import net.mamoe.mirai.message.data.MusicShare;

import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author Happysnaker
 * @description
 * @date 2022/1/29
 * @email happysnaker@foxmail.com
 */
public class TongZhongApi {
    public static String ok = "ok";
    public static String url1 = "https://tonzhon.com/api/exact_search?keyword=";
    public static String url2 = "https://tonzhon.com/secondhand_api/song_source/platform/id";
    public static String url3 = "https://tonzhon.com/api/fuzzy_search?keyword=";
    public static String url4 = "https://tonzhon.com/secondhand_api/search?platform=qq&keyword=";
    public static String url5 = "https://tonzhon.com/secondhand_api/search?platform=netease&keyword=";
    public static String neteaseJumpUrl = "http://music.163.com/#/song?id=originalId";
    public static String qqJumpUrl = "http://y.qq.com/n/yqq/song/originalId.html";
    public static String kuwoJumpUrl = "http://www.kuwo.cn/yinyue/originalId/";
    public static Map<String, MusicKind> musicKindMap = new HashMap<>(5);
    public static Map<String, String> jumpMap = new HashMap<>(5);

    static {
        musicKindMap.put("qq", MusicKind.QQMusic);
        musicKindMap.put("netease", MusicKind.NeteaseCloudMusic);
        musicKindMap.put("kuwo", MusicKind.KuwoMusic);

        jumpMap.put("qq", qqJumpUrl);
        jumpMap.put("netease", neteaseJumpUrl);
        jumpMap.put("kuwo", kuwoJumpUrl);

    }

    public static MusicShare getSongUrl(String keyword) {
        try {
//            MusicKind neteaseCloudMusic = MusicKind.NeteaseCloudMusic;
            List<Map<String, Object>> s1 = getSongs(keyword, url3);
            List<Map<String, Object>> s2 = getSongs(keyword, url4);
            List<Map<String, Object>> s3 = getSongs(keyword, url5);
            List<Map<String, Object>> songs = new ArrayList<>(getSongs(keyword, url1));
            System.out.println("getSongs(keyword, url1).size() = " + getSongs(keyword, url1).size());
            int n = Math.max(Math.max(s1.size(), s2.size()), s3.size());
            for (int i = 0; i < n; i++) {
                if (i < s1.size()) {
                    songs.add(s1.get(i));
                }
                if (i < s2.size()) {
                    songs.add(s2.get(i));
                }
                if (i < s3.size()) {
                    songs.add(s3.get(i));
                }
            }

            if (Math.random() <= 0.2) {
                songs.sort((a, b) -> {
                    if (a == null && b == null) {
                        return 0;
                    }
                    return Math.random() <= 0.5 ? 1 : -1;
                });
            }

            for (Map<String, Object> song : songs) {
                try {
                    String name = (String) song.get("name");
                    String id = String.valueOf(song.get("originalId"));
                    String platform = (String) song.get("platform");
                    String url = url2.replace("platform", platform).replace("id", id);
                    Map<String, Object> map = IOUtil.sendAndGetResponseMap(new URL(url), "GET", null, null);
                    String songUrl = null;
                    songUrl = (String) ((Map<?, ?>) map.get("data")).get("songSource");
                    if (songUrl != null) {
                        return new MusicShare(
                                musicKindMap.get(platform),
                                name,
                                "请点击右侧按钮播放",
                                jumpMap.get(platform).replace("originalId", id),
                                "http://p2.music.126.net/y19E5SadGUmSR8SZxkrNtw==/109951163785855539.jpg",
                                songUrl
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Map<String, Object>> getSongs(String keyword, String url) throws Exception {
        Map<String, Object> res = null;
        try {
            res = (Map<String, Object>) IOUtil.sendAndGetResponseMap(new URL(url + URLEncoder.encode(keyword, "UTF-8")), "GET", null, null);
        } catch (Exception e) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> songs = null;
        if (res != null) {
            if (!url.equals(url1)) {
                res = (Map<String, Object>) res.get("data");
            }
            if (res != null) {
                songs = (List<Map<String, Object>>) res.getOrDefault("songs", null);
            }
        }
//        System.out.println("songs = " + songs);
        return songs == null ? new ArrayList<>() : songs;
    }
}
