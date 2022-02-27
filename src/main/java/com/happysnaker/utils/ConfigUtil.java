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










    public static final String TEMPLATE =
            "{\n" +
                    "    \"menu\":\"主菜单\",\n" +
                    "    \"administrator\":[\"超级管理员QQ号\", \"管理员QQ号\", \"管理员QQ号\"],\n" +
                    "    \"gtAdministrator\":[\"坎公管理员QQ号\", \"坎公管理员QQ号\"],\n" +
                    "    \"exclude\":[\"群号1\", \"群号2\"],\n" +
                    "    \"include\":[],\n" +
                    "    \"pictureWithdrawalTime\": 30,\n" +
                    "    \"gtConfig\":[\n" +
                    "         {\n" +
                    "             \"groupId\":\"群号1\",\n" +
                    "             \"gtCookie\":\"cookie1\"\n" +
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
}
