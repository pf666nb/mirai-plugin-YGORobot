package com.happysnaker.handler.impl;

import com.happysnaker.config.RobotConfig;
import com.happysnaker.context.Context;
import com.happysnaker.handler.handler;
import com.happysnaker.permission.Permission;
import com.happysnaker.utils.OfUtil;
import com.happysnaker.utils.StringUtil;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.*;

/**
 * @author Happysnaker
 * @description
 * @date 2022/7/9
 * @email happysnaker@foxmail.com
 */
@handler
public class RussianRouletteMessageEventHandler extends GroupMessageEventHandler {
    public final String reload = "装弹";
    public final String shot = "开枪";
    public final String stop = "卸下弹夹";

    class Helper {
        // 总弹数
        public int totalNum = (int) RobotConfig.russianRoulette.getOrDefault("totalNum", 6);
        // 会打死人的弹数
        public int realNum = (int) RobotConfig.russianRoulette.getOrDefault("realNum", 1);
        // 奖励的基准，如果中弹的概率为 p，不中弹的概率为 1 - p，那么中弹会扣除 base * (1 - p) 的积分，而不中弹会增加 base * p 的积分
        public int base = (int) RobotConfig.russianRoulette.getOrDefault("base", 100);
        // 已经被命中的成员
        public Set<String> shotMan = new HashSet<>();
        // 成员获取的收益
        public Map<String, Integer> income = new HashMap<>();

        public Helper(int totalNum, int realNum) {
            this.totalNum = totalNum;
            this.realNum = realNum;
        }

        public int updateIncome(String qq, int delta) {
            income.put(qq, income.getOrDefault(qq, 0) + delta);
            return income.get(qq);
        }

        public Helper() {
        }

        public int getRewardOrPunishment(boolean shot, String qq) {
            // 被击中的概率
            double p = realNum * 1.0 / totalNum * 1.0;
            Random random = new Random();
            int v = base + (int) (!shot ? base * p : base * (1 - p));
            int r = random.nextInt(base / 10);
            v = random.nextInt(2) == 0 ? v + r : v - r;
            return shot ? v + income.getOrDefault(qq, 0) : v;
        }

        public boolean shot() {
            int random = (int) (Math.random() * totalNum + 1);
            return random <= realNum;
        }
    }

    private Map<String, Helper> map = new HashMap<>();

    @Override
    public List<MessageChain> handleMessageEvent(MessageEvent event, Context ctx) {
        try {
            String content = getPlantContent(event);
            String groupId = getGroupId(event);
            String qq = getSenderId(event);
            if (content.startsWith(stop)) {
                if (Permission.hasGroupAdmin(qq) || getSenderPermission(event) != 0) {
                    map.remove(groupId);
                    return buildMessageChainAsList("弹夹已卸下，游戏结束");
                }
                return buildMessageChainAsList(getQuoteReply(event), "您没有权限停止游戏，必须要本群管理员或机器人的群管理员权限才能执行此操作");
            }
            if (content.startsWith(reload)) {
                content = content.replace(reload, "");
                if (map.containsKey(groupId)) {
                    return buildMessageChainAsList(getQuoteReply(event), "游戏还未结束，请勿重复操作");
                }
                List<String> splitSpaces = StringUtil.splitSpaces(content);
                Helper helper = new Helper();
                if (splitSpaces.size() != 0) {
                    if (!Permission.hasGroupAdmin(qq) && getSenderPermission(event) == 0) {
                        return buildMessageChainAsList("您没有权限自定义弹夹数", getQuoteReply(event));
                    }
                }
                if (splitSpaces.size() == 1) {
                    helper.realNum = Integer.parseInt(splitSpaces.get(0));
                }
                if (splitSpaces.size() == 2) {
                    helper.realNum = Integer.parseInt(splitSpaces.get(0));
                    helper.totalNum = Integer.parseInt(splitSpaces.get(1));
                    if (helper.totalNum < 1 || helper.totalNum > 20) {
                        throw new Exception("自定义装弹数总数最多 20 个，最少 1 个");
                    }
                }
                if (helper.realNum <= 0 || helper.totalNum <= 0) {
                    throw new Exception("咋想的，真弹数或总弹数为 0 还咋玩");
                }
                if (helper.realNum >= helper.totalNum) {
                    throw new Exception("这么多真弹数？想集体暴毙？");
                }
                map.put(groupId, helper);
                return buildMessageChainAsList(getQuoteReply(event), "装弹成功，总弹数 " + helper.totalNum + " 弹，真弹数 " + helper.realNum + " 弹，让我们开始开枪吧，我已经迫不及待了！");
            }
            if (!content.equals(shot)) {
                return null;
            }
            Helper helper = null;
            if ((helper = map.get(groupId)) == null) {
                return buildMessageChainAsList("群暂未开启游戏，发送 装弹 来填充弹夹!");
            }
            if (helper.shotMan.contains(qq)) {
                return buildMessageChainAsList(getQuoteReply(event), "你已经中弹一次了，想学安倍身中两弹？我看你还是老老实实等待游戏结束吧！");
            }
            boolean shot;
            int v;
            synchronized (helper) {
                shot = helper.shot();
                v = helper.getRewardOrPunishment(shot, qq);
                if (shot) {
                    helper.realNum--;
                    helper.updateIncome(qq, -v);
                } else {
                    helper.updateIncome(qq, v);
                }
                helper.totalNum--;
                StringBuilder sb = new StringBuilder();
                if (shot) {
                    sb.append("boom！一朵绚烂的血花盛开，你送走了你自己！\n");
                    sb.append("『扣除积分" + v + "』\n");
                    helper.shotMan.add(qq);
                } else {
                    sb.append("有惊无险，这是一个空枪！\n");
                    sb.append("『增加积分" + v + "』\n");
                }
                sb.append("剩余总弹数: " + helper.totalNum + "\n");
                sb.append("剩余真弹数: " + helper.realNum + "\n");
                if (helper.realNum == 0 || helper.totalNum == 0 || helper.realNum == helper.totalNum) {
                    if (helper.realNum == 0) {
                        sb.append("弹夹已无真弹，游戏结束\n");
                    } else {
                        sb.append("弹夹已无空弹，剩下的人都将被子弹穿膛，游戏结束\n");
                    }
                    map.remove(groupId);
                    int max = helper.income.values().stream().max((a, b) -> a - b).get();
                    int min = helper.income.values().stream().min((a, b) -> a - b).get();
                    for (Map.Entry<String, Integer> it : helper.income.entrySet()) {
                        if (it.getValue() == max) {
                            sb.append("本次游戏最大获益人：" + it.getKey() + "，总计" + max + " 积分\n");
                            max = Integer.MAX_VALUE;
                        }
                        if (it.getValue() == min) {
                            sb.append("本次游戏最大倒霉蛋：" + it.getKey() + "，总计" + min + " 积分\n");
                            min = Integer.MAX_VALUE;
                        }
                    }
                }
                if (helper.realNum == 1 && helper.totalNum == 1) {
                    sb.append("最后一个倒霉鬼是谁呢？");
                }
                return buildMessageChainAsList(buildMessageChain(getQuoteReply(event), sb.toString()));
            }
        } catch (Exception e) {
            return buildMessageChainAsList("发生了意料之内的错误：" + e.getMessage());
        }
    }


    @Override
    public boolean shouldHandle(MessageEvent event, Context ctx) {
        return startWithKeywords(event, OfUtil.ofSet(reload, shot, stop));
    }
}
