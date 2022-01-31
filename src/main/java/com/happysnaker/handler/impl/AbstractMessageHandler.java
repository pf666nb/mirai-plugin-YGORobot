package com.happysnaker.handler.impl;

import com.happysnaker.api.QingYunKeApi;
import com.happysnaker.config.RobotConfig;
import com.happysnaker.exception.CanNotSendMessageException;
import com.happysnaker.exception.FileUploadException;
import com.happysnaker.handler.MessageHandler;
import com.happysnaker.utils.NetUtils;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Happysnaker
 * @description
 * @date 2022/1/14
 * @email happysnaker@foxmail.com
 */
public abstract class AbstractMessageHandler implements MessageHandler {
    public final String at = "[mirai:at:qq]";
    public final String atRegex = "\\[mirai:at:\\d+\\]";
    public final String atAllRegex = "\\[mirai:atall\\]";
    public final String faceRegex = "\\{face:\\d+\\}";
    public final String imageRegex = "\\[mirai:image:\\{\\w+-\\w+-\\w+-\\w+-\\w+\\}.\\w+\\]";

    // 当前机器人的 qq 号
    protected List<String> qqs = null;
    protected MiraiLogger logger = RobotConfig.logger;

    protected void info(String msg) {logger.info(msg);}

    protected void debug(String msg) {logger.debug(msg);}

    protected void error(String msg) {logger.error(msg);}


    public AbstractMessageHandler() {
        List<Bot> bots = Bot.getInstances();
        List<String> qqs = new ArrayList<>();
        for (Bot bot : bots) {
            System.out.println(bot.getId());
            qqs.add(String.valueOf(bot.getId()));
        }
        this.qqs = List.of("3070058713");
    }


