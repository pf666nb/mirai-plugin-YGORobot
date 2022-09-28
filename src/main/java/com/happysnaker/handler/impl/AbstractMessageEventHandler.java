package com.happysnaker.handler.impl;

import com.happysnaker.config.RobotConfig;
import com.happysnaker.cron.RobotCronTask;
import com.happysnaker.context.Context;
import com.happysnaker.handler.MessageEventHandler;
import com.happysnaker.utils.*;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.MiraiLogger;

import java.io.Serializable;
import java.util.*;

/**
 * 抽象的消息处理器，实现了 {@link MessageEventHandler#handleMessageEvent(MessageEvent, Context)} 的默认逻辑，抽象方法即可，此类继承了 {@link RobotUtil} 实用类，子类可以便捷地调用实用类中的方法
 * <p>此类还提供日志记录功能，子类可便捷的记录控制台日志或选择将错误日志记录至文件以便后续查看</p>
 * @author Happysnaker
 * @description
 * @date 2022/1/14
 * @email happysnaker@foxmail.com
 */
public abstract class AbstractMessageEventHandler extends RobotUtil implements MessageEventHandler, Serializable {


    /**
     *     当前机器人的 qq 号，注意可能有多个 qq（多个机器人）
     */
    public List<String> qqList = null;



    public MiraiLogger logger = RobotConfig.logger;


    /**
     * 记录 info 日志，输出至控制台
     *
     * @param msg
     */
    public void info(String msg) {
        logger.info(msg);
    }

    /**
     * 记录 debug 日志，输出至控制台
     *
     * @param msg
     */
    public void debug(String msg) {
        logger.debug(msg);
    }


    /**
     * 记录 error 日志，输出至控制台
     *
     * @param msg
     */
    public void error(String msg) {
        logger.error(msg);
    }


    /**
     * 记录 error 日志，输出至错误文件
     *
     * @param msg
     */
    public void logError(MessageEvent event, String msg) {
        recordFailLog(event, msg);
    }


    /**
     * 记录 error 日志，输出至错误文件
     */
    public void logError(MessageEvent event, Throwable e) {
        recordFailLog(event, StringUtil.getErrorInfoFromException(e));
    }



    /**
     * 读取运行时机器人的 QQ，并初始化 qqs，此方法需动态调用，因为一开始用户可能未登录
     */
    public void initBotQQ() {
        info("init bot...");
        List<Bot> bots = Bot.getInstances();
        List<String> qqs = new ArrayList<>();
        for (Bot bot : bots) {
            qqs.add(String.valueOf(bot.getId()));
        }
        this.qqList = qqs;

        try {
            info("正在提交定期任务...");
            RobotCronTask.runCustomerPeriodTask();
            info("任务提交成功！");
        } catch (Exception e) {
            e.printStackTrace();
            error("定期任务提交失败，请检查配置是否成功，如有相关问题可前往 提出 ISSUE");
        }
    }

    public String getPlantContent(MessageEvent event) {
        return getOnlyPlainContent(event);
    }

    /**
     * 检查事件消息是否以关键字开头
     *
     * @param event
     * @param keywords
     * @return
     */
    public  boolean startWithKeywords(MessageEvent event, Collection<String> keywords) {
        String content = getPlantContent(event);
        if (content != null) {
            for (String keyword : keywords) {
                if (content.startsWith(keyword)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将要回复的消息，子类需要实现
     */
    public abstract List<MessageChain> handleMessageEvent(MessageEvent event, Context ctx);
}
