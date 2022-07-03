package com.happysnaker.handler.message;

import com.happysnaker.api.MiguApi;
import com.happysnaker.api.TongZhongApi;
import com.happysnaker.context.Context;
import com.happysnaker.handler.handler;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 音乐分享
 *
 * @author Happysnaker
 * @description
 * @date 2022/1/29
 * @email happysnaker@foxmail.com
 */
@handler(priority = 1)
public class MusicShareMessageEventHandler extends GroupMessageEventHandler {
    private Set<String> keywords = new HashSet<>();
    private final String MUSIC_KEYWORD = "音乐";
    private final String MIGU_MUSIC_KEYWORD = "咪咕";

    public MusicShareMessageEventHandler() {
        keywords.add(MUSIC_KEYWORD);
        keywords.add(MIGU_MUSIC_KEYWORD);
    }

    @Override
    public List<MessageChain> handleMessageEvent(MessageEvent event, Context ctx) {
        String content = getPlantContent(event);
        try {
            if (content.startsWith(MUSIC_KEYWORD)) {
                return music(event);
            } else if (content.startsWith(MIGU_MUSIC_KEYWORD)) {
                return miguMusic(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logError(event, e);
        }
        return buildMessageChainAsList("出错了呢，换首歌试试吧！");
    }

    protected List<MessageChain> miguMusic(MessageEvent event) throws IOException {
        String content = getPlantContent(event);
        String name = content.substring(MIGU_MUSIC_KEYWORD.length()).trim();
        return buildMessageChainAsList(MiguApi.search(name));
    }

    protected List<MessageChain> music(MessageEvent event) {
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
            logError(event, e);
            ans.add(new MessageChainBuilder().append("我炸了").build());
        }
        return ans;
    }

    @Override
    public boolean shouldHandle(MessageEvent event, Context ctx) {
        return startWithKeywords(event, keywords);
    }
}
