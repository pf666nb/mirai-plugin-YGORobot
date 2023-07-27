package com.happysnaker.handler.impl;

import com.happysnaker.config.Logger;
import com.happysnaker.handler.MessageEventHandler;
import com.happysnaker.proxy.Context;
import com.happysnaker.utils.RobotUtil;
import com.happysnaker.utils.StringUtil;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static com.happysnaker.config.ConfigManager.recordFailLog;

/**
 * 抽象的消息处理器，实现了 {@link MessageEventHandler#handleMessageEvent(MessageEvent, Context)} 的默认逻辑，此类继承了 {@link RobotUtil} 实用类，子类可以便捷地调用实用类中的方法
 * <p>此类还提供日志记录功能，子类可便捷的记录控制台日志或选择将错误日志记录至文件以便后续查看</p>
 * @author Happysnaker
 * @description
 * @date 2022/1/14
 * @email happysnaker@foxmail.com
 */
public abstract class AbstractMessageEventHandler extends RobotUtil implements MessageEventHandler, Serializable {
    /**
     * 记录 info 日志，输出至控制台
     *
     * @param msg
     */
    public void info(String msg) {
        Logger.debug(msg);
    }

    /**
     * 记录 debug 日志，输出至控制台
     *
     * @param msg
     */
    public void debug(String msg) {
        Logger.debug(msg);
    }


    /**
     * 记录 error 日志，输出至控制台
     *
     * @param msg
     */
    public void error(String msg) {
        Logger.error(msg);
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
     * 提取纯文本消息，消息将不会包含图片、表情等任何非文字
     * @param event 消息时间
     * @return 纯文本
     */
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
