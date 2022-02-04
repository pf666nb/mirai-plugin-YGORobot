package com.happysnaker.starter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.happysnaker.api.TongZhongApi;
import com.happysnaker.config.RobotConfig;
import com.happysnaker.proxy.MessageHandlerProxy;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 主启动类
 *
 * @author Happysnaker
 * @description
 * @date 2022/1/19
 * @email happysnaker@foxmail.com
 */
public class HRobotStarter {
    private static MessageHandlerProxy messageHandler;

    /**
     * 启动入口
     *
     * @param plugin
     */
    public static void Start(JavaPlugin plugin) {
        // do test
        test();

        // do patch
        Patch.patch();

        // init config-data
        try {
            initRobotConfig(plugin);
        } catch (IllegalAccessException | IOException e) {
            e.printStackTrace();
        }

        // init MessageHandler
        messageHandler = new MessageHandlerProxy();

        // subscribe event
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            messageHandler.handleMessageEvent(event);
        });

        // complete start
        plugin.getLogger().info("HRobot start successfully!");
    }


    private static void initRobotConfig(JavaPlugin plugin) throws IllegalAccessException, IOException {
        RobotConfig.logger = plugin.getLogger();
        RobotConfig.configFolder = plugin.getConfigFolder();
        RobotConfig.dataFolder = plugin.getDataFolder();


        File file = new File(RobotConfig.configFolder + "/" + RobotConfig.mainConfigPathName);

        Class c = RobotConfig.class;
        Field[] fields = c.getDeclaredFields();
        if (file.exists()) {
            FileInputStream in = new FileInputStream(file);
            StringBuilder sb = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                String tmp = null;
                while ((tmp = bufferedReader.readLine()) != null) {
                    sb.append(tmp);
                }
            }
            Map<String, Object> map = JSONObject.parseObject(sb.toString(), Feature.OrderedField);
            if (map != null) {
                for (Field field : fields) {
                    if (map.containsKey(field.getName())) {
                        try {
                            field.set(null, map.get(field.getName()));
                        } catch (Exception e) {
//                        e.printStackTrace();
                            // next
                        }
                    }
//                System.out.println("field.get(null) = " + field.get(null));
                }
            }
        } else {
            file.createNewFile();
            try (FileOutputStream fileOutputStream = new FileOutputStream(file, false)) {
                String template = "{\n" +
                        "\t\"menu\":\"主菜单\",\n" +
                        "\t\"exclude\":[\"群号1\", \"群号2\"],\n" +
                        "\t\"include\":[],\n" +
                        "\t\"gtConfig\":[\n" +
                        "\t\t{\n" +
                        "\t\t\t\"groupId\":\"群号1\",\n" +
                        "\t\t\t\"gtCookie\":\"cookie1\"\n" +
                        "\t\t},\n" +
                        "        {\n" +
                        "\t\t\t\"groupId\":\"群号2\",\n" +
                        "\t\t\t\"gtCookie\":\"cookie2\"\n" +
                        "\t\t},\n" +
                        "        {\n" +
                        "\t\t\t\"groupId\":\"\",\n" +
                        "\t\t\t\"gtCookie\":\"cookie3\"\n" +
                        "\t\t}\n" +
                        "\t]\n" +
                        "}";
              fileOutputStream.write(template.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                RobotConfig.logger.info("配置文件填充错误，请手动配置");
            }
//            System.out.println("map = " + map);
        }
    }


    private static void test(Object... args) {
//        System.out.println("TongZhongApi.getSongUrl(\"Flower Dance\") = " + TongZhongApi.getSongUrl("老男孩"));
//        System.out.println("");
    }
}
