package com.happysnaker.starter;

import java.io.File;
import java.util.Objects;

/**
 * 修复无法发送群消息的 bug，补丁 会删除所有 bots 文件夹下的 account.secrets 文件
 * @author Happysnaker
 * @description
 * @date 2022/1/16
 * @email happysnaker@foxmail.com
 */
public class Patch {
    public static final String fileName = "account.secrets";

    private static void dfs(File file) {
        if (file.isDirectory()) {
            for (File listFile : Objects.requireNonNull(file.listFiles())) {
                dfs(listFile);
            }
        } else {
            if (file.isFile() && file.getName().contains(fileName)) {
                boolean delete = file.delete();
            }
        }
    }

    public static void  patch() {
        File file = new File("./");
        if (file.isDirectory()) {
            for (File listFile : Objects.requireNonNull(file.listFiles())) {
                if (listFile.getName().contains("bots") && listFile.isDirectory()) {
                    dfs(listFile);
                    break;
                }
            }
        }
    }
}
