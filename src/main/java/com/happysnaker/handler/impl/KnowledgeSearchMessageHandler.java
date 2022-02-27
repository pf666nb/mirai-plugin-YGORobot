package com.happysnaker.handler.impl;

import com.happysnaker.api.BaiduBaikeApi;
import com.happysnaker.exception.FileUploadException;
import com.happysnaker.handler.handler;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Happysnaker
 * @description
 * @date 2022/2/26
 * @email happysnaker@foxmail.com
 */
@handler
public class KnowledgeSearchMessageHandler extends GroupMessageHandler {
    public static final String BAIDU_BAIKE = "百度百科";

    private HashSet<String> keywords = new HashSet<>();

    public KnowledgeSearchMessageHandler() {
        keywords.add(BAIDU_BAIKE);
    }


    @Override
    protected List<MessageChain> getReplyMessage(MessageEvent event) {
        try {
            return parseBaidu(event);
        } catch (Exception e) {
            logError(event, e);
            e.printStackTrace();
            return buildMessageChainAsList("发生了错误，错误原因：" + e.getMessage());
        }
    }

    protected List<MessageChain> parseBaidu(MessageEvent event) throws MalformedURLException, FileUploadException {
        String content = getPlantContent(event).replace(BAIDU_BAIKE, "");
        Map<String, Object> map = BaiduBaikeApi.search(content);
        if (map == null || (int)map.get("code") != 200) {
            return buildMessageChainAsList("检索失败，换个关键词试试吧", getQuoteReply(event));
        }
        System.out.println("map = " + map);
        StringBuilder sb = new StringBuilder();
        sb.append("标题：");
        sb.append(map.get("title") + "\n");
        sb.append("描述：");
        sb.append(map.get("desc") + "\n");
        sb.append(map.get("content") + "\n");
        if (map.get("image") != null && map.get("image") instanceof String) {
//            System.out.println("map.get(\"image\") = " + map.get("image"));
            return buildMessageChainAsList(sb.toString(), uploadImage(event, new URL((String) map.get("image"))));
        }
        return buildMessageChainAsList(sb.toString());
    }

    @Override
    public boolean shouldHandle(MessageEvent event) {
        return startWithKeywords(event, keywords);
    }
}
