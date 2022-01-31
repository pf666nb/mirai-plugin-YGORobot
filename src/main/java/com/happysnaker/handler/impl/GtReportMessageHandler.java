package com.happysnaker.handler.impl;

import com.happysnaker.config.RobotConfig;
import com.happysnaker.handler.handler;
import com.happysnaker.utils.BaseUtils;
import com.happysnaker.utils.NetUtils;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Happysnaker
 * @description
 * @date 2022/1/16
 * @email happysnaker@foxmail.com
 */
@handler(priority = 1)
public class GtReportMessageHandler extends GroupMessageHandler {
    public static final String BATTLE_REPORT = "会战报表";
    public static final String FRONTLINE_REPORTING = "前线报道";
    public static final String BATTLE_STATISTICS = "会战统计";

    private static final String BATTLE_REPORT_URL = "https://www.bigfun.cn/api/feweb?target=kan-gong-guild-report%2Fa&date=";
    public static final String FRONTLINE_REPORTING_URL = "https://www.bigfun.cn/api/feweb?target=kan-gong-guild-boss-info%2Fa";

    private Set<String> keywords;

    public static final int TOTAL_COUNT = 3;
    public static final String DATA = "data";
    public static final String USER_NAME = "user_name";
    public static final String DAMAGE_TOTAL = "damage_total";
    public static final String DAMAGE_LIST = "damage_list";
    public static final String DAMAGE = "damage";
    public static final String BOSS_NAME = "boss_name";
    public static final String IS_KILL = "is_kill";
    public static final String BOSS = "boss";
    public static final String LEVEL = "level";
    public static final String TOTAL_HP = "total_hp";
    public static final String REMAIN_HP = "remain_hp";
    public static final String NAME = "name";
    public static final String TYPE = "elemental_type_cn";


    public GtReportMessageHandler() {
        keywords = new HashSet<>(16);
        keywords.add(BATTLE_REPORT);
        keywords.add(FRONTLINE_REPORTING);
        keywords.add(BATTLE_STATISTICS);
    }

    @Override
    protected List<MessageChain> getReplyMessage(MessageEvent event) {
        List<Map<String, String>> gtConfigs = RobotConfig.gtConfig;
        String cookie = null;
        String groupId = getGroupId(event);
        for (Map<String, String> gtConfig : gtConfigs) {
            System.out.println("gtConfig = " + gtConfig);
            System.out.println("groupId = " + groupId);
            String gid = gtConfig.getOrDefault("groupId", null);
            if (gid == null || gid.isEmpty() || gid.equals(groupId)) {
                cookie = gtConfig.getOrDefault("gtCookie", null);
                break;
            }
        }
        if (cookie != null && !cookie.isEmpty()) {
            String content = getContent(event);
            try {
                if (content.contains(BATTLE_REPORT)) {
                    return List.of(doParseBattleReport(cookie));
                } else if (content.contains(FRONTLINE_REPORTING)) {
                    return List.of(doParseFrontlineReporting(cookie));
                } else if (content.contains(BATTLE_STATISTICS)) {
                    return List.of(doParseBattleStatistics(cookie));
                }
            } catch (Exception e) {
                e.printStackTrace();
                return List.of(new MessageChainBuilder().append("发生了一条意料之外的错误").build());
            }
        }
        return List.of(new MessageChainBuilder().append("该群暂未配置相关信息").build());
    }

    private String getMostDamageUser(Map<String, Object> data, String bossName) {
        long d = 0;
        String ans = "";
        for (Map.Entry<String, Object> it : data.entrySet()) {
            Map<String, Object> map = (Map<String, Object>) it.getValue();
            long dmg = (long) ((Map)map.getOrDefault(bossName, new HashMap<>())).getOrDefault("totalDamage", 0l);
            if (dmg > d) {
                d = dmg;
                ans = it.getKey();
            }
        }
        return ans;
    }

    public MessageChain doParseBattleStatistics(String cookie) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        String numberOfCuts = "count";
        String totalDamage = "totalDamage";

