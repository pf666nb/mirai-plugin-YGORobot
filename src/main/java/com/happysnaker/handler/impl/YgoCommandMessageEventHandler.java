package com.happysnaker.handler.impl;

import com.happysnaker.config.RobotConfig;
import com.happysnaker.exception.FileUploadException;
import com.happysnaker.proxy.Context;
import com.happysnaker.utils.RobotUtil;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * plugin
 * 游戏王指令
 *
 * @author : wpf
 * @date : 2023-08-14 11:57
 **/
//陨石的测试
public class YgoCommandMessageEventHandler extends GroupMessageEventHandler{
    //用来存储最新的5条群消息
    ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(5);

    @Override
    public boolean shouldHandle(MessageEvent event, Context ctx) {
        //只要是群聊事件就监听
        return isGroupMessageEvent(event);
    }

    @Override
    public List<MessageChain> handleMessageEvent(MessageEvent event, Context ctx) {
        List<MessageChain> ans = new ArrayList<>();
        String content = getPlantContent(event);
        queue.add(content);
        if(queue.size()==5){
            try {
                boolean flag = true;
                 String res = queue.take();
                for (String s : queue) {
                    if(!s.equals(res)){
                        flag = false;
                    }
                }
                //触发陨石！
                if (flag){
                    ans.add(doYunshi(event));
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return ans;
    }

    private MessageChain doYunshi(MessageEvent event) {
        Image image = null;
        try {
            image = RobotUtil.uploadImage(event, RobotConfig.configFolder+"/card/27204311.jpg");
        } catch (FileUploadException e) {
            throw new RuntimeException(e);
        }
        return new MessageChainBuilder()
                .append("发现5条重复消息！触发原始生命态-尼比鲁")
                .append(image)
                .build();
    }
}
