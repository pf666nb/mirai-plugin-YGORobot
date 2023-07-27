package com.happysnaker.starter;

import com.happysnaker.CustomRegistry;
import com.happysnaker.Test;
import com.happysnaker.config.ConfigManager;
import com.happysnaker.config.Logger;
import com.happysnaker.config.RobotConfig;
import com.happysnaker.cron.RobotCronJob;
import com.happysnaker.proxy.MessageHandlerProxy;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
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
    public static void Start(JavaPlugin plugin) throws Exception {
        // 打印 banner
        HRobotStartPrinter.printBanner();

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
        EventChannel<Event> instance =  GlobalEventChannel.INSTANCE.parentScope(com.happysnaker.Main.INSTANCE);;
        instance.subscribeAlways(GroupMessageEvent.class, event -> {
            if (messageHandler.shouldHandle(event, null)) {
                messageHandler.handleMessageEvent(event, null);
            }
        });

        // 订阅其他事件
        CustomRegistry.registry(instance);


        // 启动后台主线程
        RobotCronJob.cron();

        // 测试
        Test.test();

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
            Logger.info("正在读取化机器人配置文件...");
            Map<?, ?> map = null;
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
                            // 可能是名字类型不符合，忽略
                        }
                    }
                }
            }
            Logger.info("机器人配置配置初始化完成");
        }
        // 文件不存在，创建文件并填写模板
        else {
            Logger.info("未检测到配置文件，正在创建配置文件模板");
            boolean newFile = file.createNewFile();
            try (FileOutputStream fileOutputStream = new FileOutputStream(file, false)) {
                String template = ConfigManager.TEMPLATE;
                fileOutputStream.write(template.getBytes(StandardCharsets.UTF_8));
                Logger.info("成功创建配置文件初始模板，请你填写配置文件，重启机器人生效");
                Thread.sleep(1500);
//                System.exit(100);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.error("配置文件模板填充错误，请手动配置，可前往 Github 官网查询");
            }
        }
    }

    public static void loadSensitiveWord() throws IOException, InterruptedException {
        File file = new File(ConfigManager.getConfigFilePath(RobotConfig.sensitiveWordPathName));

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
            boolean newFile = file.createNewFile();
            Logger.info("敏感词库文件不存在，正在创建敏感词库文件，你可添加敏感词到此文件中，路径：config/sensitiveWord.txt");

            InputStream inputStream = ConfigManager.class.getClassLoader().getResourceAsStream("config/sensitiveWord.txt");
            assert inputStream != null;
            FileOutputStream outputStream = new FileOutputStream(file);
            byte b;
            while ((b = (byte) inputStream.read()) != -1) {
                outputStream.write(b);
            }
            inputStream.close();
            outputStream.close();

            Logger.info("成功生成默认敏感词库，重启机器人生效");
            Thread.sleep(1500);
        }
    }
}

