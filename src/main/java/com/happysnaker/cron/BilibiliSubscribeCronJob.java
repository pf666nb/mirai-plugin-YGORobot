package com.happysnaker.cron;

import com.happysnaker.api.BilibiliApi;
import com.happysnaker.config.RobotConfig;
import com.happysnaker.entry.BilibiliDynamic;
import com.happysnaker.exception.FileUploadException;
import com.happysnaker.utils.MapGetter;
import com.happysnaker.utils.RobotUtil;
import com.happysnaker.utils.StringUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * 哔哩哔哩订阅推送
 */
public class BilibiliSubscribeCronJob extends SubscribeCronJob {
    public BilibiliSubscribeCronJob(long pushGroup, List<String> atMembers, int type, String key) {
        super(pushGroup, atMembers, type, key);
    }

    public BilibiliSubscribeCronJob(MapGetter mapGetter) {
        super(mapGetter);
    }

    @Override
    public MessageChain doCheckAndPush(Contact contact) throws IOException, FileUploadException {
        return type == 1 ? checkFanDrama(contact) : checkUpDynamic(contact);
    }

    private MessageChain checkFanDrama(Contact contact) throws IOException, FileUploadException {
        BilibiliDynamic dynamic = BilibiliApi.getLatestFanDrama(key);
        if (dynamic == null || dynamic.pubTime <= this.lastPubTime) {
            return null;
        }
        String sb = String.format("您订阅的番剧 %s 更新啦\n", dynamic.getAuthName()) +
                String.format("更新时间：%s\n", RobotUtil.formatTime(dynamic.getPubTime())) +
                String.format("跳转链接：%s\n", dynamic.getJumpUrl()) +
                String.format("更新章节：%s\n", dynamic.getDesc()) +
                String.format("分享描述：%s\n", dynamic.getPubAction());
        this.lastPubTime = dynamic.pubTime;
        if (StringUtil.isNullOrEmpty(dynamic.face) && StringUtil.isNullOrEmpty(dynamic.cover)) {
            return RobotUtil.buildMessageChain(sb);
        }
        Image face  = RobotUtil.uploadImage(contact, new URL(dynamic.face));
        Image cover = RobotUtil.uploadImage(contact, new URL(dynamic.cover));
        return RobotUtil.buildMessageChain(sb, face, cover);
    }

    private MessageChain checkUpDynamic(Contact contact) throws IOException, FileUploadException {
        BilibiliDynamic dynamic = BilibiliApi.getLatestDynamic(key);
        if (dynamic.pubTime <= this.lastPubTime) {
            return null;
        }
        String sb = String.format("您订阅的 up 主 %s %s\n", dynamic.getAuthName(), dynamic.getPubAction()) +
                String.format("更新时间：%s\n", RobotUtil.formatTime(dynamic.getPubTime())) +
                String.format("跳转链接：%s\n", dynamic.getJumpUrl()) +
                String.format("描述内容：%s\n", dynamic.getDesc());
        Image face  = RobotUtil.uploadImage(contact, new URL(dynamic.face));
        Image cover = null;
        if (!StringUtil.isNullOrEmpty(dynamic.cover)) {
            cover = RobotUtil.uploadImage(contact, new URL(dynamic.cover));
        }
        this.lastPubTime = dynamic.pubTime;
        return RobotUtil.buildMessageChain(sb, face, cover);
    }
}
