package com.happysnaker.api;

import com.happysnaker.utils.IOUtil;
import com.happysnaker.utils.MapGetter;
import com.happysnaker.utils.StringUtil;
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
    public static String url1 = "https://tonzhon.com/secondhand_api/song_source/platform/id";
    public static String url2 = "https://tonzhon.com/api/exact_search?keyword=";
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
            List<Map<String, Object>> songs = new ArrayList<>(getSongs(keyword, url2));
            List<Map<String, Object>> s1 = getSongs(keyword, url3);
            List<Map<String, Object>> s2 = getSongs(keyword, url4);
            List<Map<String, Object>> s3 = getSongs(keyword, url5);
            int n = Math.max(Math.max(s1.size(), s2.size()), s3.size());
            // 从多个 URL 里搜索聚合
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

            for (int i = 0; i < songs.size(); i++) {
                songs.get(i).put("index", i);
            }
            // 按照名称相似程度排序
            songs.sort((a, b) -> {
                if (a == null || b == null) {
                    return a == null ? 1 : -1;
                }
                String aName = new MapGetter(a).getStringOrDefault("name", "");
                String bName = new MapGetter(b).getStringOrDefault("name", "");
                int adis = StringUtil.getEditDistance(aName, keyword), bdis = StringUtil.getEditDistance(bName, keyword);
                if (adis == bdis) {
                    // 如果名称优先度相同，则按照原本搜索的顺序排序
                    return (int) a.get("index") - (int) b.get("index");
                }
                return adis - bdis;
            });

            // 偶尔随机排一下，来点新鲜感，换不同的版本听听
            if (Math.random() <= 0.2) {
                songs.sort((a, b) -> {
                    if (a == null && b == null) {
                        return 0;
                    }
                    return Math.random() <= 0.5 ? 1 : -1;
                });
            }

            for (Map<String, Object> map : songs) {
                try {
                    MapGetter song = new MapGetter(map);
                    String name = song.getString("name");
                    String id = String.valueOf(song.get("originalId"));
                    String platform = song.getString("platform");
                    String url = url1.replace("platform", platform).replace("id", id);

                    MapGetter urlSource = IOUtil.sendAndGetResponseMapGetter(new URL(url), "GET", null, null);
                    String songUrl = urlSource.getMapGetter("data").getString("songSource");
                    if (songUrl != null) {
                        String singer = "未知歌手";
                        List artists = song.getList("artists");
                        if (artists != null && artists.size() > 0) {
                            singer = new MapGetter(artists.get(0)).getStringOrDefault("name", "未知歌手");
                        }
                        return new MusicShare(
                                musicKindMap.get(platform),
                                name,
                                singer + " 点击右侧按钮播放",
                                jumpMap.get(platform).replace("originalId", id),
                                "http://p2.music.126.net/y19E5SadGUmSR8SZxkrNtw==/109951163785855539.jpg",
                                songUrl
                        );
                    }
                } catch (Exception ignored) {
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
            res = IOUtil.sendAndGetResponseMap(new URL(url + URLEncoder.encode(keyword, "UTF-8")), "GET", null, null);
        } catch (Exception e) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> songs = null;
        if (!url.equals(url2)) {
            res = (Map<String, Object>) res.get("data");
        }
        if (res != null) {
            songs = (List<Map<String, Object>>) res.getOrDefault("songs", null);
        }
        return songs == null ? new ArrayList<>() : songs;
    }
}
