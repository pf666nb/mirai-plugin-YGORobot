package com.happysnaker.filter;

import net.mamoe.mirai.event.events.MessageEvent;

/**
 * @author Happysnaker
 * @description
 * @date 2022/1/24
 * @email happysnaker@foxmail.com
 */
public interface Filter {
    /**
     * 过滤事件，如果返回真则将该事件过滤
     * @param event
     * @return 返回真过滤，返回假通过
     */
    boolean doFilter(MessageEvent event);
}
