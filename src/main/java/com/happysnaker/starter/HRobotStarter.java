package com.happysnaker.starter;

import com.happysnaker.CustomRegistry;
import com.happysnaker.api.TongZhongApi;
import com.happysnaker.config.RobotConfig;
import com.happysnaker.cron.RobotCronTask;
import com.happysnaker.proxy.MessageHandlerProxy;

import com.happysnaker.utils.*;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

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
    public static void Start(JavaPlugin plugin) throws Exception {
        // 补丁
        Patch.patch();

        // 第一步先初始化配置
        try {
            initRobotConfig(plugin);
        } catch (IllegalAccessException | IOException e) {
            e.printStackTrace();
        }

        // 加载敏感词
        loadSensitiveWord();

        // 获取消息代理
        messageHandler = new MessageHandlerProxy();

        // 订阅消息事件
        GlobalEventChannel instance = GlobalEventChannel.INSTANCE;
        instance.subscribeAlways(GroupMessageEvent.class, event -> {
            if (messageHandler.shouldHandle(event, null)) {
                messageHandler.handleMessageEvent(event, null);
            }
        });

        // 订阅其他事件
        CustomRegistry.registry(instance);


        // 启动后台主线程
        RobotCronTask.cron();

        // 测试
        test();

        // 打印 banner
        HRobotStartPrinter.printBanner();

        // 检查版本
        HRobotVersionChecker.checkVersion();
    }

    /**
     * 读取配置文件，初始化 RobotConfig
     *
     * @param plugin
     * @throws IllegalAccessException
     * @throws IOException
     */
    public synchronized static void initRobotConfig(JavaPlugin plugin) throws IllegalAccessException, IOException {
        Yaml yaml = new Yaml();

        if (plugin != null) {
            RobotConfig.logger = plugin.getLogger();
            RobotConfig.configFolder = plugin.getConfigFolder();
            RobotConfig.dataFolder = plugin.getDataFolder();
        }


        File file = new File(RobotConfig.configFolder + "/" + RobotConfig.mainConfigPathName);
        Class<RobotConfig> c = RobotConfig.class;
        Field[] fields = c.getDeclaredFields();
        // 如果配置文件存在
        if (file.exists()) {
            RobotConfig.logger.info("正在初始化机器人配置");
            Map map = null;
            try (FileInputStream in = new FileInputStream(file)) {
                map = yaml.loadAs(in, Map.class);
            }
            // 反射设置 RobotConfig
            if (map != null) {
                for (Field field : fields) {
                    if (map.containsKey(field.getName())) {
                        try {
                            if (map.get(field.getName()) != null)
                                field.set(null, map.get(field.getName()));
                        } catch (Exception ignored) {

                        }
                    }
                }
            }
            RobotConfig.logger.info("配置初始化完成");
            System.out.println("RobotConfig.customKeyword = " + RobotConfig.customKeyword);
        }
        // 文件不存在，创建文件并填写模板
        else {
            boolean newFile = file.createNewFile();
            try (FileOutputStream fileOutputStream = new FileOutputStream(file, false)) {
                String template = ConfigUtil.TEMPLATE;
                fileOutputStream.write(template.getBytes(StandardCharsets.UTF_8));
                RobotConfig.logger.info("成功创建配置文件，请您填写配置并重新启动");
            } catch (Exception e) {
                e.printStackTrace();
                RobotConfig.logger.info("配置文件填充错误，请手动配置");
            }
        }
    }

    public static void loadSensitiveWord() throws IOException {
        File file = new File(RobotConfig.configFolder + "/" + RobotConfig.sensitiveWordPathName);
        RobotConfig.sensitiveWord = new HashSet<>();
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String word = null;
                while ((word = reader.readLine()) != null) {
                    RobotConfig.sensitiveWord.add(word);
                }
            }
        }

        // 文件不存在，创建文件并填写模板
        else {
            file.createNewFile();
            RobotConfig.logger.info("创建敏感词库文件！");
        }
    }


    private static void test(Object... args) throws Exception {
        System.out.println("Pattern = " + Pattern.matches("[\\s]*", "   "));
//        throw new RuntimeException();
    }
}

