package com.happysnaker.proxy;

import com.happysnaker.config.RobotConfig;
import com.happysnaker.inspect.Interceptor;
import com.happysnaker.inspect.checker;
import com.happysnaker.handler.MessageHandler;
import com.happysnaker.handler.handler;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
    public final String packageName = "com.happysnaker";
    private List<MessageHandler> handlers;
    private List<Interceptor> filters;

    /**
     * 扫描 jar 包下的所有类，并添加 MessageHandler 和
     */
    public MessageHandlerProxy(boolean inTest) {
        handlers = new ArrayList<>();
        filters = new ArrayList<>();
        TreeMap<Integer, List<MessageHandler>> map = new TreeMap<>((a, b) -> b - a);
        List<String> classes = !inTest ? getAllClasses("") : getAllClassesInTest(packageName);
        for (String aClass : classes) {
            try {
                Class c = Class.forName(aClass);
                if (c.isAnnotationPresent(handler.class)) {
                    handler annotation = (handler) c.getAnnotation(handler.class);
                    map.putIfAbsent(annotation.priority(), new ArrayList<>());
                    map.get(annotation.priority()).add((MessageHandler) c.getConstructor().newInstance());
                }
                if (c.isAnnotationPresent(checker.class)) {
                    filters.add((Interceptor) c.getConstructor().newInstance());
                }
            } catch (Exception e) {
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

    public MessageHandlerProxy() {
        this(false);
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
        for (Interceptor filter : filters) {
            if (filter.intercept(event)) {
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




    /**
     * 扫描测试环境下的所有类
     * @param p 包名
     * @return 返回包名前缀为 p 的所有类
     */
    private List<String> getAllClassesInTest(String p)  {
        p = p.replaceAll("\\.", "/");
        List<String> ans = new ArrayList<>();
        Enumeration<URL> urls = null;
        try {
            urls = getClass().getClassLoader().getResources(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String path;
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            File file = new File(url.getPath());
            ans.addAll(dfs(file, "com"));
        }
        return ans;
    }

    private List<String> dfs(File file, String prefix) {
        List<String> ans = new ArrayList<>();
//        System.out.println("file = " + file.getName());
        if (file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                ans.addAll(dfs(listFile, prefix + "." + file.getName()));
            }
        } else {
            if (file.getName().contains(".class")) {
                String c = file.getName().substring(0, file.getName().indexOf(".class"));
                c = prefix + "." + c;
                ans.add(c);
            }
        }
        return ans;
    }
}