        Set<String> bossNames = new HashSet<>(4);
        Map<String, Object> memberData = new HashMap<>();
        while (true) {

            String date = dateFormat.format(calendar.getTime());


            Map<String, Object> msg = null;
            try {
                msg = NetUtils.sendAndGetResponseMap(new URL(BATTLE_REPORT_URL + date), "GET", getHeaders(cookie), null);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
//            System.out.println("msg = " + msg);
            if (msg == null || (int) msg.getOrDefault("code", 5) != 200 || ((String) msg.getOrDefault("message", "")).equals("服务器内部错误")) {
                break;
            }

            List<Map<String, Object>> data = (List<Map<String, Object>>) msg.getOrDefault(DATA, new ArrayList<>());
            for (Map<String, Object> dat : data) {
                String userName = (String) dat.get(USER_NAME);
                List<Map<String, Object>> damageList = (List<Map<String, Object>>) dat.get(DAMAGE_LIST);
                // 成员
                Map<String, Object> member = (Map<String, Object>) memberData.getOrDefault(userName, new HashMap<>());
                long count = 0; // 总出刀次数
                long tot = 0; // 总伤害
                for (Map<String, Object> damage : damageList) {
                    String bossName = (String) damage.get(BOSS_NAME);
                    bossNames.add(bossName);
                    long dmg = BaseUtils.intToLong(damage.get(DAMAGE));
                    // 首领统计
                    Map<String, Object> bossMap = (Map<String, Object>) member.getOrDefault(bossName, new HashMap<>());
                    bossMap.put(numberOfCuts, (long) bossMap.getOrDefault(numberOfCuts, 0l) + 1l);
                    bossMap.put(totalDamage, (long) bossMap.getOrDefault(totalDamage, 0l) + dmg);
                    member.put(bossName, bossMap);
                    count++;
                    tot += dmg;
                }
                member.put(numberOfCuts, (long) member.getOrDefault(numberOfCuts, 0l) + count);
                member.put(totalDamage, (long) member.getOrDefault(totalDamage, 0l) + tot);
                memberData.put(userName, member);
            }
            calendar.set(Calendar.HOUR_OF_DAY, -24);
        }
        StringBuffer sb = new StringBuffer();
        TreeMap<Long, Map<String, Object>> sortMap = new TreeMap<>((a, b) -> (int) (b - a));

        for (Map.Entry<String, Object> it : memberData.entrySet()) {
            Map<String, Object> map = (Map<String, Object>) it.getValue();
            map.put(USER_NAME, it.getKey());
            sortMap.put((Long) map.get(totalDamage), map);
        }

        sortMap.forEach((k, v) -> {
            Map<String, Object> map = v;
            sb.append("用户名：" + map.get(USER_NAME) + "\n");
            sb.append("  总伤害：" + map.getOrDefault(totalDamage, 0) + "\n");
            sb.append("  总出刀：" + map.getOrDefault(numberOfCuts, 0) + "\n");

            for (String bossName : bossNames) {
                Map<String, Object> bossMap = (Map<String, Object>) map.getOrDefault(bossName, new HashMap<>());
                sb.append("  " + bossName + "：\n");
                sb.append("    对怪物总伤害：" + bossMap.getOrDefault(totalDamage, 0) + "\n");
                sb.append("    对怪物总出刀：" + bossMap.getOrDefault(numberOfCuts, 0) + "\n");
            }
            sb.append("\n");
        });
        sb.append("参与会战人数：" + sortMap.size() + "\n\n");
        for (String bossName : bossNames) {
            sb.append("对" + bossName + "造成伤害最高的是：" + getMostDamageUser(memberData, bossName) + "\n\n");
        }
        sb.append("总伤害最高的是：" + sortMap.get(sortMap.firstKey()).get(USER_NAME));
//        System.out.println("sb = " + sb);
        return new MessageChainBuilder().append(sb.toString()).build();
    }

