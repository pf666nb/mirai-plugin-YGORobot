package com.happysnaker;

import com.happysnaker.handler.impl.MemberJoinRequestEventHandler;
import net.mamoe.mirai.event.GlobalEventChannel;
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
        MemberJoinRequestEventHandler memberJoinRequestEventHandler = new MemberJoinRequestEventHandler();
        instance.subscribeAlways(MemberJoinRequestEvent.class, memberJoinRequestEventHandler::handleEvent);
    }
}
