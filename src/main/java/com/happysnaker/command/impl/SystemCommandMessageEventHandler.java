package com.happysnaker.command.impl;

import com.happysnaker.config.RobotConfig;
import com.happysnaker.exception.CanNotParseCommandException;
import com.happysnaker.exception.InsufficientPermissionsException;
import com.happysnaker.handler.handler;
import com.happysnaker.permission.Permission;
import com.happysnaker.starter.HRobotStarter;
import com.happysnaker.utils.ConfigUtil;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;

/**
 * @author Happysnaker
 * @description
 * @date 2022/2/22
 * @email happysnaker@foxmail.com
 */
@handler(priority = 1024)
public class SystemCommandMessageEventHandler extends DefaultCommandMessageEventHandlerManager {
    public static String reloadConfigCommand = "重载配置";
    public static String saveConfigCommand = "保存配置";
    public static String showConfigStatusCommand = "查看配置状态";
    public static String showConfigLogCommand = "查看配置日志";
    public static String shutdown = "关机";
    public static String boot = "开机";
    public static String openSe = "开启涩图";
    public static String closeSe = "关闭涩图";

    public SystemCommandMessageEventHandler() {
        super.registerKeywords(saveConfigCommand);
        super.registerKeywords(showConfigStatusCommand);
        super.registerKeywords(showConfigLogCommand);
        super.registerKeywords(reloadConfigCommand);
        super.registerKeywords(shutdown).registerKeywords(boot);
        super.registerKeywords(openSe).registerKeywords(closeSe);
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
            } else if (content.equals(shutdown)) {
                return doShutDown(event);
            } else if (content.equals(boot)) {
                return doBoot(event);
            } else if (content.equals(openSe)) {
                return doOpenSe(event);
            } else if (content.equals(closeSe)) {
                return doCloseSe(event);
            }
        } catch (InsufficientPermissionsException e) {
            throw e;
        } catch (Exception e) {
            throw new CanNotParseCommandException(e);
        }
        return null;
    }

    private List<MessageChain> doOpenSe(MessageEvent event) throws InsufficientPermissionsException {
        if (!Permission.hasSuperAdmin(getSenderId(event))) {
            throw new InsufficientPermissionsException("您的权限不足，无法执行此操作");
        }
        RobotConfig.colorSwitch = true;
        return buildMessageChainAsList("已开启颜色图片");
    }

    private List<MessageChain> doCloseSe(MessageEvent event) throws InsufficientPermissionsException {
        if (!Permission.hasSuperAdmin(getSenderId(event))) {
            throw new InsufficientPermissionsException("您的权限不足，无法执行此操作");
        }
        RobotConfig.colorSwitch = false;
        return buildMessageChainAsList("已关闭颜色图片");
    }

    private List<MessageChain> doShutDown(MessageEvent event) throws InsufficientPermissionsException {
        if (!Permission.hasSuperAdmin(getSenderId(event))) {
            throw new InsufficientPermissionsException("您的权限不足，无法执行此操作");
        }
        RobotConfig.enableRobot = false;
        return buildMessageChainAsList("走啦，期待下次见面!");
    }

    private List<MessageChain> doBoot(MessageEvent event) throws InsufficientPermissionsException {
        if (!Permission.hasSuperAdmin(getSenderId(event))) {
            throw new InsufficientPermissionsException("您的权限不足，无法执行此操作");
        }
        RobotConfig.enableRobot = true;
        return buildMessageChainAsList("我又回来了！");
    }


    public List<MessageChain> doReloadConfigCommand(MessageEvent event) throws InsufficientPermissionsException, CanNotParseCommandException {
        if (!Permission.hasAdmin(getSenderId(event))) {
            throw new InsufficientPermissionsException("您的权限不足，无法执行此操作");
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
            throw new InsufficientPermissionsException("您的权限不足，无法执行此操作");
        }
        String log = checkLogStatus();
        return log == null ? buildMessageChainAsList("暂无日志") : buildMessageChainAsList(log);
    }

    public List<MessageChain> doShowConfigStatusCommand(MessageEvent event) throws InsufficientPermissionsException {
        if (!Permission.hasAdmin(getSenderId(event))) {
            throw new InsufficientPermissionsException("您的权限不足，无法执行此操作");
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
            throw new InsufficientPermissionsException("您的权限不足，无法执行此操作");
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
