package com.happysnaker.config;

import com.happysnaker.api.PixivApi;
import com.happysnaker.exception.CanNotSendMessageException;
import com.happysnaker.utils.RobotUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.MessageChain;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 机器人后台线程，每 10 分钟执行一次，用户可向此类提交后台任务
 * <p><strong>机器人应该只有一个 {@link ScheduledExecutorService}，若想要调用定时任务，需调用此类全局的 service 进行服务</strong></p>
 *
 * @author Happysnaker
 * @description
 * @date 2022/7/2
 * @email happysnaker@foxmail.com
 */
public class RobotCronTask {
    public volatile static CopyOnWriteArrayList<Runnable> tasks = new CopyOnWriteArrayList<>();
    public volatile static Timer service = new Timer();

    public static final int PERIOD_MINUTE = 30;

    public static void cron() throws Exception {
        // 运行后台线程
        service.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                RobotConfig.logger.info("cron task running...");
                for (Runnable task : tasks) {
                    task.run();
                }
            }
        }, 0, PERIOD_MINUTE * 1000 * 60);
    }

    /**
     * 执行定期任务，此方法必须得等到机器人初始化完成后调用
     */
    public static void cronPeriodTask() throws Exception {
        // 执行定时任务
        Date now = new Date(System.currentTimeMillis());
        int year = now.getYear(), month = now.getMonth();
        for (Map<String, Object> map : RobotConfig.periodicTask) {
            int hour = (int) map.get("hour"), minute = (int) map.get("minute");
            long gid = Long.parseLong((String) map.get("groupId"));
            String content = (String) map.get("content");
            boolean image = (boolean) map.getOrDefault("image", false);
            int count = (int) map.getOrDefault("count", Integer.MAX_VALUE);
            if (count <= 0) {
                count = Integer.MAX_VALUE;
            }
            MessageChain message = RobotUtil.parseMiraiCode(content);
            List<Bot> instances = Bot.getInstances();
            for (Bot instance : instances) {
                if (instance.getGroups().contains(gid)) {
                    Contact contact = instance.getGroups().getOrFail(gid);
                    if (image) {
                        message = message.plus(RobotUtil.uploadImage(
                                contact, new URL(PixivApi.beautifulImageUrl)
                        ));
                        image = false;
                    }
                    RobotUtil.submitSendMsgTask(hour, minute, count, message, contact);
                }
            }
        }
    }

    public static void addCronTask(Runnable task) {
        tasks.add(task);
    }

    public static void rmCronTask(Runnable task) {
        tasks.remove(task);
    }
}
