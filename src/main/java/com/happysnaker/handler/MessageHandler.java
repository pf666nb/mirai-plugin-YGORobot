package com.happysnaker.handler;

import net.mamoe.mirai.event.events.MessageEvent;

/**
 * @author Happysnaker
 * @description
 * @date 2022/1/15
 * @email happysnaker@foxmail.com
 */
public interface MessageHandler {
    /**
     * 处理一个新的消息事件
     *
     * @param event
     */
     void handleMessageEvent(MessageEvent event);


    /**
     * 是否应该处理事件，子类应该扩展它
     *
     * @param event
     * @return 如果需要处理，则返回 true；如果不需要处理，则返回 false
     */
    boolean shouldHandle(MessageEvent event);

}
