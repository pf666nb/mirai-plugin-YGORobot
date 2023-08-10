package com.happysnaker.handler.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
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
import java.util.*;

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

    private  static   HashMap<String,Integer> todayMap = new HashMap<>();
    static {
        todayMap.put("顶G",0);
        todayMap.put("神抽",0);
        todayMap.put("解场",0);
        todayMap.put("削手",0);
        todayMap.put("封锁",0);
        todayMap.put("除外",0);
        todayMap.put("做康",0);
        todayMap.put("烧血",0);
        todayMap.put("擦牌",0);
        todayMap.put("堆墓",0);
    }

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
                ans.add(doDayCardLuck(event));
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

    private MessageChain doDayCardLuck(MessageEvent event) throws IOException, FileUploadException {
        CardEntry card = YgoSearchApi.RandomImage();
        Image image = null;
        if(card!=null) {
            image = RobotUtil.uploadImage(event, RobotConfig.configFolder+"/card-pic/" + card.getId() + ".jpg");
        }else {
            return new MessageChainBuilder()
                    .append("零依网络的今日牌运出现了未知错误！请联系本初")
                    .build();
        }
        todayMap.forEach((k,v)->{
            todayMap.put(k, RandomUtil.randomInt(0,2));
        });
        StringBuilder trueBuilder = new StringBuilder();
        trueBuilder.append("宜：");
        StringBuilder falseBuilder = new StringBuilder();
        falseBuilder.append("禁：");
        todayMap.forEach((k,v)->{
             Integer i = todayMap.get(k);
             if(i==1){
                 trueBuilder.append(k).append(",");
             }else{
                 falseBuilder.append(k).append(",");
             }
        });
        return new MessageChainBuilder()
                .append("今日牌运--今日命卡：")
                .append(card.getName()).append("\n")
                .append(image)
                .append("\n")
                .append(trueBuilder).append("\n")
                .append(falseBuilder)
                .build();
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
        CardBeanByBaige imageByKeyWord = YgoSearchApi.getImageByKeyWord(tags);
        //TODO 目前优先只返回第一个最接近的卡片
        Image image = null;
        if (imageByKeyWord != null) {
            image = RobotUtil.uploadImage(event, RobotConfig.configFolder+"/card/"+ imageByKeyWord.getId() + ".jpg");
        }
        if (image != null) {
            return new MessageChainBuilder()
                    .append(imageByKeyWord.getCn_name())
                    .append("\n")
                    .append(image)
                    .append("\n")
                    .append(imageByKeyWord.getText().getTypes())
                    .append("\n")
                    .append(imageByKeyWord.getText().getDesc())
                    .build();
        }
        return null;

    }

    private List<String> getTags(String content, String tag) {
        List<String> tags = new ArrayList<>();
        List<String> strings = StringUtil.splitSpaces(content.replace(tag, ""));
        for (String s : strings) {
            if (!s.isEmpty() && !s.equals(tag)) {
                tags.add(s.trim());
            }
        }
        return tags;
    }

    /**
     * 注册应该监听的关键字
     * @param event
     * @param ctx
     * @return
     */
    @Override
    public boolean shouldHandle(MessageEvent event, Context ctx) {
        return startWithKeywords(event, keywords);
    }


}
