package com.happysnaker.handler.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.happysnaker.api.YgoSearchApi;
import com.happysnaker.config.RobotConfig;
import com.happysnaker.entry.CardBeanByBaige;
import com.happysnaker.entry.CardEntry;
import com.happysnaker.exception.FileUploadException;
import com.happysnaker.handler.handler;
import com.happysnaker.proxy.Context;
import com.happysnaker.utils.RobotUtil;
import com.happysnaker.utils.StringUtil;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    public final String silence = "因果切断";

    public final String  unmute = "死者苏生";


    public final String MonsterTag = "怪兽饼图";

    public final String DeckTag = "卡组饼图";

    public final String ExTag = "额外饼图";

    public final String SideTag = "SIDE饼图";

    public final String SpellTag = "魔法饼图";

    public final String TrapTag = "陷阱饼图";

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
        keywords.add(unmute);
        keywords.add(silence);
        keywords.add(MonsterTag);
        keywords.add(ExTag);
        keywords.add(DeckTag);
        keywords.add(SideTag);
        keywords.add(TrapTag);
        keywords.add(SpellTag);
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
            //娱乐卡片，禁言和解禁
            if(content.startsWith(unmute) || content.startsWith(silence)){
                ans.add(doSilence(event,content));
            }
            if (content.startsWith(DeckTag)
            ||content.startsWith(ExTag)
            ||content.startsWith(MonsterTag)
            ||content.startsWith(SideTag)
            ||content.startsWith(SpellTag)
            ||content.startsWith(TrapTag)){
                ans.add(doYgoPie(event,content));
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

    private MessageChain doYgoPie(MessageEvent event,String content) {
        Image image = null;
        try {
            image = RobotUtil.uploadImage(event, RobotConfig.configFolder+"/pic/YGO"+content+".jpeg");
        } catch (FileUploadException e) {
            throw new RuntimeException(e);
        }
        return new MessageChainBuilder().append(image).build();
    }

    private MessageChain doSilence(MessageEvent event,String content) throws FileUploadException {
        Image image = null;
        for (SingleMessage message : event.getMessage()) {
            if (message instanceof At) {
                At at = (At) message;
                 long target = at.getTarget();
                 if(content.startsWith(silence)){
                     mute(String.valueOf(target),getGroupMessageEvent(event).getGroup(),180);
                     image = RobotUtil.uploadImage(event, RobotConfig.configFolder+"/card/71587526.jpg");

                     return new MessageChainBuilder()
                             .append(event.getSender().getNick())
                             .append("对")
                             .append(getGroupMessageEvent(event).getGroup().get(target).getNick())
                             .append("使用了因果切断！")
                             .append("\n")
                             .append(image)
                             .build();
                 }else {
                     for (NormalMember member : getGroupMessageEvent(event).getGroup().getMembers()) {
                         if (member.getId() == Long.parseLong(String.valueOf(target))) {
                             member.unmute();
                             image = RobotUtil.uploadImage(event, RobotConfig.configFolder+"/card/83764718.jpg");
                             return new MessageChainBuilder()
                                     .append(event.getSender().getNick())
                                     .append("对")
                                     .append(getGroupMessageEvent(event).getGroup().get(target).getNick())
                                     .append("使用了死者苏生！")
                                     .append("\n")
                                     .append(image).build();
                         }
                     }
                 }

            }
        }
        return new MessageChainBuilder().append("因为法老王的诅咒！发动失败！").build();
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
        falseBuilder.append("忌：");
        AtomicInteger trueFlag = new AtomicInteger();
        AtomicInteger falseFlag = new AtomicInteger();
        todayMap.forEach((k,v)->{
             Integer i = todayMap.get(k);
             if(i==1&& trueFlag.get() <3){
                 trueFlag.getAndIncrement();
                 trueBuilder.append(k).append(",");
             }else if(falseFlag.get() <3){
                 falseFlag.getAndIncrement();
                 falseBuilder.append(k).append(",");
             }
        });
        DateUtil.today();
        return new MessageChainBuilder()
                .append("今日牌运:").append("\n")
                .append("今天是星期").append(DateUtil.thisDayOfWeek()-1+"  ").append("人品值：")
                .append(RandomUtil.randomInt(0,101)+"")
                .append("\n")
                .append("星座: ").append(DateUtil.getZodiac(DateUtil.month(DateUtil.date()),DateUtil.dayOfMonth(DateUtil.date()))).append("\n")
                .append("今日命卡：").append(card.getName()).append("\n")
                .append("神碑YYDS！").append("\n")
                .append("打卡成功，零依提醒你要多运动哦~~")
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
