package com.happysnaker.filter;

import com.happysnaker.config.RobotConfig;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * 检查 include 和 exclude 的事件
 * @author Happysnaker
 * @description
 * @date 2022/1/30
 * @email happysnaker@foxmail.com
 */
@checker
public class BaseFilter implements Filter {
    private Set<String> exclude = new HashSet<>(RobotConfig.exclude);
    private Set<String> include = new HashSet<>(RobotConfig.include);

    /**
     * 返回真则过滤该事件
     * @param event
     * @return
     */
    @Override
    public boolean doFilter(MessageEvent event) {
        if (event == null) return true;
        String groupId = String.valueOf(((GroupMessageEvent) event).getGroup().getId());
        if (include.isEmpty()) {
            return exclude.contains(groupId);
        }
        return !include.contains(groupId);
    }
}