    /**
     * 将要回复的消息，默认使用青云客 API 消息回复，允许子类进行扩展，如果此消息返回 null，则不会尝试回复该消息
     *
     * @param event 经过 proxyContent 处理后的消息
     * @return 允许发送多条消息，因此需要返回一个消息列表
     */
    protected List<MessageChain> getReplyMessage(MessageEvent event) {
        String content = getPlantContent(event);
        String help1 = "帮助", help2 = "help";
        if (help1.equals(content) || help2.equals(content)) {
            try {
                return List.of(new MessageChainBuilder()
                        .append(new PlainText(RobotConfig.menu))
                        .append(uploadImage(event, new URL("https://tse4-mm.cn.bing.net/th/id/OIP-C.rHuc8SKa0wLVwCqqA27uIwHaEt?pid=ImgDet&rs=1")))
                        .build());
            } catch (FileUploadException | MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return List.of(replaceFaceFromContent(QingYunKeApi.getMessage(content)));
    }

    /**
     * 具体的回复动作
     *
     * @param replyMessages
     * @param contact
     * @throws CanNotSendMessageException
     */
    private void reply(List<MessageChain> replyMessages, Contact contact) throws CanNotSendMessageException {
        if (replyMessages == null) return;
        try {
            for (MessageChain replyMessage : replyMessages) {
                MessageReceipt<Contact> receipt = contact.sendMessage(replyMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CanNotSendMessageException("Can not send message: \""
                    + replyMessages + "\", the contact is: " + contact + "\nCause by " + e.getCause().toString());
        }
    }



    /**
     * 处理一个新的消息事件
     *
     * @param messageEvent
     */
    @Override
    public void handleMessageEvent(@NotNull MessageEvent messageEvent) {
        if (shouldHandle(messageEvent)) {
            try {
                reply(getReplyMessage(messageEvent), messageEvent.getSubject());
            } catch (CanNotSendMessageException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 对消息中的 {face:num} 表情进行解析，并将表情用实际的 Face 类替代，封装进 MessageChain 中，MessageChain 中仍然保持原消息中表情和其他消息的相对位置
     *
     * @param text 要解析的文本
     * @return MessageChain
     */
    protected MessageChain replaceFaceFromContent(String text) {
        String regex = faceRegex;
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        boolean findFace = false;
        int start = 0, end = 0;
        while (matcher.find()) {
            findFace = true;
            end = matcher.start();
            if (start != end) {
                messageChainBuilder.append(new PlainText(text.substring(start, end)));
            }
            String[] split = matcher.group().split("\\D+");
            for (String s1 : split) {
                if (!s1.isEmpty()) {
                    try {
                        int face = Integer.parseInt(s1);
                        messageChainBuilder.append(new Face(face));
                    } catch (Exception e) {
                        // continue
                    }
                }
            }
            start = matcher.end();
        }
        return findFace ? messageChainBuilder.build() : messageChainBuilder.append(text).build();
    }


    /**
     * 从事件中提取消息，并将该消息转换为 mirai 码
     *
     * @param event
     * @return mirai 编码消息
     */
    protected String getContent(MessageEvent event) {
        if (event == null) {
            return null;
        }
        return event.getMessage().serializeToMiraiCode();
    }



    /**
     * 从事件中提取消息，该消息仅包含文本内容，不包含任何 表情、图片、语音等
     * 但是必须注意，at 和 at all 消息仍然以 mirai 编码形式被包含在内
     * @see #getContent
     * @param event
     * @return 纯文本
     */
    protected String getPlantContent(MessageEvent event) {
        StringBuffer sb = new StringBuffer();
        for (SingleMessage singleMessage : event.getMessage()) {
            if (singleMessage instanceof PlainText) {
                sb.append(singleMessage);
            }
        }
        return sb.toString().trim();
    }



    /**
     * 读取文件（图片）并上传至腾讯服务器
     *
     * @param event
     * @param filename 图片文件路径名
     * @return net.mamoe.mirai.message.data.Image
     */
    protected net.mamoe.mirai.message.data.Image uploadImage(MessageEvent event, String filename) throws FileUploadException {
        try {
            return ExternalResource.uploadAsImage(new File(filename), event.getSubject());
        } catch (Exception e) {
            throw new FileUploadException("Can not upload the image from the file: " + filename + "\nCause by " + e.getCause().toString());
        }
    }


    /**
     * 如果匹配则删除，注意该函数会返回 null
     *
     * @param content   源消息(MIRAI编码)
     * @param regex at 匹配的消息
     * @return 如果被 at 将返回去除 atMessage(所有的) 信息后的消息，否则返回 null
     */
    protected String handlerContentIfMatches(String content, String regex) {
        // 如果 split = 1，说明没有分割，即不包含该 regex
        if (content != null && content.split(regex).length != 1) {
            return content.replaceAll(regex, "").trim();
        }
        return null;
    }


    /**
     * 如果消息中包含图片 mirai 信息，则去除它们
     * @see AbstractMessageHandler#getPlantContent
     * @param content (MIRAI编码)
     * @return 如果消息中包含图片信息，则去除它们，否则什么也不做
     */
    protected String handlerContentIfContainsImage(String content) {
        String ans;
        return (ans = handlerContentIfMatches(content, imageRegex)) == null ? content : ans;
    }

    /**
     * 网络图片并上传至腾讯服务器
     *
     * @param event
     * @param url   网络图片 URL
     * @return net.mamoe.mirai.message.data.Image
     */
    protected net.mamoe.mirai.message.data.Image uploadImage(MessageEvent event, URL url) throws FileUploadException {
        try {
            return ExternalResource.uploadAsImage(NetUtils.sendAndGetResponseStream(url, "GET", null, null), event.getSubject());
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileUploadException("Can not upload the image from the url: " + url + "\nCause by " + e.getCause().toString());
        }
    }

    /**
     * 获取一条引用消息的源，如果不存在，则返回 null
     *
     * @param event
     * @return MessageSource
     */
    protected MessageSource getQuoteReply(MessageEvent event) {
        return event.getMessage().get(QuoteReply.Key).getSource();
    }

    /**
     * 获取一个消息链中的 Images
     *
     * @param chain
     * @return List&lt;SingleMessage&gt;，可以将 SingleMessage 强转为 Image 类
     */
    protected List<SingleMessage> getImagesFromMessage(MessageChain chain) {
        return chain.stream().filter(Image.class::isInstance).collect(Collectors.toList());
    }


}
