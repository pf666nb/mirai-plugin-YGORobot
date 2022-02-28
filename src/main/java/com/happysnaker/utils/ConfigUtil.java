package com.happysnaker.utils;

import com.alibaba.fastjson.JSONObject;
import com.happysnaker.config.RobotConfig;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Happysnaker
 * @description
 * @date 2022/2/15
 * @email happysnaker@foxmail.com
 */
public class ConfigUtil {
    public static Set<String> configNames = Set.of(
            "administrator",
            "gtAdministrator",
            "groupAdministrator",
            "menu",
            "gtConfig",
            "include",
            "exclude",
            "pictureWithdrawalTime",
            "customKeywordSimilarity",
            "customKeyword"
    );


    public static String getConfigFilePath() {
        return RobotConfig.configFolder + "/" + RobotConfig.mainConfigPathName;
    }

    public static String getConfigFilePath(String fileName) {
        return RobotConfig.configFolder + "/" + fileName;
    }

    public static String getDataFilePath(String fileName) {
        return RobotConfig.dataFolder + "/" + fileName;
    }

    public static Map<String, Object> getConfigMap() {
        try {
            Field[] fields = RobotConfig.class.getDeclaredFields();
            Map<String, Object> map = new HashMap<>();
            for (Field field : fields) {
                if (configNames.contains(field.getName())) {
                    map.put(field.getName(), field.get(null));
                }
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized static void writeConfig() throws Exception {
        File file = new File(getConfigFilePath());
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(JSONObject.toJSONString(getConfigMap(), true).getBytes());
        }
    }


    public static String TEMPLATE =
            "{\n" +
                    "    \"menu\":\"主菜单\",\n" +
                    "    \"administrator\":[\"超级管理员QQ号\", \"管理员QQ号\", \"管理员QQ号\"],\n" +
                    "    \"gtAdministrator\":[\"坎公管理员QQ号\", \"坎公管理员QQ号\"],\n" +
                    "    \"groupAdministrator\":[\"群管理员QQ号\", \"群管理员QQ号\"],\n" +
                    "    \"customKeywordSimilarity\": 0.8,\n" +
                    "    \"customKeyword\":{\n" +
                    "        \"全局关键字\":\"全局关键字回复\",\n" +
                    "        \"#regex#全局正则表达式\":\"回复\",\n" +
                    "        \"群号1\":{\n" +
                    "            \"群内关键字\":\"群内关键字回复\"\n" +
                    "         }\n" +
                    "    },\n" +
                    "    \"exclude\":[\"群号1\", \"群号2\"],\n" +
                    "    \"include\":[],\n" +
                    "    \"pictureWithdrawalTime\": 30,\n" +
                    "    \"gtConfig\":[\n" +
                    "         {\n" +
                    "             \"groupId\":\"群号1\",\n" +
                    "             \"gtCookie\":\"cookie1\",\n" +
                    "             \"members\":[\"成员1\", \"成员2\"]\n" +
                    "         },\n" +
                    "         {\n" +
                    "             \"groupId\":\"群号2\",\n" +
                    "             \"gtCookie\":\"cookie2\"\n" +
                    "         },\n" +
                    "         {\n" +
                    "             \"groupId\":\"\",\n" +
                    "             \"gtCookie\":\"cookie3\"\n" +
                    "         }\n" +
                    "    ]\n" +
                    "}";
//    String s = ""
}
