package com.happysnaker.intercept;

import com.happysnaker.config.RobotConfig;
import com.happysnaker.utils.RobotUtil;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 后置拦截器，对回复的语句做自定义替换
 *
 * @author Happysnaker
 * @description
 * @date 2022/7/2
 * @email happysnaker@foxmail.com
 */
@intercept
public class ReplyInterceptor implements Interceptor {


    @Override
    public boolean interceptBefore(MessageEvent event) {
        return false;
    }

    @Override
    public List<MessageChain> interceptAfter(MessageEvent e, List<MessageChain> mc) {
        List<MessageChain> ans = new ArrayList<>();
        if (mc == null) {
            return null;
        }
        for (MessageChain chain : mc) {
            String content = RobotUtil.getContent(chain);
            boolean v = false;
            for (Map<String, String> map : RobotConfig.replyReplace) {
                for (Map.Entry<String, String> it : map.entrySet()) {
                    if (content.contains(it.getKey())) {
                        content = content.replace(it.getKey(), it.getValue());
                        v = true;
                    }
                }
            }
            ans.add(v ? RobotUtil.parseMiraiCode(content) : chain);
        }
        return ans;
    }
}
