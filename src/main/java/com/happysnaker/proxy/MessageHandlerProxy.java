package com.happysnaker.proxy;

import com.happysnaker.config.RobotConfig;
import com.happysnaker.context.Context;
import com.happysnaker.intercept.Interceptor;
import com.happysnaker.intercept.intercept;
import com.happysnaker.handler.MessageEventHandler;
import com.happysnaker.handler.handler;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author Happysnaker
 * @description
 * @date 2022/1/16
 * @email happysnaker@foxmail.com
 */
public class MessageHandlerProxy implements MessageEventHandler {
    // 扫描所有的类
    public final String packageName = "com.happysnaker";
    private List<MessageEventHandler> handlers = new ArrayList<>();
    private List<Interceptor> interceptors = new ArrayList<>();

    /**
     * 扫描 jar 包下的所有类，并添加 MessageHandler 和
     */
    public MessageHandlerProxy() throws Exception {
        Reflections reflections = new Reflections("com.happysnaker");
        TreeMap<Integer, List<MessageEventHandler>> map = new TreeMap<>((a, b) -> b - a);
        Set<Class<?>> handlerCs = reflections.getTypesAnnotatedWith(handler.class);
        Set<Class<?>> inspectCs = reflections.getTypesAnnotatedWith(intercept.class);
        for (Class<?> c : handlerCs) {
            if (Modifier.isAbstract(c.getModifiers())) {
                continue;
            }
            try {
                handler annotation = c.getAnnotation(handler.class);
                map.putIfAbsent(annotation.priority(), new ArrayList<>());
                MessageEventHandler h = (MessageEventHandler) c.getConstructor().newInstance();
                map.get(annotation.priority()).add(h);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Class<?> c : inspectCs) {
            try {
                Interceptor it = (Interceptor) c.getConstructor().newInstance();
                interceptors.add(it);
            } catch (Exception e) {
                e.printStackTrace();
                throw  e;
            }
        }

        for (Map.Entry<Integer, List<MessageEventHandler>> it : map.entrySet()) {
            handlers.addAll(it.getValue());
        }
        RobotConfig.logger.info("HRobot handlers and filters loading finished, total " + handlers.size() + " handlers, " + interceptors.size() + " inspectors.");
    }


    @Override
    public List<MessageChain> handleMessageEvent(MessageEvent event, Context ctx) {
        if (ctx == null) {
            ctx = new Context(handlers, interceptors);
        }
        ctx. execute(event);
        return null;
    }

    @Override
    public boolean shouldHandle(MessageEvent event, Context ctx) {
        for (Interceptor filter : interceptors) {
            if (filter.interceptBefore(event)) {
                // 如果事件被指明过滤，则不回复
                return false;
            }
        }
        return true;
    }
}
