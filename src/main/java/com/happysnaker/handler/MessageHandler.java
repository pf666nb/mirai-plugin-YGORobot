package com.happysnaker.handler;

import com.happysnaker.wrapper.EventWrapper;
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
     * @return
     */
    boolean shouldHandle(MessageEvent event);

}
