package com.happysnaker.handler.impl;

import com.happysnaker.exception.InsufficientPermissionsException;
import com.happysnaker.handler.handler;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageSource;

/**
 * @author Happysnaker
 * @description
 * @date 2022/1/14
 * @email happysnaker@foxmail.com
 */
@handler(priority = 0)
public class GroupMessageHandler extends AbstractMessageHandler {

    protected String getGroupId(MessageEvent event) {
        GroupMessageEvent group = (GroupMessageEvent) event;
        return String.valueOf(group.getGroup().getId());
    }

    /**
     * 是否是群聊事件
     * @param event
     * @return
     */
    protected boolean isGroupMessageEvent(MessageEvent event) {
        return event != null && event instanceof GroupMessageEvent;
    }


    /**
     * 如果是一条 atall 消息，则去除 atall 信息
     * @see #getPlantContent
     * @param content 源消息(MIRAI编码)
     * @return 如果被 at all 所标记 将返回去除 at all 信息后的消息，否则什么也不做
     */
    protected String handlerContentIfAtAll(String content) {
        String ans;
        return (ans = handlerContentIfMatches(content, atAllRegex)) == null ? content : ans;
    }

    /**
     * 如果机器人被 at，则去除该 at 信息，该函数不会响应 at all
     * @see #getPlantContent
     * @param content (MIRAI编码)
     * @return 如果被 at 将返回去除 at 信息后的消息，否则什么也不做
     */
    protected String handlerContentIfBotBeAt(String content) {
        for (String qq : qqs) {
            if (content.indexOf(at.replace("qq", qq)) != -1) {
                return handlerContentIfMatches(content, atRegex);
            }
        }
        return content;
    }




    /**
     * 机器人是否被 at
     * @param content (MIRAI编码)
     * @return
     */
    protected boolean isAtBot(String content) {
        for (String qq : qqs) {
            if (content.indexOf(at.replace("qq", qq)) != -1) {
                return true;
            }
        }
        return false;
    }



    /**
     * 该消息是否为 atall 消息
     *
     * @param content 源消息(MIRAI编码)
     * @return
     */
    protected boolean isAtAll(String content) {
        return content == null ? false : content.indexOf(atAllRegex) != -1;
    }

    /**
     * 去除 mirai 编码消息内的 at、at all 信息，我们提供此方法只是为了可能的需要，如果您只需要获取存文本信息，你应该看看 getPlantContent 方法
     * @see #getPlantContent
     * @param content
     * @return 返回处理后的消息，如果为 null，则不会处理该消息
     */
    protected String removeAtInfo(String content) {
        content = handlerContentIfBotBeAt(content);
        content = handlerContentIfAtAll(content);
        return content;
    }

    /**
     * 获取纯文本消息，不包含任何 mirai 编码内容
     * @param event
     * @return
     *  @see AbstractMessageHandler#getPlantContent
     */
    @Override
    protected String getPlantContent(MessageEvent event) {
        return removeAtInfo(super.getPlantContent(event)).trim();
    }

    /**
     * 只有被 at 后并且是群聊消息事件才回复该消息，无视 at all
     * @param event
     * @return
     */
    @Override
    public boolean shouldHandle(MessageEvent event) {
        return isGroupMessageEvent(event) && isAtBot(getContent(event));
    }

    /**
     * 撤回一条消息，如果撤回失败请考虑是否是权限不够
     * @param source
     * @return 成功返回 true，否则返回 false
     */
    protected boolean cancelMessage(MessageSource source) {
        try {
            MessageSource.recall(source);
            return true;
        } catch (Exception e) {
            e.getMessage();
            return false;
        }
    }


    /**
     * 撤回一条消息，如果撤回失败请考虑是否是权限不够
     * @param event
     * @return 成功返回 true，否则返回 false
     * @exception InsufficientPermissionsException 如果权限不够则抛出此异常
     */
    protected boolean cancelMessage(MessageEvent event) throws InsufficientPermissionsException {
        if (((GroupMessageEvent) event).getPermission().getLevel() > 0) {
            throw new InsufficientPermissionsException("权限不足");
        }
        return cancelMessage(event.getSource());
    }

}
