package com.happysnaker.utils;

import com.alibaba.fastjson.JSONObject;
import com.happysnaker.config.RobotConfig;
import org.yaml.snakeyaml.Yaml;

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
        try (OutputStream outputStream = new FileOutputStream(file)) {
            new Yaml().dump(getConfigMap(), new OutputStreamWriter(outputStream));
        }
    }

    /**
     * 配置类模板，在第一次创建配置文件时会写入此模板
     */
    public static String TEMPLATE = "# 成员输入 帮助 或 help 或菜单后机器人回复的消息\n" +
            "menu: '欢迎使用 HRobotv3.0，请前往 https://github.com/happysnaker/mirai-plugin-HRobot 查看相关信息'\n" +
            "\n" +
            "\n" +
            "# 指明机器人生效的群，include 和 exclude 只能配置一个而置另一个为空\n" +
            "# include 表示机器人只在这些群内生效\n" +
            "# exclude 表示机器人在除这些群外的所有群内生效\n" +
            "# 若都为空则表示对所有群生效\n" +
            "include:\n" +
            "exclude:\n" +
            "  - '群号1'\n" +
            "  - '群号2'\n" +
            "\n" +
            "\n" +
            "# 配置超级管理员与管理员\n" +
            "# 超级管理员只有一个，并在第一项给出，管理员可有多个\n" +
            "administrator:\n" +
            "  - '超级管理员 QQ 号'\n" +
            "  - '管理员QQ号'\n" +
            "  - '管理员QQ号'\n" +
            "\n" +
            "# 配置群管理员，群管理员的配置通常只在其所在群内生效\n" +
            "groupAdministrator:\n" +
            "  - '群管理员QQ号'\n" +
            "  - '群管理员QQ号'\n" +
            "\n" +
            "# 配置坎公管理员，管理坎公相关信息\n" +
            "gtAdministrator:\n" +
            "  - '坎公管理员QQ号'\n" +
            "  - '坎公管理员QQ号'\n" +
            "\n" +
            "# 指定调用网络 API 的超时时间，单位毫秒\n" +
            "timeout: 3000\n" +
            "\n" +
            "# 指定命令前缀，允许为空\n" +
            "commandPrefix: '#'\n" +
            "\n" +
            "# 聊天是否需要 @ 机器人，如果此字段为 false，则任意一条消息机器人都回回复，建议为 true\n" +
            "enableAt: true\n" +
            "\n" +
            "# 自动审批配置\n" +
            "# 当验证消息为空时则会默认同意\n" +
            "autoApproval:\n" +
            "  - 'QQ 群号': '审批验证消息'\n" +
            "  - 'QQ 群号': ''\n" +
            "\n" +
            "# 涩图撤回时间，单位为秒\n" +
            "pictureWithdrawalTime: 30\n" +
            "\n" +
            "\n" +
            "# 对机器人的回复替代\n" +
            "replyReplace:\n" +
            "  - '菲菲': '欧尼酱'        # 将回复语句中的菲菲替代为欧尼酱\n" +
            "  - '猛男': '漂亮妹妹'\n" +
            "\n" +
            "\n" +
            "# 开启敏感词检测的群\n" +
            "enableSensitiveWordDetection:\n" +
            "  - '群号'\n" +
            "  - '群号'\n" +
            "# 敏感词检测是是否需要忽略一些无意义的此，例如假设 艹尼玛 为敏感词 艹&&&&&尼玛 其中的 & 为无意义的词，建议开启\n" +
            "skipIsMeaninglessWord: true\n" +
            "# 检测跳步，例如 艹天尼玛，中间跳一了一步（”天“），开启后将对跳步自动检测\n" +
            "# 不建议设置过大，否则很容易误判，建议设置小于等于 3\n" +
            "skipStep: 2\n" +
            "# 撤回阈值，当检测到多少个敏感词后机器人会警告成员并撤回成员消息\n" +
            "withdrawalThreshold: 1\n" +
            "\n" +
            "# 坎公配置，需要配置群号对应的 cookie，其中 members 是可选项，如果不配置 members，在催刀时默认会将所有群成员作为公会成员\n" +
            "gtConfig:\n" +
            "  - groupId: '群号1'\n" +
            "    gtCookie: 'cookie1'\n" +
            "    members:\n" +
            "      - '成员1'\n" +
            "      - '成员2'\n" +
            "  - groupId: '群号2'\n" +
            "    gtCookie: 'cookie2'\n" +
            "  - groupId: ''                # 当群号为空时，代表 cookie 对所有群生效\n" +
            "    gtCookie: 'cookie3'\n" +
            "\n" +
            "\n" +
            "# 发送鸡汤时有一定概率生成毒鸡汤，在这里配置概率，这是一个 [0, 1] 之间的小数\n" +
            "duChickenSoupProbability: 0.2\n" +
            "\n" +
            "\n" +
            "# 自定义关键字配置，支持群内关键字和全局关键字，群内关键字只在群内生效，关键字会有优先匹配群内关键字\n" +
            "# 群内关键字以群号作为关键字，嵌套一层 map，如下所示\n" +
            "customKeyword:\n" +
            "  '全局关键字': '全局关键字回复'\n" +
            "  '#regex#全局正则表达式': '回复'\n" +
            "  '群号':\n" +
            "    '群内关键字: 群内关键字回复'\n" +
            "  # 群内关键词实例，如下 牛批 只在 903025723 群内生效\n" +
            "  '903025723':\n" +
            "    '牛批': '[mirai:face:317][mirai:face:317][mirai:face:317]'\n" +
            "\n" +
            "# 定时任务配置\n" +
            "# 注意由配置文件配置的定时任务不会被立即提交，因为在这一时刻机器人可能还未登录，因此你必须在机器人登录后在任意群内发送一条任意消息以触发初始化事件。在命令行中看到定时任务提交成功后才算提交成功\n" +
            "periodicTask:\n" +
            "  - groupId: '777147915'    # 定时任务发送消息的群\n" +
            "    hour: 13                # 配置时间\n" +
            "    minute: 15                # 配置时间\n" +
            "    count: 0                # 一共持续几次，0 代表无限循环\n" +
            "    content: |-                # 发布的内容，|- 是 yaml 的语法。这允许字符串换行\n" +
            "      你好呀！我很喜欢你\n" +
            "      爱你噢\n" +
            "    image: true                # 是否在消息末尾附加一张随机美图\n" +
            "# 上述任务表示在每日的 13 点 15 分 向群 777147915 发送一条消息，消息末尾包含一张美图\n" +
            "# 请注意定时任务无法中途取消，除非你强制性停止机器人";
}
