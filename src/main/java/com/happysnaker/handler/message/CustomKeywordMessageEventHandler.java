package com.happysnaker.handler.message;

import com.happysnaker.config.RobotConfig;
import com.happysnaker.context.Context;
import com.happysnaker.handler.handler;
import com.happysnaker.utils.StringUtil;
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
@handler(priority = 0)
public class CustomKeywordMessageEventHandler extends GroupMessageEventHandler {
    public static final String REGEX_PREFIX = "#regex#";

    @Override
    public List<MessageChain> handleMessageEvent(MessageEvent event, Context ctx) {
        return buildMessageChainAsList(parseMiraiCode(ctx.getMessage()));
    }

    @Override
    public boolean shouldHandle(MessageEvent event, Context ctx) {
        Map<String, Object> keywordConfig = RobotConfig.customKeyword;
        if (getBotsAllGroupId().contains(getContent(event))) {
            return false;
        }
        String gid = getGroupId(event);
        // 优先群内回复，获取群内的 Map 定义
        if (keywordConfig.containsKey(gid)) {
            String reply = (String) checkSimilarWord((Map<String, Object>) keywordConfig.get(gid), getContent(event));
            if (reply != null) {
                // 传递到 getReplyMessage 中执行
                ctx.setMessage(reply);
                return true;
            }
        }
        String reply = (String) checkSimilarWord(keywordConfig, getContent(event));
        if (reply != null) {
            ctx.setMessage(reply);
            return true;
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
