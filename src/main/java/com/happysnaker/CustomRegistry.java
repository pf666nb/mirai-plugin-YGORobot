package com.happysnaker;

import com.happysnaker.cron.RobotCronJob;
import com.happysnaker.handler.impl.MemberJoinRequestEventHandler;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.BotActiveEvent;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;

/**
 * HRobot 会自动监听 MessageEvent 事件，而其他事件需要用户手动指定监听
 * @author Happysnaker
 * @description
 * @date 2022/7/2
 * @email happysnaker@foxmail.com
 */
public class CustomRegistry {
    public static void registry(GlobalEventChannel instance) {
        instance.subscribeAlways(MemberJoinRequestEvent.class, new MemberJoinRequestEventHandler()::handleEvent);
        instance.subscribeAlways(BotOnlineEvent.class, e -> {
            try {
                RobotCronJob.runCustomerPeriodTask(e.getBot());
            } catch (Exception ex) {
                throw new RuntimeException("注册定期发送消息任务失败，请检查配置项", ex);
            }
        });
    }
}
