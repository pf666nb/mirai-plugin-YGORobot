package com.happysnaker.api;

import com.happysnaker.utils.IOUtil;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * 全国疫情 API
 */
public class PneumoniaApi {
    static String api = "https://c.m.163.com/ug/api/wuhan/app/data/list-total";

    public static Map<String, Object> queryPneumoniaMap(String area) throws IOException {
        String grt = IOUtil.sendAndGetResponseString(new URL(api), "GET", null, null);
        Map<String, Object> map = (Map<String, Object>) IOUtil.sendAndGetResponseMap(new URL(api), "GET", null, null).get("data");
        if (area == null || area.isEmpty()) {
            area = "中国";
        }
        List<Map<String, Object>> areaTree = (List<Map<String, Object>>) map.get("areaTree");
        return dfs(area, areaTree);
    }

    private static Map<String, Object> dfs(String area, List<Map<String, Object>> areaTree) throws IOException {
        if (areaTree == null || areaTree.isEmpty()) {
            return null;
        }
        Map<String, Object> map = null;
        for (Map<String, Object> obj : areaTree) {
            if (obj.getOrDefault("name", "").equals(area)) {
                map = obj;
                break;
            }
            if (obj.get("children") != null) {
                if ((map = dfs(area, (List<Map<String, Object>>) obj.get("children"))) != null) {
                    break;
                }
            }
        }
        return map;
    }
}