    public MessageChain doParseFrontlineReporting(String cookie) {
        Map<String, Object> msg = null;
        try {
            msg = NetUtils.sendAndGetResponseMap(new URL(FRONTLINE_REPORTING_URL), "GET", getHeaders(cookie), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuffer sb = new StringBuffer();
        sb.append("******前线报道******\n");
        Map<String, Object> data = (Map<String, Object>) msg.getOrDefault(DATA, new ArrayList<>());
        List<Map<String, Object>> bossList = (List<Map<String, Object>>) data.get(BOSS);
        for (Map<String, Object> boss : bossList) {
            sb.append(boss.get(NAME) + "：\n");
            sb.append("  等级：" + boss.get(LEVEL) + "\n");
            sb.append("  属性：" + boss.get(TYPE) + "\n");
            sb.append("  总血量：" + boss.get(TOTAL_HP) + "\n");
            sb.append("  剩余血量：" + boss.get(REMAIN_HP) + "\n");
            double percentage = BaseUtils.intToDouble(boss.get(REMAIN_HP)) / BaseUtils.intToDouble(boss.get(TOTAL_HP));
            String str = String.valueOf(percentage * 100);
            if (str.length() > 5) {
                str = str.substring(0, 5);
            }
            sb.append("  剩余血量占比：" + str + "%\n\n");
        }
//        System.out.println(sb.toString());
        return new MessageChainBuilder().append(sb.toString()).build();
    }

    public MessageChain doParseBattleReport(String cookie) {
        Map<String, Object> msg = null;
        try {
            msg = NetUtils.sendAndGetResponseMap(new URL(BATTLE_REPORT_URL), "GET", getHeaders(cookie), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int c = 0, maxDamage = 0;
        String maxDamageUser = "无";
        StringBuffer sb = new StringBuffer();
        sb.append("******今日会战报表******\n");
        List<Map<String, Object>> data = (List<Map<String, Object>>) msg.getOrDefault(DATA, new ArrayList<>());
        for (Map<String, Object> datum : data) {
            sb.append("用户名：" + datum.get(USER_NAME) + "\n");
            sb.append("总伤害：" + datum.get(DAMAGE_TOTAL) + "\n");
            List<Map<String, Object>> damageList = (List<Map<String, Object>>) datum.get(DAMAGE_LIST);
            int sum = 0;
            for (int i = 1; i <= TOTAL_COUNT; i++) {
                sb.append("  第" + i + "刀：");
                if (damageList == null || damageList.size() < i) {
                    sb.append("暂未出刀\n");
                    continue;
                }
                sb.append("\n");
                Map<String, Object> damage = damageList.get(i - 1);
                String isKill = (int) damage.get(IS_KILL) == 1 ? "是" : "否";
                sb.append("    boss：" + damage.get(BOSS_NAME) + "\n");
                sb.append("    伤害：" + damage.get(DAMAGE) + "\n");
                sb.append("    是否尾刀：" + isKill + "\n");
                sum += (int) damage.get(DAMAGE);
                c++;
            }
            if (sum > maxDamage) {
                maxDamage = sum;
                maxDamageUser = (String) datum.get(USER_NAME);
            }
            sb.append('\n');
        }
        sb.append("今日总出刀人数：" + data.size() + "\n");
        sb.append("今日出刀数：" + c + "\n");
        sb.append("今日伤害最高玩家：" + maxDamageUser + "\n");
        sb.append("最高伤害：" + maxDamage);
//        System.out.println(sb.toString());
        return new MessageChainBuilder().append(sb.toString()).build();
    }

    private Map getHeaders(String cookie) {
        Map heads = new HashMap(10);
        heads.put("Cookie", cookie);
        return heads;
    }

    /**
     * 检测到关键词回复，不需要 at 机器人
     *
     * @param event
     * @return
     */
    @Override
    public boolean shouldHandle(MessageEvent event) {
        if (isGroupMessageEvent(event)) {
            String content;
            if ((content = super.handlerContentIfBotBeAt(getContent(event))) != null) {
                for (String keyword : keywords) {
                    if (content.trim().equals(keyword)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

