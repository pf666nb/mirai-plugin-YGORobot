package com.happysnaker.starter;

import com.alibaba.fastjson.JSONObject;
import com.happysnaker.config.RobotConfig;
import com.happysnaker.utils.NetUtil;
import net.mamoe.mirai.message.data.MessageChain;

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
    public static final String VERSION = "HRobot v2.0";
    public static final String api = "https://api.github.com/repos/happysnaker/mirai-plugin-HRobot/releases/latest";
    public static final String fileName = "plugin-2.0.mirai.jar";

    public static final String lastRelease = "plugin-2.0-SNAPSHOT.mirai.jar";

    public static void checkVersion() {
        try {
            Map<String, Object> map = NetUtil.sendAndGetResponseMap(new URL(api), "GET", null, null);
            String latestVersion = (String) map.get("name");
            if (latestVersion.equals(VERSION)) {
                RobotConfig.logger.info("当前 HRobot 插件已为最新版");
                return;
            }

            String assetsUrl = (String) map.get("assets_url");
            String res = NetUtil.sendAndGetResponseString(new URL(assetsUrl), "GET", null, null);
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
                        RobotConfig.logger.info("正在检索当前版本文件");
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
                        RobotConfig.logger.info("检索成功，开始下载新版本文件");
                        InputStream in = null;
//                        System.out.println("downLoadUrl = " + downLoadUrl);
                        try {
                            in = NetUtil.sendAndGetResponseStream(new URL(downLoadUrl), "GET", null, null, 1000 * 15);
                            File newFile = new File(plugins + "/" + downloadName);
                            newFile.createNewFile();
                            NetUtil.writeToFile(newFile, in);
                        } finally {
                            if (in != null) {
                                in.close();
                            }
                        }
                        RobotConfig.logger.info("文件下载成功！");
                        if (oldFile.delete()) {
                            RobotConfig.logger.info("旧版本插件已成功从您的电脑中移除.");
                        } else {
                            JOptionPane.showMessageDialog(null, "无法删除旧版本文件，请手动删除", "提示", JOptionPane.PLAIN_MESSAGE);
                        }
                        JOptionPane.showMessageDialog(null, "更新成功，请重启 robot！", "提示", JOptionPane.PLAIN_MESSAGE);
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
