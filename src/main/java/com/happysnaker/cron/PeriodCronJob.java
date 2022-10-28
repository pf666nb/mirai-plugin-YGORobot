package com.happysnaker.cron;

import com.happysnaker.api.PixivApi;
import com.happysnaker.config.ConfigManager;
import com.happysnaker.config.RobotConfig;
import com.happysnaker.utils.RobotUtil;
import com.happysnaker.utils.StringUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import org.quartz.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PeriodCronJob implements Job {
    public static final String KEY = "KEY_PeriodCronJobData";

    public static class PeriodCronJobData {
        public volatile AtomicInteger count;
        public volatile List<MessageChain> messages;
        public volatile List<String> rawMessages;
        public volatile Contact contact;
        @Deprecated
        public volatile boolean plusImage;

        @Deprecated
        public PeriodCronJobData(int count, boolean plusImage, List<MessageChain> messages, Contact contact) {
            this.count = new AtomicInteger(count);
            this.messages = messages;
            this.contact = contact;
            this.plusImage = plusImage;
        }

        public PeriodCronJobData(int count, List<String> messages, Contact contact) {
            this.count = new AtomicInteger(count);
            this.rawMessages = messages;
            this.contact = contact;
        }

        @Deprecated
        public static JobDataMap getJobDataMap(int count, boolean plusImage, List<MessageChain> messages, Contact contact) {
            PeriodCronJobData jobData = new PeriodCronJobData(count, plusImage, messages, contact);
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(KEY, jobData);
            return jobDataMap;
        }

        public static JobDataMap getJobDataMap(int count, List<String> messages, Contact contact) {
            PeriodCronJobData jobData = new PeriodCronJobData(count, messages, contact);
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(KEY, jobData);
            return jobDataMap;
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if (!RobotConfig.enableRobot) {
            return;
        }
        PeriodCronJobData jobData = (PeriodCronJobData) context.getJobDetail().getJobDataMap().get(KEY);
        RobotConfig.logger.info("执行用户配置定期发送消息任务：" + context.getJobDetail().getKey());
        RobotConfig.logger.info("当前时间：" + new Date() + "，下一次执行时间：" + context.getNextFireTime());
        if (jobData.count.get() <= 0) {
            RobotConfig.logger.info(String.format("任务 %s 执行次数已达阈值，取消任务", context.getJobDetail().getKey()));
            try {
                context.getScheduler().deleteJob(context.getJobDetail().getKey());
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }

        try {
            if (jobData.messages == null && jobData.rawMessages != null) {
                jobData.messages = new ArrayList<>();
                for (String rawMessage : jobData.rawMessages) {
                    jobData.messages.add(RobotUtil.parseMiraiCode(rawMessage, jobData.contact));
                }
            }

            Message message = jobData.messages.get((int) (jobData.messages.size() * Math.random()));
            if (jobData.plusImage) {
                message = message.plus(RobotUtil.uploadImage(jobData.contact, new URL(PixivApi.beautifulImageUrl)));
            }
            jobData.contact.sendMessage(message);
            RobotConfig.logger.info(String.format("任务 %s 执行完毕，剩余次数：%d", context.getJobDetail().getKey(), jobData.count.decrementAndGet()));
        } catch (Exception e) {
            e.printStackTrace();
            ConfigManager.recordFailLog(null, StringUtil.getErrorInfoFromException(e));
        }
    }
}
