package com.happysnaker.handler.impl;

import com.happysnaker.api.TongZhongApi;
import com.happysnaker.handler.handler;
import com.happysnaker.proxy.Context;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.MusicShare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 音乐分享，基于铜钟音乐
 *
 * @author Happysnaker
 * @description
 * @date 2022/1/29
 * @email happysnaker@foxmail.com
 */
@handler(priority = 1)
public class MusicShareMessageEventHandler extends GroupMessageEventHandler {
    private final Set<String> keywords = new HashSet<>();
    private final String MUSIC_KEYWORD = "音乐";


    public MusicShareMessageEventHandler() {
        keywords.add(MUSIC_KEYWORD);
    }

    @Override
    public List<MessageChain> handleMessageEvent(MessageEvent event, Context ctx) {
        String content = getPlantContent(event);
        try {
            if (content.startsWith(MUSIC_KEYWORD)) {
                return music(event);
            }
        } catch (Exception e) {
            logError(event, e);
            return buildMessageChainAsSingletonList("出错了呢，换首歌试试吧！\n" + e.getMessage());
        }
        return null;
    }

    /**
     * 获取音乐
     * @param event
     * @return
     * @throws IOException
     */
    protected List<MessageChain> music(MessageEvent event) throws IOException {
        List<MessageChain> ans = new ArrayList<>();
        String content = getPlantContent(event);
        String name = content.substring(MUSIC_KEYWORD.length()).trim();
        try {
            MusicShare music = TongZhongApi.getSongUrl(name);
            if (music != null) {
                ans.add(new MessageChainBuilder().append(music).build());
            } else {
                ans.add(buildMessageChain("查无此歌"));
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
        return ans;
    }


    @Override
    public boolean shouldHandle(MessageEvent event, Context ctx) {
        return startWithKeywords(event, keywords);
    }
}
