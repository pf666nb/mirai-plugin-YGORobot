package com.happysnaker.handler.message;


import com.happysnaker.api.BingApi;
import com.happysnaker.api.PixivApi;
import com.happysnaker.config.RobotConfig;
import com.happysnaker.context.Context;
import com.happysnaker.exception.FileUploadException;
import com.happysnaker.handler.handler;
import com.happysnaker.utils.IOUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * 分享一些图片，你懂的
 *
 * @author Happysnaker
 * @description
 * @date 2022/1/20
 * @email happysnaker@foxmail.com
 */
@handler(priority = 1)
public class ImageShareMessageEventHandler extends GroupMessageEventHandler {
    public final String chickenSoup = "鸡汤";
    public final String mysteriousImage = "神秘代码";
    public final String landscapeImage = "风景图";
    public final String beautifulImage = "美图";
    public final String seImage = "涩图";
    public final String seImagePlus = "高清涩图";
    public final String beautifulImageUrl = PixivApi.beautifulImageUrl;
    public final String chickenSoupUrl = PixivApi.chickenSoupUrl;
    public final String duChickenSoupUrl = PixivApi.duChickenSoupUrl;
    public final String pixivSearchApi = PixivApi.pixivSearchApi;

    private Set<String> keywords = new HashSet<>();

    public ImageShareMessageEventHandler() {
        keywords.add(chickenSoup);
        keywords.add(mysteriousImage);
        keywords.add(landscapeImage);
        keywords.add(beautifulImage);
        keywords.add(seImage);
        keywords.add(seImagePlus);
    }

    @Override
    public List<MessageChain> handleMessageEvent(MessageEvent event, Context ctx) {
        String content = getPlantContent(event);
        List<MessageChain> ans = new ArrayList<>();
        try {
            // 鸡汤
            if (content.contains(chickenSoup)) {
                ans.add(doParseChickenSoup(event));
            }

            // 神秘代码
            if (content.startsWith(mysteriousImage)) {
                // 需要分割 tag
                List<String> tags = getTags(content, mysteriousImage);
                ans.add(doParseMysteriousImage(event, tags));
            }

            // 涩图 或 高清涩图
            if (content.startsWith(seImage) || content.startsWith(seImagePlus)) {
                String tem = content.startsWith(seImage) ? seImage : seImagePlus;
                // 需要分割 tag
                List<String> tags = getTags(content, tem);
                //检测到涩图需要自动撤回，因此自定义逻辑处理，处理完返回 null，不需要抽象类的逻辑
                MessageChain chain = doParseSeImage(event, tags, tem.equals(seImagePlus));

                if (chain != null && !chain.isEmpty() && chain.get(0) instanceof Image) {
                    MessageReceipt<Contact> receipt = event.getSubject().sendMessage(chain);
                    receipt.recallIn(RobotConfig.pictureWithdrawalTime * 1000);
                } else {
                    throw new Exception("无法获取涩图...");
                }
                return null;
            }


            // 美图
            if (content.contains(beautifulImage)) {
                ans.add(doParseBeautifulImage(event));
            }

            // 风景图
            if (content.contains(landscapeImage)) {
                ans.add(doParseLandscapeImage(event));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logError(event, e);
            // next
            ans.add(new MessageChainBuilder().append("意外地失去了与地球上的通信...\n错误原因：" + e.getMessage().toString()).build());
        }
        return ans;
    }

    /**
     * 根据 plantContent 获取 tag
     *
     * @param content plantContent
     * @param tem     关键词
     * @return 返回去除关键词后分割空格检索出的 tags
     */
    private List<String> getTags(String content, String tem) {
        List<String> tags = new ArrayList<>();
        String[] strings = content.replace(tem, "").split("\\s+");
        for (String s : strings) {
            if (!s.isEmpty() && !s.equals(tem)) {
                tags.add(s.trim());
            }
        }
        return tags;
    }

    /**
     * 获取风景图
     *
     * @param event
     * @return
     * @throws MalformedURLException
     * @throws FileUploadException
     */
    private MessageChain doParseLandscapeImage(MessageEvent event) throws MalformedURLException, FileUploadException {
        return new MessageChainBuilder()
                .append(uploadImage(event, new URL(BingApi.getRandomImageUrl())))
                .build();
    }

    /**
     * 获取美图
     *
     * @param event
     * @return
     * @throws MalformedURLException
     * @throws FileUploadException
     */
    private MessageChain doParseBeautifulImage(MessageEvent event) throws MalformedURLException, FileUploadException {
        return new MessageChainBuilder()
                .append(uploadImage(event, new URL(beautifulImageUrl)))
                .build();
    }

    /**
     * 获取涩图
     *
     * @param event
     * @param tags
     * @param isPlus 是否是高清涩图
     * @return
     * @throws IOException
     * @throws FileUploadException
     */
    private MessageChain doParseSeImage(MessageEvent event, List<String> tags, boolean isPlus) throws IOException, FileUploadException {
        if (!RobotConfig.colorSwitch) {
            return null;
        }
        long pid = PixivApi.getSeImagePid(tags);
        if (pid == -1) {
            return buildMessageChain("查无此图");
        }
        // 如果不是高清涩图加上 &web=true，表示请求一张更小的图片
        String api = isPlus ? pixivSearchApi : pixivSearchApi + "&web=true";
        String imgUrl = api.replace("IMGID", String.valueOf(pid));
        info("imgurl = " + imgUrl);
        return new MessageChainBuilder()
                .append(uploadImage(event, new URL(imgUrl)
                )).build();
    }

    /**
     * 获取神秘代码
     *
     * @param event
     * @param tags
     * @return
     * @throws IOException
     * @throws FileUploadException
     */
    private MessageChain doParseMysteriousImage(MessageEvent event, List<String> tags) throws IOException, FileUploadException {
        if (tags.size() == 0) {
            return buildMessageChain(uploadImage(event, new URL(PixivApi.randomUrl)));
        }
        long pid = PixivApi.getImagePid(tags);
        if (pid == -1) {
            return buildMessageChain("查无此图");
        }
        String imgUrl = pixivSearchApi.replace("IMGID", String.valueOf(pid));
        info("imgurl = " + imgUrl);
        return new MessageChainBuilder()
                .append(uploadImage(event, new URL(imgUrl))).build();
    }

    /**
     * 获取鸡汤
     *
     * @param event
     * @return
     * @throws IOException
     * @throws FileUploadException
     */
    private MessageChain doParseChickenSoup(MessageEvent event) throws IOException, FileUploadException {
        String chickenSoupUrl = this.chickenSoupUrl;
        // 恶搞，生成毒鸡汤
        if (Math.random() < RobotConfig.duChickenSoupProbability) {
            chickenSoupUrl = this.duChickenSoupUrl;
        }
        Map<String, Object> map = IOUtil.sendAndGetResponseMap(new URL(chickenSoupUrl), "GET", null, null);
        String text = (String) ((Map) (map.get("data"))).get("text");
        return new MessageChainBuilder()
                .append(text)
                .append(uploadImage(event, new URL(beautifulImageUrl)))
                .build();
    }

    /**
     * 不需要 @ 机器人，检测到以关键词开头的事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean shouldHandle(MessageEvent event, Context ctx) {
        return startWithKeywords(event, keywords);
    }
}
