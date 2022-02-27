package com.happysnaker.handler.impl;

import com.happysnaker.config.RobotConfig;
import com.happysnaker.exception.CanNotSendMessageException;
import com.happysnaker.handler.handler;
import com.happysnaker.utils.StringUtil;
import jdk.internal.joptsimple.util.RegexMatcher;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 自定义关键词检测
 * @author Happysnaker
 * @description
 * @date 2022/2/22
 * @email happysnaker@foxmail.com
 */
@handler(priority = -1) // 优先度最低
public class CustomKeywordMessageHandler extends GroupMessageHandler {
    public static final String REGEX_PREFIX = "#regex#";



    @Override
    protected List<MessageChain> getReplyMessage(MessageEvent event) {
        return null;
    }

    @Override
    public boolean shouldHandle(MessageEvent event) {
        Map<String, Object> keywordConfig = RobotConfig.customKeyword;
        if (getBotsAllGroupId().contains(getContent(event))) {
            return false;
        }
        String gid = getGroupId(event);
        if (keywordConfig.containsKey(gid)) {
            String reply = (String) checkSimilarWord((Map<String, Object>) keywordConfig.get(gid), getContent(event));
            if (reply != null) {
                try {
                    sendMsg(parseMiraiCode(reply), event);
                } catch (CanNotSendMessageException e) {
                    e.printStackTrace();
                    logError(event, e);
                } finally {
                    return true;
                }
            }
        }
        String reply = (String) checkSimilarWord(keywordConfig, getContent(event));
        if (reply != null) {
            try {
                sendMsg(parseMiraiCode(reply), event);
//                return true;
            } catch (CanNotSendMessageException e) {
                e.printStackTrace();
                logError(event, e);
            } finally {
                return true;
            }
        }
        return false;
    }

    private Object checkSimilarWord(Map<String, Object> map, String word) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            // 如果是正则表达式的话，则用正则表达式模式匹配
            if (key.startsWith(REGEX_PREFIX)) {
                key = key.replace(REGEX_PREFIX, "");
                if (Pattern.matches(key, word)) {
                    return val;
                }
                continue;
            }
            int len = key.length();
            double maximumEditDistance = (1 - RobotConfig.customKeywordSimilarity) * len;
            if (StringUtil.getEditDistance(key, word) <= maximumEditDistance) {
                return val;
            }
        }
        return null;
    }
}
