package com.happysnaker.inspect;

import net.mamoe.mirai.event.events.MessageEvent;

/**
 * @author Happysnaker
 * @description
 * @date 2022/1/24
 * @email happysnaker@foxmail.com
 */
public interface Interceptor {
    /**
     * 拦截事件，如果返回真则将该事件拦截
     * @param event
     * @return 返回真拦截，返回假通过
     */
    boolean intercept(MessageEvent event);
}
