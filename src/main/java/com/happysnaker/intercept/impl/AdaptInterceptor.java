package com.happysnaker.intercept.impl;

import com.happysnaker.intercept.Interceptor;
import com.happysnaker.utils.RobotUtil;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;

/**
 * 适配器拦截器，实现方法但不包含任何逻辑，子类可选择实现
 * @author happysnaker
 * @date 2022/10/27
 * @email happysnaker@foxmail.com
 */
public class AdaptInterceptor extends RobotUtil implements Interceptor {
    @Override
    public boolean interceptBefore(MessageEvent event) {
        return false;
    }

    @Override
    public List<MessageChain> interceptAfter(MessageEvent event, List<MessageChain> mc) {
        return mc;
    }
}
