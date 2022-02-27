package com.happysnaker.config;

import net.mamoe.mirai.utils.MiraiLogger;

import java.io.File;
import java.util.*;

/**
 * @author Happysnaker
 * @description
 * @date 2022/1/15
 * @email happysnaker@foxmail.com
 */
public class RobotConfig {
    // ------------ 系统配置 ----------------
    public static String mainConfigPathName = "config.json";

    public static File configFolder;

    public static File dataFolder;

    public static MiraiLogger logger;

    // ------------ 用户配置 ----------------
    /**
     * 主菜单，回复 help 或 帮助 获取
     */
    public static String menu;

    /**
     * 机器人管理员，索引 0 为超级管理员
     */
    public static List<String> administrator = new ArrayList<>();


    /**
     * 群管理员
     */
    public static List<String> groupAdministrator = new ArrayList<>();

    /**
     * 坎公管理员
     */
    public static List<String> gtAdministrator = new ArrayList<>();

    /**
     * 坎公配置
     */
    public static List<Map<String, Object>> gtConfig = new ArrayList<>();

    /**
     * 需要排除的群
     */
    public static List<String> include = new ArrayList<>();

    /**
     * 包括的群
     */
    public static List<String> exclude = new ArrayList<>();

    /**
     * 涩图撤回时间，单位 s
     */
    public static int pictureWithdrawalTime = 30;

    /**
     * 检测自定关键词的相似度，换句话说，当相似度达到 customKeywordSimilarity 时，即认为触发自定义关键词，此项范围: (0, 1.0]
     */
    public static double customKeywordSimilarity = 0.8;

    /**
     * 自定义关键词及回复，value 允许是一条 mirai 消息，这意味着回复消息可以是表情或者图片等，此 Map {key - val} 可能是 {自定义关键词 - 自定义回复}，也有可能是 {群号 - 嵌套的 Map}，因此自定义关键词不允许是 bot 的所有的群号，当 {key - val} 为 {群号 - 嵌套的 Map} 形式，说明此嵌套的 Map 只在特定群内生效
     */
    public static Map<String, Object> customKeyword = new HashMap<>();
}
