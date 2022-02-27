package com.happysnaker.permission;

import com.happysnaker.config.RobotConfig;

import java.util.*;

/**
 * 权限管理类，HRobot 自身拥有 超级管理员 和 管理员两种权限，超级管理员权限大于普通管理员，而同时根据具体的应用划分，HRobot 又拥有多种应用管理员，应用管理员只能管理某种特定的应用，不同之间的应用管理员之间不具备可比性，但是，超级管理员和普通管理员同时具备所有应用管理员的权限<br/><br/>
 * 在此类中，权限值小于等于 100 的管理员值越小，权限越大(NONE 除外)；而权限值大于 100 的管理员为应用管理员，不具备可比性
 * @author Happysnaker
 * @description
 * @date 2022/2/18
 * @email happysnaker@foxmail.com
 */
public class Permission {
    /**
     * 没有任何权限
     */
    public static final int NONE = -1;

    /**
     * 超级管理员，至高无上的权限，最多只允许有 1 位
     */
    public static final int SUPER_ADMINISTRATOR= 0;

    /**
     * 管理员，仅次于超级管理员，可以有无限多位
     */
    public static final int ADMINISTRATOR = 1;

    /**
     * Bot 指定的群管理员，用于管理相关群，权限次于管理员与超级管理员，目前暂未实现，视未来需求而定
     */
    public static final int GROUP_ADMINISTRATOR = 2;

    /**
     * 坎公管理员，只允许管理坎公骑冠剑相关配置，目前，此管理员可跨群管理，这是我们未来持续关注的重点
     */
    public static final int GT_ADMINISTRATOR = 101;

    public static List<String> getGtAdminList() {
        return RobotConfig.gtAdministrator;
    }

    public static List<String> getGroupAdminList() {
        return RobotConfig.groupAdministrator;
    }

    public static List<String> getBotAdminList() {
        return RobotConfig.administrator;
    }


    /**
     * 是否是否具备超级管理员权限
     * @param qq
     * @return
     */
    public static boolean hasSuperAdmin(String qq) {
        return getPermissionSet(qq).contains(SUPER_ADMINISTRATOR);
    }




    /**
     * 是否具备普通管理员权限
     * @param qq
     * @return
     */
    public static boolean hasAdmin(String qq) {
        return getPermissionSet(qq).contains(ADMINISTRATOR) || hasSuperAdmin(qq);
    }


    /**
     * 是否具备群管理员权限
     * @param qq
     * @return
     */
    public static boolean hasGroupAdmin(String qq) {
        return getPermissionSet(qq).contains(GROUP_ADMINISTRATOR) || hasAdmin(qq);
    }


    /**
     * 是否具备坎公管理员权限
     * @param qq
     * @return
     */
    public static boolean hasGtAdmin(String qq) {
        return getPermissionSet(qq).contains(SUPER_ADMINISTRATOR) || hasAdmin(qq);
    }



    /**
     * 获取对应 qq 的权限列表
     * @param qq
     * @return
     */
    public static Set<Integer> getPermissionSet(String qq) {
        Set<Integer> permissionList = new HashSet<>();
        List<String> botAdminList = getBotAdminList();
        for (int i = 0; i < botAdminList.size(); i++) {
            if (botAdminList.get(i).equals(qq)) {
                permissionList.add(i == 0 ? SUPER_ADMINISTRATOR : ADMINISTRATOR);
            }
        }

        if (getGroupAdminList().contains(qq)) {
            System.out.println("true");
            permissionList.add(GROUP_ADMINISTRATOR);
        }

        List<String> gtAdminList = getGtAdminList();
        if (gtAdminList.contains(qq)) {
            permissionList.add(GT_ADMINISTRATOR);
        }

        return permissionList;
    }

    /**
     * 比较双方的权限
     * @param qq1
     * @param qq2
     * @return 如果 qq1 的权限高于 qq2 则返回正数，如果相等返回零，如果 qq1 权限低于 qq2 返回负数
     */
    public static int compare(String qq1, String qq2) {
        Set<Integer> set1 = getPermissionSet(qq1);
        Set<Integer> set2 = getPermissionSet(qq2);
        int p1 = NONE, p2 = NONE;
        for (Integer p : set1) {
            if (p1 == NONE || p < p1) {
                p1 = p;
            }
        }
        for (Integer p : set2) {
            if (p2 == NONE || p < p2) {
                p2 = p;
            }
        }
        p1 = p1 == NONE ? Integer.MAX_VALUE : p1;
        p2 = p2 == NONE ? Integer.MAX_VALUE : p2;
        // 现在，值越小，权限越高
        return p2 - p1;
    }
}
