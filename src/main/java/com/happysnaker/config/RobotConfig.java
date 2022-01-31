package com.happysnaker.config;

import net.mamoe.mirai.utils.MiraiLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static String menu = "******** HHHH机器人 ********\n" +
            "关键字：帮助 或 help\n\n" +
            "关键字：天气\n" +
            "   示例1：天气深圳\n\n" +
            "关键字：翻译\n" +
            "   示例1：翻译i love you\n" +
            "   示例2：翻译我爱\n\n" +
            "关键字：笑话\n\n" +
            "关键字：歌词\n" +
            "   示例1：歌词后来\n\n" +
            "关键字：计算\n" +
            "   示例1：计算1+1*2/3-4\n\n" +
            "关键字：归属\n" +
            "   示例1：归属127.0.0.1\n" +
            "   示例2：归属13430108888\n\n" +
            "关键字：成语\n" +
            "   示例1：成语一生一世\n\n" +
            "关键字：五笔/拼音\n" +
            "   示例1：好字的五笔/拼音\n\n" +
            "**** 扩展功能1：GtReport ****\n\n" +
            "关键字：前线报道\n\n" +
            "关键字：会战报表\n\n" +
            "关键字：会战统计\n\n" +
            "**** 扩展功能2：ImageShare ****\n\n" +
            "关键字：鸡汤\n\n" +
            "关键字：神秘代码\n" +
            "   示例1：神秘代码\n" +
            "   示例2：神秘代码萝莉\n" +
            "   示例3：神秘代码 黑丝 萝莉\n\n" +
            "关键字：美图\n\n" +
            "**** 扩展功能3：MusicShare ****\n\n" +
            "关键字：音乐\n" +
            "   示例1：音乐克罗地亚狂想曲\n";


    /**
     * 坎公配置
     */
    public static List<Map<String, String>> gtConfig = new ArrayList<>();

    /**
     * 需要排除的群
     */
    public static List<String> include = new ArrayList<>();

    /**
     * 包括的群
     */
    public static List<String> exclude = new ArrayList<>();

}
