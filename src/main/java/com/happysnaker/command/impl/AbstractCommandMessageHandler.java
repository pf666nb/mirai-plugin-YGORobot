package com.happysnaker.command.impl;

import com.happysnaker.command.CommandHandler;
import com.happysnaker.command.CommandHandlerManager;
import com.happysnaker.exception.CanNotParseCommandException;
import com.happysnaker.exception.InsufficientPermissionsException;
import com.happysnaker.handler.impl.GroupMessageHandler;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;

/**
 * @author Happysnaker
 * @description
 * @date 2022/2/15
 * @email happysnaker@foxmail.com
 */
public abstract class AbstractCommandMessageHandler extends GroupMessageHandler implements CommandHandler, CommandHandlerManager {

    /**
     * 命令必须以该前缀开头才会被处理
     */
    protected static final String commandPrefix = "#";

    @Override
    protected List<MessageChain> getReplyMessage(MessageEvent event) {
        List<MessageChain> ans;
        try {
            ans = parseCommand(event);
        } catch (CanNotParseCommandException e) {
            fail(event, e);
            return List.of(buildMessageChain(
                    "命令解析出错，错误原因：" + e.getMessage(), getQuoteReply(event)));
        } catch (InsufficientPermissionsException e) {
            fail(event, "权限不足：");
            return List.of(buildMessageChain(
                    "对不起，您没有足够的权限，说明：" + e.getMessage(), getQuoteReply(event)));
        } catch (Exception e) {
            e.printStackTrace();
            fail(event, e);
            return List.of(buildMessageChain(
                    "异常错误，错误原因：" + e.getMessage(), getQuoteReply(event)));
        }
        if (ans != null) {
            success(event);
        }
        return ans;
    }

    /**
     * 返回去除命令前缀的纯文本消息
     *
     * @param event
     * @return
     */
    @Override
    protected String getPlantContent(MessageEvent event) {
        return super.getPlantContent(event).replace(commandPrefix, "").trim();
    }


    /**
     * 只有当消息包含命令前缀时才会返回 true，子类允许拥有自己的逻辑，但子类在执行具体逻辑前必须先询问此类的 shouldHandle，如果此类 shouldHandle 返回 false，则子类应当立马返回 false<br/><br/>
     * <pre><code>
     *     public boolean shouldHandle(MessageEvent event) {
     *         if (!super.shouldHandle(event)) {
     *             return false;
     *         }
     *         //具体的逻辑
     *     }</code></pre>
     *
     * @param event
     * @return
     */
    @Override
    public boolean shouldHandle(MessageEvent event) {
        return (super.getPlantContent(event).startsWith(commandPrefix));
    }



    /**
     * 解析命令，子类必须要实现的方法
     *
     * @see CommandHandler#parseCommand
     */
    @Override
    public abstract List<MessageChain> parseCommand(MessageEvent event) throws CanNotParseCommandException, InsufficientPermissionsException;
}
