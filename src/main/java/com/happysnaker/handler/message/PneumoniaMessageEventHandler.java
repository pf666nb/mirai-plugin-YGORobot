package com.happysnaker.handler.message;

import com.happysnaker.api.PneumoniaApi;
import com.happysnaker.context.Context;
import com.happysnaker.utils.StringUtil;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PneumoniaMessageEventHandler extends GroupMessageEventHandler {
    @Override
    public List<MessageChain> handleMessageEvent(MessageEvent event, Context ctx)  {
        String content = event == null ? "" : getPlantContent(event);
        content = content.replace("疫情", "");
        StringBuilder sb = new StringBuilder();
        try {
            if (content.isEmpty()) {
                content = "中国";
            }
            Map<String, Object> map = PneumoniaApi.queryPneumoniaMap(content);
            if (map == null) {
                return buildMessageChainAsList("未查询到相关数据", getQuoteReply(event));
            }
            String lastUpdateTime = (String) map.get("lastUpdateTime");
            sb.append("查询" + content + "疫情结果\n");
            sb.append("数据更新时间：" + lastUpdateTime + "\n");
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastUpdateTime);
            System.out.println("date = " + date);
            Date now = new Date(System.currentTimeMillis());
            date.setHours(1);
            now.setHours(0);
            if (date.before(now)) {
                sb.append("今日份数据暂未更新\n");
            }
            Map<String, Integer> today = (Map<String, Integer>) map.get("today");
            Map<String, Integer> total = (Map<String, Integer>) map.get("total");
            Map<String, Integer> extDate = (Map<String, Integer>) map.get("extDate");
            sb.append("新增确诊：" + today.getOrDefault("confirm", 0) + "\n");
            if (extDate != null) {
                sb.append("新增无症状感染者：" + extDate.getOrDefault("incrNoSymptom", 0) + "\n");
            }
            sb.append("新增死亡：" + today.getOrDefault("dead", 0) + "\n");
            sb.append("新增治愈：" + today.getOrDefault("heal", 0) + "\n");
            sb.append("新增境外输入：" + today.getOrDefault("input", 0) + "\n");
            sb.append("现有确诊：" + getNowConfirm(total)+ "\n");
            sb.append("累计确诊：" + total.getOrDefault("confirm", 0) + "\n");
            if (extDate != null) {
                sb.append("累计无症状感染者：" + extDate.getOrDefault("noSymptom", 0) + "\n");
            }
            sb.append("累计境外输入：" + total.getOrDefault("input", 0) + "\n");
            sb.append("累计治愈：" + total.getOrDefault("heal", 0) + "\n");
            System.out.println(sb);
            return buildMessageChainAsList(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            logError(event, e);
            return buildMessageChainAsList("哎呦不错哦，竟然出错了，错误原因：" + StringUtil.getErrorInfoFromException(e));
        }
    }

    private int getNowConfirm(Map<String, Integer> total) {
        return total.getOrDefault("confirm", 0)
                - total.getOrDefault("heal", 0)
                - total.getOrDefault("dead", 0);
    }

    @Override
    public boolean shouldHandle(MessageEvent event, Context ctx) {
        String content = getPlantContent(event);
        return StringUtil.reverse(content).startsWith("情疫");
    }
}
