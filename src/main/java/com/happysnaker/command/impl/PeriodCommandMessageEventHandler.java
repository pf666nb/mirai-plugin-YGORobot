package com.happysnaker.command.impl;

import com.happysnaker.api.PixivApi;
import com.happysnaker.config.RobotConfig;
import com.happysnaker.exception.CanNotParseCommandException;
import com.happysnaker.exception.InsufficientPermissionsException;
import com.happysnaker.handler.handler;
import com.happysnaker.permission.Permission;
import com.happysnaker.utils.PairUtil;
import com.happysnaker.utils.RobotUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

import java.net.URL;
import java.util.List;

/**
 * @author Happysnaker
 * @description
 * @date 2022/7/3
 * @email happysnaker@foxmail.com
 */
@handler(priority = 1024)
public class PeriodCommandMessageEventHandler extends DefaultCommandMessageEventHandlerManager {

    public static final String ADD_PERIOD_TASK = "设置定时任务";

    public PeriodCommandMessageEventHandler() {
        registerKeywords(ADD_PERIOD_TASK);
    }

    @Override
    public List<MessageChain> parseCommand(MessageEvent event) throws CanNotParseCommandException, InsufficientPermissionsException {
        if (!Permission.hasAdmin(getSenderId(event))) {
            throw new InsufficientPermissionsException("权限不足");
        }
        try {
            // 不能获取纯文本
            String content = getContent(event).replace(RobotConfig.commandPrefix + ADD_PERIOD_TASK, "").trim();
            PairUtil<String, String> keyVal = getKeyVal(content);
            String[] split = keyVal.getKey().split("-");
            int hour = Integer.parseInt(split[0]);
            int minute = Integer.parseInt(split[1]);
            int count = Integer.parseInt(split[2]);
            int image = split.length == 4 ? Integer.parseInt(split[3]) : 0;
            MessageChain message = parseMiraiCode(keyVal.getValue());
            Contact contact = event.getSubject();
            if (image == 1) {
                message = message.plus(RobotUtil.uploadImage(
                        contact, new URL(PixivApi.beautifulImageUrl)
                ));
            }
            RobotUtil.submitSendMsgTask(hour, minute, count, message, contact);
            return buildMessageChainAsList("任务提交成功！");
        } catch (Exception e) {
            throw new CanNotParseCommandException(e.getMessage());
        }
    }

    /**
     * 提取时间及回复
     *
     * @param content
     * @return
     * @throws CanNotParseCommandException
     */
    private PairUtil<String, String> getKeyVal(String content) throws CanNotParseCommandException {
        int l = content.indexOf('{');
        int r = content.indexOf('}');
        if (l == -1 || r == -1) {
            throw new CanNotParseCommandException("未检测到关键字，请以{}包裹关键字，日期格式为 hour-minute-count-image");
        }
        String keyword = content.substring(l + 1, r);
        String val = content.replace("{" + keyword + "}", "");
        return new PairUtil<>(keyword.trim(), val.trim());
    }
}
