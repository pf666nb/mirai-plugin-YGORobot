package com.happysnaker.handler.impl;

import com.happysnaker.api.YgoApi;
import com.happysnaker.exception.FileUploadException;
import com.happysnaker.handler.handler;
import com.happysnaker.proxy.Context;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author apple
 */
@handler(priority = 1)
public class YgoImageShareMessageEventHandler extends GroupMessageEventHandler{

    public final String randomCard = "抽一张卡";

    public final String getOneCard = "查卡";


    private final Set<String> keywords = new HashSet<>();


    public YgoImageShareMessageEventHandler(){
        keywords.add(randomCard);
        keywords.add(getOneCard);

    }

    /**
     * 将要回复的消息，默认使用青云客 API 消息回复，允许子类进行扩展，如果此消息返回 null，则不会尝试回复该消息
     *
     * @param event 经过 proxyContent 处理后的消息
     * @param ctx
     * @return 允许发送多条消息，因此需要返回一个消息列表
     */
    @Override
    public List<MessageChain> handleMessageEvent(MessageEvent event, Context ctx) {
        String content = getPlantContent(event);
        List<MessageChain> ans = new ArrayList<>();
        try{
            //随机抽一张卡返回
            if(content.startsWith(randomCard)){

            }
            //根据查卡后面的关键字返回对应的列表
            if(content.startsWith(getOneCard)){

            }


        }catch (Exception e){
            logError(event,e);
            ans.add(new MessageChainBuilder()
                    .append("闪刀网络出现了问题。。。\n错误原因：")
                    .append(e.getMessage() == null ? e.getCause().toString(): e.getMessage())
                    .build());
        }

        return ans;
    }

    /**
     *
     *
     * @param event   消息事件
     * @param tags     关键词
     * @return 返回消息链
     */
    private MessageChain doParseYgoImage(MessageEvent event, List<String> tags) throws MalformedURLException, FileUploadException {
        String image = YgoApi.getImage(tags);
        return new MessageChainBuilder()
                .append(uploadImage(event, new URL(image))).build();

    }
}
