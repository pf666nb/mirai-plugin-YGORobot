package com.happysnaker.config;

import com.happysnaker.utils.IOUtil;
import com.happysnaker.utils.StringUtil;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * HRobot 日志输出，提供如下几个等级
 * <ul>
 *     <li>DEBUG: 输出机器人 DEBUG 信息</li>
 *     <li>INFO: 高层次的诊断信息，只会输出重要信息</li>
 *     <li>INFO_RECORD: INFO 加强版。会记录错误日志到文件中</li>
 * </ul>
 *
 * @Author happysnaker
 * @Date 2023/2/15
 * @Email happysnaker@foxmail.com
 */
public class Logger {
    public static final int DEBUG = 0;
    public static final int INFO = 1;
    public static final int INFO_RECORD = 2;


    /**
     * 只有 debug 权限可以运行，其他权限看不到这个日志输出
     *
     * @param log
     */
    public static void debug(String log) {
        if (RobotConfig.logLevel == DEBUG) {
            RobotConfig.logger.debug(log);
        }
    }

    /**
     * 三种权限都可以看到
     *
     * @param log
     */
    public static void info(String log) {
        if (RobotConfig.logLevel >= DEBUG) {
            RobotConfig.logger.info(log);
        }
    }

    /**
     * 三种权限都可以看到
     *
     * @param log
     */
    public static void error(String log) {
        if (RobotConfig.logLevel >= DEBUG) {
            RobotConfig.logger.error(log);
        }
    }

    /**
     * 三种权限都可以看到，但只有 RECORD 权限能够记录
     *
     * @param log
     * @throws FileNotFoundException
     */
    public static void logErrorAndRecord(String log) throws FileNotFoundException {
        error(log);
        if (RobotConfig.logLevel >= INFO_RECORD) {
            synchronized (Logger.class) {
                String errorLogPath = ConfigManager.getDataFilePath(StringUtil.formatTime().split(" ")[0] + "-error.log");
                IOUtil.writeToFile(new File(errorLogPath), log);
            }
        }
    }
}
