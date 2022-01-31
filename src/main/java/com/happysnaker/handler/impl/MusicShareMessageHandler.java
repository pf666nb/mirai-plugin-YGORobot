package com.happysnaker.handler.impl;

import com.happysnaker.api.TongZhongApi;
import com.happysnaker.handler.handler;
import com.happysnaker.utils.NetUtils;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import sun.nio.ch.Net;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Happysnaker
 * @description
 * @date 2022/1/29
 * @email happysnaker@foxmail.com
 */
@handler(priority = 1)
public class MusicShareMessageHandler extends GroupMessageHandler {
    private String keyword = "音乐";

    @Override
    protected List<MessageChain> getReplyMessage(MessageEvent event) {
        List<MessageChain> ans = new ArrayList<>();
        String content = getPlantContent(event);
        String name = content.substring(keyword.length()).trim();
        try {
            MusicShare music = TongZhongApi.getSongUrl(name);
            if (music != null) {
                ans.add(new  MessageChainBuilder().append(music).build());
            }
        } catch (Exception e) {
            error(e.getMessage());
            ans.add(new  MessageChainBuilder().append("我炸了").build());
        }
        return ans;
    }

    @Override
    public boolean shouldHandle(MessageEvent event) {
        if (isGroupMessageEvent(event)) {
            return getPlantContent(event).startsWith(keyword);
        }
        return false;
    }
}
