package com.happysnaker.starter;

import com.alibaba.fastjson.JSONObject;
import com.happysnaker.config.Logger;
import com.happysnaker.config.RobotConfig;
import com.happysnaker.utils.IOUtil;

import javax.swing.*;
import java.io.File;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    public static final String VERSION = String.format("HRobot v%s", RobotConfig.CURRENT_VERSION);
    /**
     * 请求 API
     */
    public static final String api = "https://api.github.com/repos/happysnaker/mirai-plugin-HRobot/releases/latest";
    /**
     * 当前插件文件名
     */
    public static final String fileName = String.format("plugin-%s-SNAPSHOT.mirai.jar", RobotConfig.CURRENT_VERSION);

    public static int compareVersion(String v1, String v2) {
        String n1 = v1.substring(0, v1.indexOf('-') == -1 ? v1.length() : v1.indexOf('-')).replace("HRobot v", "");
        String n2 = v2.substring(0, v2.indexOf('-') == -1 ? v2.length() : v2.indexOf('-')).replace("HRobot v", "");
        String[] split1 = n1.split("\\.");
        String[] split2 = n2.split("\\.");
        int ret = 0;
        for (int i = 0; i < Math.max(split1.length, split2.length); i++) {
            int num1 = i < split1.length ? Integer.parseInt(split1[i]) : 0;
            int num2 = i < split2.length ? Integer.parseInt(split2[i]) : 0;
            if (num1 > num2) {
                ret = 1;
                break;
            } else if (num1 < num2) {
                ret = -1;
                break;
            }
        }
        if (ret != 0 || (!v1.contains("beta") && !v2.contains("beta"))) {
            return ret;
        }
        if (v1.contains("beta") && v2.contains("beta")) {
            if (v1.endsWith("beta")) {
                v1 += ".0";
            }
            if (v2.endsWith("beta")) {
                v2 += ".0";
            }
            return Integer.parseInt(v1.split("beta\\.")[1]) - Integer.parseInt(v2.split("beta\\.")[1]);
        }
        return v1.contains("beta") ? -1 : 1;
    }

    public static void checkVersion() {
        try {
            Map<String, Object> map = IOUtil.sendAndGetResponseMap(new URL(api), "GET", null, null);
            String latestVersion = (String) map.get("name");

            Logger.info("Github 最新发行版本 " + latestVersion);
            Logger.info("当前版本 " + VERSION);
            if (compareVersion(VERSION, latestVersion) >= 0) {
                Logger.debug("当前 HRobot 插件已为最新版");
                return;
            }

            String assetsUrl = (String) map.get("assets_url");

            String res = IOUtil.sendAndGetResponseString(new URL(assetsUrl), "GET", null, null);
            
            List<Map<String, Object>> lists = JSONObject.parseObject(res, List.class);
            String downLoadUrl = (String) lists.get(0).get("browser_download_url");
            String downloadName = (String) lists.get(0).get("name");

            Logger.info("检测到新版本: " + latestVersion + ", 请前往 " + map.get("html_url") + 
                    " 查看详情, 如您使用的已经是最新版插件请忽略此消息" );

            // 只有 windows 才自动更新，linux 仅提示
            if (isWindows()) {
                int n = JOptionPane.showConfirmDialog(null, "新版本 " + latestVersion + " 可用于你的系统，是否自动更新？", "更新提示", JOptionPane.YES_NO_OPTION);
                // 自动更新
                if (n == 0) {
                    try {
                        Logger.info("正在检索当前版本文件： " + fileName);
                        String plugins = RobotConfig.dataFolder.getParentFile().getParentFile() + "/" + "plugins";
                        File file = new File(plugins);
                        File oldFile = null;
                        for (File listFile : Objects.requireNonNull(file.listFiles())) {
                            if (listFile.getName().equals(fileName)) {
                                oldFile = listFile;
                                break;
                            }
                        }
                        if (oldFile == null) {
                            JOptionPane.showMessageDialog(null, "更新失败，无法卸载当前版本插件，请手动尝试", "提示", JOptionPane.PLAIN_MESSAGE);
                            return;
                        }
                        Logger.info("检索成功，开始下载新版本文件，下载链接：" + downLoadUrl);
                        try (InputStream in = IOUtil.sendAndGetResponseStream(new URL(downLoadUrl), "GET", null, null, 1000 * 15)) {
                            Logger.info("下载新版本完成，正在安装：" + plugins + "/" + downloadName);
                            File newFile = new File(plugins + "/" + downloadName);
                            boolean b = newFile.createNewFile();
                            IOUtil.writeToFile(newFile, in);
                            Logger.info("新版本安装成功，正在卸载旧版本插件");
                        }
                        System.gc();
                        Thread.sleep(2000);
                        if (oldFile.delete()) {
                            Logger.info("旧版本插件已成功从您的电脑中移除.");
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
            e.printStackTrace();
        }
        Logger.info("已取消下载");
    }

    public static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS");
    }
}
