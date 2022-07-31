package com.happysnaker.handler.impl;

import com.happysnaker.config.RobotConfig;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;

import java.util.Map;

/**
 * 加群审批事件
 *
 * @author Happysnaker
 * @description
 * @date 2022/7/2
 * @email happysnaker@foxmail.com
 */
public class MemberJoinRequestEventHandler {

    public void handleEvent(MemberJoinRequestEvent event) {
        String gid = String.valueOf(event.getGroupId());
        if (RobotConfig.autoApproval.isEmpty()) {
            return;
        }
        for (Map<String, String> map : RobotConfig.autoApproval) {
            for (Map.Entry<String, String> it : map.entrySet()) {
                if (it.getKey().equals(gid)) {
                    if (it.getValue().isEmpty() ||
                            it.getValue().equals(event.getMessage())) {
                        event.accept();
                    } else {
                        event.reject();
                    }
                    return;
                }
            }
        }
    }
}
