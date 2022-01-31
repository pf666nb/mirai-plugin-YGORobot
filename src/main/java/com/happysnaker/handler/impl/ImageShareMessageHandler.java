package com.happysnaker.handler.impl;


import com.happysnaker.api.BingApi;
import com.happysnaker.api.PixivApi;
import com.happysnaker.exception.FileUploadException;
import com.happysnaker.handler.handler;
import com.happysnaker.utils.NetUtils;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 分享一些图片，你懂的
 *
 * @author Happysnaker
 * @description
 * @date 2022/1/20
 * @email happysnaker@foxmail.com
 */
@handler(priority = 1)
public class ImageShareMessageHandler extends GroupMessageHandler {
    public final String chickenSoup = "鸡汤";
    public final String mysteriousImage = "神秘代码";
    public final String landscapeImage = "风景图";
    public final String beautifulImage = "美图";
    public final String beautifulImageUrl = PixivApi.beautifulImageUrl;
    public final String chickenSoupUrl = PixivApi.chickenSoupUrl;
    public final String pixivSearchApi = PixivApi.pixivSearchApi;

    private Set<String> keywords = new HashSet<>();

    public ImageShareMessageHandler() {
        keywords.add(chickenSoup);
        keywords.add(mysteriousImage);
        keywords.add(landscapeImage);
        keywords.add(beautifulImage);
    }

    @Override
    protected List<MessageChain> getReplyMessage(MessageEvent event) {
        String content = getPlantContent(event);
        List<MessageChain> ans = new ArrayList<>();
        try {
            if (content.contains(chickenSoup)) {
                ans.add(doParseChickenSoup(event));
            }
            if (content.contains(mysteriousImage)) {
                List<String> tags = new ArrayList<>();
                String[] strings = content.replace(mysteriousImage, "").split("\\s+");
                for (String s : strings) {
                    if (!s.isEmpty() && !s.equals(mysteriousImage)) {
                        tags.add(s);
                    }
                }
                ans.add(doParseMysteriousImage(event, tags));
            }
            if (content.contains(beautifulImage)) {
                ans.add(doParseBeautifulImage(event));
            }
            if (content.contains(landscapeImage)) {
                ans.add(doParseLandscapeImage(event));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // next
            ans.add(new MessageChainBuilder().append("意外地失去了与地球上的通信...").build());
        }
        return ans;
    }

    private MessageChain doParseLandscapeImage(MessageEvent event) throws MalformedURLException, FileUploadException {
        return new MessageChainBuilder()
                .append(uploadImage(event,
                        new URL(BingApi.getRandomImageUrl())))
                .build();
    }

    private MessageChain doParseBeautifulImage(MessageEvent event) throws MalformedURLException, FileUploadException {
        return new MessageChainBuilder()
                .append(uploadImage(event, new URL(beautifulImageUrl)))
                .build();
    }


    private MessageChain doParseMysteriousImage(MessageEvent event, List<String> tags) throws IOException, FileUploadException {
        long pid = PixivApi.getImagePid(tags);
        return new MessageChainBuilder()
                .append(uploadImage(event, new URL(pixivSearchApi.replace("IMGID", String.valueOf(pid))))).build();
    }

    private MessageChain doParseChickenSoup(MessageEvent event) throws IOException, FileUploadException {
        return new MessageChainBuilder()
                .append(NetUtils.sendAndGetResponseString(new URL(chickenSoupUrl), "GET", null, null))
                .append(uploadImage(event, new URL(beautifulImageUrl)))
                .build();
    }

    @Override
    public boolean shouldHandle(MessageEvent event) {
        if (isGroupMessageEvent(event)) {
            String content = getPlantContent(event);
            for (String keyword : keywords) {
                if (content.startsWith(keyword)) {
                    return true;
                }
            }
        }
        return false;
    }
}
