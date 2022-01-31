package com.happysnaker.wrapper;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

/**
 * 由于 Java 与 kt 之间的类不能向子类强转，所以不能设置 MessageEvent 接口来代替所有消息事件，因此提供此类包装所有事件
 * @author Happysnaker
 * @description
 * @date 2022/1/15
 * @email happysnaker@foxmail.com
 */
public class EventWrapper {
    /**
     * 该字段用以判断是否有任何一个不为 null 的消息
     */
    private MessageEvent anyEventNotNull;

    /**
     * 当收到一个群聊消息后触发该事件
     */
    private GroupMessageEvent groupMessageEvent;

    public EventWrapper() {
    }

    public GroupMessageEvent getGroupMessageEvent() {
        return groupMessageEvent;
    }

    public EventWrapper setGroupMessageEvent(GroupMessageEvent groupMessageEvent) {
        this.groupMessageEvent = groupMessageEvent;
        this.anyEventNotNull = groupMessageEvent;
        return this;
    }

    public MessageEvent getAnyEventNotNull() {
        return anyEventNotNull;
    }
}