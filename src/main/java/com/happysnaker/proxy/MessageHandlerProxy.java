package com.happysnaker.proxy;

import com.happysnaker.config.RobotConfig;
import com.happysnaker.filter.Filter;
import com.happysnaker.filter.checker;
import com.happysnaker.handler.MessageHandler;
import com.happysnaker.wrapper.EventWrapper;
import com.happysnaker.handler.handler;
import com.sun.tools.doclint.Checker;
import net.mamoe.mirai.event.events.BotPassiveEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.File;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Happysnaker
 * @description
 * @date 2022/1/16
 * @email happysnaker@foxmail.com
 */
public class MessageHandlerProxy implements MessageHandler {
    // 扫描所有的类
    public final String packageName = "";
    private List<MessageHandler> handlers;
    private List<Filter> filters;

    /**
     * 扫描 jar 包下的所有类，并添加 MessageHandler
     */
    public MessageHandlerProxy() {
        handlers = new ArrayList<>();
        filters = new ArrayList<>();
        TreeMap<Integer, List<MessageHandler>> map = new TreeMap<>((a, b) -> b - a);
        List<String> classes = getAllClasses(packageName);
        for (String aClass : classes) {
            try {
                Class c = Class.forName(aClass);
                if (c.isAnnotationPresent(handler.class)) {
                    handler annotation = (handler) c.getAnnotation(handler.class);
                    map.putIfAbsent(annotation.priority(), new ArrayList<>());
                    map.get(annotation.priority()).add((MessageHandler) c.getConstructor().newInstance());
                }
                if (c.isAnnotationPresent(checker.class)) {
                    filters.add((Filter) c.getConstructor().newInstance());
                }
            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("e == " + e.getMessage());
                // next
            } catch (Error e) {
//                System.out.println("e = " + e);
                // next
            }
        }

        for (Map.Entry<Integer, List<MessageHandler>> it : map.entrySet()) {
            handlers.addAll(it.getValue());
        }
        RobotConfig.logger.info("HRobot handlers and filters loading finished, total " + handlers.size() + " handlers, " + filters.size() + " filters.");
    }

    @Override
    public void handleMessageEvent(MessageEvent event) {
        if (shouldHandle(event)) {
            for (MessageHandler handler : handlers) {
                if (handler.shouldHandle(event)) {
                    handler.handleMessageEvent(event);
                    return;
                }
            }
        }
    }

    @Override
    public boolean shouldHandle(MessageEvent event) {
        for (Filter filter : filters) {
            if (filter.doFilter(event)) {
                // 如果事件被指明过滤，则不回复
                return false;
            }
        }
        return true;
    }

    /**
     * 扫描 jar 包下的类
     *
     * @return 返回包名前缀为 p 的所有类
     */
    private List<String> getAllClasses(String p) {
        p = p.replaceAll("\\.", "/");
        List<String> ans = new ArrayList<>();
        try {
            String path = MessageHandlerProxy.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            JarFile localJarFile = new JarFile(new File(path));
            Enumeration<JarEntry> entries = localJarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String innerPath = jarEntry.getName();
                if (innerPath.startsWith(p) && innerPath.contains(".class")) {
                    innerPath = innerPath.substring(0, innerPath.indexOf(".class"));
                    ans.add(innerPath.replaceAll("/", "."));
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("e.getMessage() = " + e.getMessage());
        }
        return ans;
    }
}
