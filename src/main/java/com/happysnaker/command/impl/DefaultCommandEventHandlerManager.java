package com.happysnaker.command.impl;

import com.happysnaker.context.Context;
import com.happysnaker.exception.CanNotParseCommandException;
import com.happysnaker.exception.InsufficientPermissionsException;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CommandMessageHandler 的集中化管理，监控 CommandMessageHandler 行为，并提供一些默认行为.
 * <p>
 *     CommandMessageHandler 需要继承此类，并重写 parseCommand 方法，子类须将命令关键词注册到此类的 keywords 集合中，要想此类更好的管理 CommandMessageHandler，<strong>子类在实现 parseCommand 时必须遵守约定抛出 {@link com.happysnaker.exception CanNotParseCommand}、{@link com.happysnaker.exception InsufficientPermissionsException} 异常</strong>。
 * </p>
 * @author Happysnaker
 * @description
 * @date 2022/2/23
 * @email happysnaker@foxmail.com
 */
public class DefaultCommandEventHandlerManager extends AbstractCommandEventHandler implements Serializable {
    protected Set<String> keywords = new HashSet<>();

    private static StringBuilder log = new StringBuilder();
    private static int successNum = 0;


    protected DefaultCommandEventHandlerManager registerKeywords(String kw) {
        keywords.add(kw);
        return this;
    }

    /**
     * 解析命令，子类必须实现
     * @param event 命令事件
     * @return 解析完成后返回的消息
     * @throws CanNotParseCommandException 无法解析时请抛出此异常
     * @throws InsufficientPermissionsException 没有足够权限时请抛出此异常
     */
    @Override
    public List<MessageChain> parseCommand(MessageEvent event) throws CanNotParseCommandException, InsufficientPermissionsException {
        throw new CanNotParseCommandException();
    }



    /**
     * 子类使用命令刷新配置时请调用此方法
     */
    protected static void flush() {
        log = new StringBuilder();
        successNum = 0;
    }

    /**
     * 当事件执行成功时调用，用以记录命令执行日志
     * @param event
     */
    @Override
    public void success(MessageEvent event) {
        log.append(getLog(event) + "\n");
        successNum++;
    }

    @Override
    public void fail(MessageEvent event, String errorMsg) {
        recordFailLog(event, errorMsg);
    }


    protected String checkLogStatus() {
        return successNum > 0 ? log.toString() : null;
    }

    public static int getSuccessNum() {
        return successNum;
    }

    /**
     * 只有当以命令前缀开头，并且命中命令关键词(开头)才会被回复
     * @param event
     * @return
     */
    @Override
    public boolean shouldHandle(MessageEvent event, Context ctx) {
        if (super.shouldHandle(event, ctx)) {
            String content = getPlantContent(event);
            if (content != null) {
                for (String keyword : keywords) {
                    if (content.startsWith(keyword)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
