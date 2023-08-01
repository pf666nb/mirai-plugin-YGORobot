package com.happysnaker.handler.impl;

import com.happysnaker.api.YgoSearchApi;
import com.happysnaker.config.RobotConfig;
import com.happysnaker.entry.CardBeanByBaige;
import com.happysnaker.entry.CardEntry;
import com.happysnaker.exception.FileUploadException;
import com.happysnaker.handler.handler;
import com.happysnaker.proxy.Context;
import com.happysnaker.utils.RobotUtil;
import com.happysnaker.utils.StringUtil;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author apple
 */
@handler(priority = 5)
public class YgoImageShareMessageEventHandler extends GroupMessageEventHandler{
    public final String cardPicPath = "";
    public final String randomCard = "抽一张卡";

    public final String getOneCard = "查卡";

    public final String guessCard = "猜一张卡";

    public final String todayCardLuck = "今日牌运";
    private final Set<String> keywords = new HashSet<>();


    public YgoImageShareMessageEventHandler(){
        keywords.add(randomCard);
        keywords.add(getOneCard);
        keywords.add(guessCard);
        keywords.add(todayCardLuck);
    }

    /**
     *
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
                ans.add(doParseYgoImage(event));
            }
            //根据查卡后面的关键字返回对应的列表
            if(content.startsWith(getOneCard)){
                List<String> tags = getTags(content, getOneCard);
                ans.add(doParseYgoImage(event,tags));
            }
            if(content.startsWith(guessCard)){

            }
            if (content.startsWith(todayCardLuck)){

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
     * 生成一张随机的游戏王卡片加入MessageChain中返回
     * @param event 消息事件
     * @return MessageChain
     */
    private MessageChain doParseYgoImage(MessageEvent event) throws IOException, FileUploadException {
        //获取一张随机的游戏王卡片
        CardEntry card = YgoSearchApi.RandomImage();
        Image image = null;
        if(card!=null) {
             image = RobotUtil.uploadImage(event, RobotConfig.configFolder+"/card/" + card.getId() + ".jpg");
        }else {
            return new MessageChainBuilder()
                    .append("零依网络的随机一卡出现了未知错误！请联系本初")
                    .build();
        }

        return new MessageChainBuilder().append(card.getId()).append("\n")
                .append(card.getName()).append("\n")
                .append(image)
                .build();
    }

    /**
     *
     *
     * @param event   消息事件
     * @param tags     关键词
     * @return 返回消息链
     */
    private MessageChain doParseYgoImage(MessageEvent event, List<String> tags) throws IOException, FileUploadException {
        List<String> idList = YgoSearchApi.getImageByKeyWord(tags);
        //TODO 目前优先只返回第一个最接近的卡片
        Image image = null;
        if (idList != null) {
            image = RobotUtil.uploadImage(event, RobotConfig.configFolder+"/card/"+ idList.get(0) + ".jpg");
        }
        return new MessageChainBuilder()
                .append(image)
                .build();

    }

    private List<String> getTags(String content, String tem) {
        List<String> tags = new ArrayList<>();
        List<String> strings = StringUtil.splitSpaces(content.replace(tem, ""));
        for (String s : strings) {
            if (!s.isEmpty() && !s.equals(tem)) {
                tags.add(s.trim());
            }
        }
        return tags;
    }
}
