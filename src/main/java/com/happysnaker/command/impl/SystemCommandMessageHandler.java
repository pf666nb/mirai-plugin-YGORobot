package com.happysnaker.command.impl;

import com.happysnaker.exception.CanNotParseCommandException;
import com.happysnaker.exception.InsufficientPermissionsException;
import com.happysnaker.handler.handler;
import com.happysnaker.permission.Permission;
import com.happysnaker.starter.HRobotStarter;
import com.happysnaker.utils.ConfigUtil;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Happysnaker
 * @description
 * @date 2022/2/22
 * @email happysnaker@foxmail.com
 */
@handler(priority = 1024)
public class SystemCommandMessageHandler extends DefaultCommandMessageHandlerManager {
    public static final String reloadConfigCommand = "重载配置";
    public static final String saveConfigCommand = "保存配置";
    public static final String showConfigStatusCommand = "查看配置状态";
    public static final String showConfigLogCommand = "查看配置日志";

    public SystemCommandMessageHandler() {
        keywords.add(saveConfigCommand);
        keywords.add(showConfigStatusCommand);
        keywords.add(showConfigLogCommand);
        keywords.add(reloadConfigCommand);
    }


    @Override
    public List<MessageChain> parseCommand(MessageEvent event) throws CanNotParseCommandException, InsufficientPermissionsException {
        String content = getPlantContent(event);
        try {
            if (content.equals(saveConfigCommand)) {
                return doSaveConfig(event);
            } else if (content.equals(showConfigStatusCommand)) {
                return doShowConfigStatusCommand(event);
            } else if (content.equals(showConfigLogCommand)) {
                return doShowConfigLogCommand(event);
            } else if (content.equals(reloadConfigCommand)) {
                return doReloadConfigCommand(event);
            }
        } catch (InsufficientPermissionsException e) {
            throw e;
        } catch (Exception e) {
            throw new CanNotParseCommandException(e);
        }
        return null;
    }


    public List<MessageChain> doReloadConfigCommand(MessageEvent event) throws InsufficientPermissionsException, CanNotParseCommandException {
        if (!Permission.hasAdmin(getSenderId(event))) {
            throw new InsufficientPermissionsException();
        }
        try {
            HRobotStarter.initRobotConfig(null);
            return buildMessageChainAsList("重载成功!");
        } catch (Exception e) {
            throw new CanNotParseCommandException(e);
        }
    }

    public List<MessageChain> doShowConfigLogCommand(MessageEvent event) throws InsufficientPermissionsException {
        if (!Permission.hasAdmin(getSenderId(event))) {
            throw new InsufficientPermissionsException();
        }
        String log = checkLogStatus();
        return log == null ? buildMessageChainAsList("暂无日志") : buildMessageChainAsList(log);
    }

    public List<MessageChain> doShowConfigStatusCommand(MessageEvent event) throws InsufficientPermissionsException {
        if (!Permission.hasAdmin(getSenderId(event))) {
            throw new InsufficientPermissionsException();
        }
        int num = getSuccessNum();
        return num == 0 ? buildMessageChainAsList("The command tree is clean now") : buildMessageChainAsList("There are " + num + " command successfully be done，it may be dirty now, try to check the log!");
    }

    /**
     * 保存配置，命令格式 "#保存配置"，命令需要提供 bot 管理员权限
     *
     * @param event
     * @return
     */
    public List<MessageChain> doSaveConfig(MessageEvent event) throws InsufficientPermissionsException, CanNotParseCommandException {
        if (!Permission.hasAdmin(getSenderId(event))) {
            throw new InsufficientPermissionsException();
        }
        try {
            ConfigUtil.writeConfig();
            flush();
        } catch (Exception e) {
            throw new CanNotParseCommandException(e);
        }
        return buildMessageChainAsList("配置保存成功！");
    }
}
