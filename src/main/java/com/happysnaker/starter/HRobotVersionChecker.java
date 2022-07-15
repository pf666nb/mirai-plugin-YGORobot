package com.happysnaker.starter;

import com.alibaba.fastjson.JSONObject;
import com.happysnaker.config.RobotConfig;
import com.happysnaker.utils.IOUtil;

import javax.swing.*;
import java.io.File;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @author Happysnaker
 * @description
 * @date 2022/2/27
 * @email happysnaker@foxmail.com
 */
public class HRobotVersionChecker {
    /**
     * 当前版本信息
     */
    public static final String VERSION = "HRobot v3.2";
    /**
     * 请求 API
     */
    public static final String api = "https://api.github.com/repos/happysnaker/mirai-plugin-HRobot/releases/latest";
    /**
     * 当前插件文件名
     */
    public static final String fileName = "plugin-3.2-SNAPSHOT.mirai.jar";
    /**
     * 先与或等于当前插件的最后一个稳定版本
     */
    public static final String lastRelease = "plugin-3.2-SNAPSHOT.mirai.jar";

    public static void checkVersion() {
        try {
            Map<String, Object> map = IOUtil.sendAndGetResponseMap(new URL(api), "GET", null, null);
            String latestVersion = (String) map.get("name");
            System.out.println("latestVersion = " + latestVersion);
            if (latestVersion.equals(VERSION)) {
                RobotConfig.logger.info("当前 HRobot 插件已为最新版");
                return;
            }

            String assetsUrl = (String) map.get("assets_url");

            String res = IOUtil.sendAndGetResponseString(new URL(assetsUrl), "GET", null, null);
            List<Map<String, Object>> lists = JSONObject.parseObject(res, List.class);
            String downLoadUrl = (String) lists.get(0).get("browser_download_url");
            String downloadName = (String) lists.get(0).get("name");

            if (lastRelease.equals(downloadName)) {
                RobotConfig.logger.info("当前 HRobot 插件已为最新版");
                return;
            }

            RobotConfig.logger.info("检测到新版本: " + latestVersion + ", 请前往 " + map.get("html_url") + " 查看详情");
            if (isWindows()) {
                int n = JOptionPane.showConfirmDialog(null, "新版本 " + latestVersion + " 可用于你的系统，是否自动更新？", "更新提示", JOptionPane.YES_NO_OPTION);
                // 自动更新
                if (n == 0) {
                    try {
                        RobotConfig.logger.info("正在检索当前版本文件： " + fileName);
                        String plugins = RobotConfig.dataFolder.getParentFile().getParentFile() + "/" + "plugins";
                        File file = new File(plugins);
                        File oldFile = null;
                        for (File listFile : file.listFiles()) {
                            if (listFile.getName().equals(fileName)) {
                                oldFile = listFile;
                                break;
                            }
                        }
                        if (oldFile == null) {
                            JOptionPane.showMessageDialog(null, "更新失败，未检测到当前版本插件，请手动尝试", "提示", JOptionPane.PLAIN_MESSAGE);
                            return;
                        }
                        RobotConfig.logger.info("检索成功，开始下载新版本文件，下载链接：" + downLoadUrl);
                        InputStream in = null;
                        try {
                            in = IOUtil.sendAndGetResponseStream(new URL(downLoadUrl), "GET", null, null, 1000 * 15);
                            RobotConfig.logger.info("下载新版本完成，正在创建文件夹：" + plugins + "/" + downloadName);
                            File newFile = new File(plugins + "/" + downloadName);
                            boolean b = newFile.createNewFile();
                            if (b) {
                                RobotConfig.logger.info("文件创建成功，正在写入文件...");
                            } else {
                                RobotConfig.logger.info("创建文件失败");
                            }
                            IOUtil.writeToFile(newFile, in);
                            RobotConfig.logger.info("写入成功，请重启机器人");
                        } finally {
                            if (in != null) {
                                in.close();
                            }
                        }
                        RobotConfig.logger.info("文件下载成功！");
                        System.gc();
                        Thread.sleep(2000);
                        if (oldFile.delete()) {
                            RobotConfig.logger.info("旧版本插件已成功从您的电脑中移除.");
                        } else {
                            JOptionPane.showMessageDialog(null, "无法删除旧版本文件，请手动删除", "提示", JOptionPane.PLAIN_MESSAGE);
                        }
                        JOptionPane.showMessageDialog(null, "更新成功，请重启 robot 使版本插件生效", "提示", JOptionPane.PLAIN_MESSAGE);
                        System.exit(200);
                        return;
                    } catch (SocketTimeoutException e) {
                        JOptionPane.showMessageDialog(null, "网络连接超时，请手动尝试", "提示", JOptionPane.PLAIN_MESSAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "更新失败，请手动尝试", "提示", JOptionPane.PLAIN_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            //
        }
        RobotConfig.logger.info("已取消下载");
    }

    public static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1;
    }


}
