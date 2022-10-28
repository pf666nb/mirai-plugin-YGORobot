package com.happysnaker.cron;

import com.happysnaker.config.ConfigManager;
import com.happysnaker.config.RobotConfig;
import com.happysnaker.exception.FileUploadException;
import com.happysnaker.utils.MapGetter;
import com.happysnaker.utils.StringUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.AtAll;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class SubscribeCronJob implements Runnable {
    /**
     * 推送的群号
     */
    public long pushGroup;
    /**
     * 通知需要 @ 的成员
     */
    public List<String> atMembers;
    /**
     * 订阅类型
     * <ul>
     *     <li>b 站类型：0 代表订阅 up 动态，1 代表订阅番剧更新</li>
     * </ul>
     */
    public int type;
    /**
     * 唯一标识符
     */
    public String key;
    /**
     * 上一次推送时间
     */
    public volatile long lastPubTime;


    public SubscribeCronJob(long pushGroup, List<String> atMembers, int type, String key) {
        this.pushGroup = pushGroup;
        this.atMembers = atMembers;
        this.type = type;
        this.key = key;
        this.lastPubTime = System.currentTimeMillis();
        if (atMembers == null) {
            this.atMembers = new ArrayList<>();
        }
    }

    public SubscribeCronJob(MapGetter mapGetter) {
        this(
                mapGetter.getLong("pushGroup"),
                mapGetter.getListOrWrapperSingleton("atMembers", String.class),
                mapGetter.getInt("type"),
                mapGetter.getString("key"));
    }

    @Override
    public void run() {
        if (!RobotConfig.enableRobot) {
            return;
        }
        RobotConfig.logger.info("run subscribe cron job...");
        Group contact = null;

        if (Bot.getInstances().isEmpty()) {
            RobotConfig.logger.info("未检查到任何 Bot 登录，忽略此次订阅检测");
            return;
        }

        for (Bot instance : Bot.getInstances()) {
            if ((contact = instance.getGroup(pushGroup)) != null) {
                break;
            }
        }

        if (contact == null) {
            RobotConfig.logger.info(String.format("未检查到任何 Contact，请检查推送群号 %s 是否正确", pushGroup));
            return;
        }

        try {
            MessageChain message = null;
            message = doCheckAndPush(contact);
            if (message == null)
                return;

            MessageChainBuilder builder = new MessageChainBuilder();
            for (String atMember : atMembers) {
                if (atMember.equals("-1")) {
                    builder.add(AtAll.INSTANCE);
                } else {
                    builder.add(new At(Long.parseLong(atMember)));
                }
            }
            if (!builder.isEmpty()) {
                builder.add("\n");
            }
            builder.add(message);
            contact.sendMessage(builder.build());
        } catch (Exception e) {
            ConfigManager.recordFailLog(null, new Date() + ": 推送订阅消息失败 \n" + StringUtil.getErrorInfoFromException(e));
        }
    }

    public abstract MessageChain doCheckAndPush(Contact contact) throws IOException, FileUploadException;

    @Override
    public String toString() {
        return "SubscribeCronJob{" +
                "pushGroup=" + pushGroup +
                ", atMembers=" + atMembers +
                ", type=" + type +
                ", key='" + key + '\'' +
                ", lastPubTime=" + lastPubTime +
                '}';
    }
}
