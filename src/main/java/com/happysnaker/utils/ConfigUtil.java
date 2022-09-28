package com.happysnaker.utils;

import com.alibaba.fastjson.JSONObject;
import com.happysnaker.config.RobotConfig;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * @author Happysnaker
 * @description
 * @date 2022/2/15
 * @email happysnaker@foxmail.com
 */
public class ConfigUtil {
    public static Set<String> configNames = OfUtil.ofSet(
            "administrator",
            "gtAdministrator",
            "groupAdministrator",
            "menu",
            "gtConfig",
            "include",
            "exclude",
            "pictureWithdrawalTime",
            "customKeywordSimilarity",
            "customKeyword",
            "commandPrefix",
            "timeout",
            "duChickenSoupProbability",
            "enableAt",
            "replyReplace",
            "autoApproval",
            "skipIsMeaninglessWord",
            "withdrawalThreshold",
            "enableSensitiveWordDetection",
            "skipStep",
            "periodicTask",
            "colorSwitch",
            "russianRoulette"
    );


    /**
     * 获取主配置文件路径
     * @return
     */
    public static String getConfigFilePath() {
        return RobotConfig.configFolder + "/" + RobotConfig.mainConfigPathName;
    }

    /**
     * 根据配置文件名返回配置路径
     * @param fileName
     * @return
     */
    public static String getConfigFilePath(String fileName) {
        return RobotConfig.configFolder + "/" + fileName;
    }

    /**
     * 根据数据文件名返回存放数据文件的路径
     * @param fileName
     * @return
     */
    public static String getDataFilePath(String fileName) {
        return RobotConfig.dataFolder + "/" + fileName;
    }


    /**
     * 将配置转换未 map 的形式返回
     * @return
     */
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

    /**
     * 将配置动态写入配置文件，这个方法是串行的
     * @throws Exception
     */
    public synchronized static void writeConfig() throws Exception {
        File file = new File(getConfigFilePath());
        try (OutputStream outputStream = Files.newOutputStream(file.toPath())) {
            new Yaml().dump(getConfigMap(), new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        }
    }

    /**
     * 配置类模板，在第一次创建配置文件时会写入此模板
     */
    public static String TEMPLATE = Objects.requireNonNull(IOUtil.readFromFile(new File("config_template.yaml"))).replace("$CURRENT_VERSION", RobotConfig.CURRENT_VERSION);
}
