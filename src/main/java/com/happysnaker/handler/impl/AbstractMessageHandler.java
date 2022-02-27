package com.happysnaker.handler.impl;

import com.happysnaker.config.RobotConfig;
import com.happysnaker.exception.CanNotParseCommandException;
import com.happysnaker.exception.CanNotSendMessageException;
import com.happysnaker.exception.FileUploadException;
import com.happysnaker.handler.MessageHandler;
import com.happysnaker.utils.ConfigUtil;
import com.happysnaker.utils.NetUtil;
import com.happysnaker.utils.StringUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Happysnaker
 * @description
 * @date 2022/1/14
 * @email happysnaker@foxmail.com
 */
public abstract class AbstractMessageHandler implements MessageHandler, Serializable {
    public final static ScheduledExecutorService service = Executors.newScheduledThreadPool(0);

    public final String at = "[mirai:at:qq]";
    public final String atRegex = "\\[mirai:at:\\d+\\]";
    public final String atAllRegex = "\\[mirai:atall\\]";
    public final String faceRegex = "\\{face:\\d+\\}";
    public final String imageRegex = "\\[mirai:image:\\{\\w+-\\w+-\\w+-\\w+-\\w+\\}.\\w+\\]";

    // 当前机器人的 qq 号，注意可能有多个 qq（多个机器人）
    protected List<String> qqs = null;
    protected MiraiLogger logger = RobotConfig.logger;

    /**
     * 记录 info 日志，输出至控制台
     *
     * @param msg
     */
    protected void info(String msg) {
        logger.info(msg);
    }

    /**
     * 记录 debug 日志，输出至控制台
     *
     * @param msg
     */
    protected void debug(String msg) {
        logger.debug(msg);
    }


    /**
     * 记录 error 日志，输出至控制台
     *
     * @param msg
     */
    protected void error(String msg) {
        logger.error(msg);
    }


    /**
     * 记录 error 日志，输出至错误文件
     *
     * @param msg
     */
    protected void logError(MessageEvent event, String msg) {
        failApi(event, msg);
    }


    /**
     * 记录 error 日志，输出至错误文件
     */
    protected void logError(MessageEvent event, Throwable e) {
        failApi(event, StringUtil.getErrorInfoFromException(e));
    }


    /**
     * 读取运行时机器人的 QQ，并初始化 qqs
     */
    protected void initBotQQ() {
        List<Bot> bots = Bot.getInstances();
        List<String> qqs = new ArrayList<>();
        for (Bot bot : bots) {
            System.out.println("读取机器人QQ： " + bot.getId());
            qqs.add(String.valueOf(bot.getId()));
        }
        this.qqs = qqs;
    }

    /**
     * 读取机器人所有的群
     *
     * @return
     */
    protected Set<String> getBotsAllGroupId() {
        List<Bot> bots = Bot.getInstances();
        Set<String> ans = new HashSet<>();
        for (Bot bot : bots) {
            for (Group group : bot.getGroups()) {
                ans.add(String.valueOf(group.getId()));
            }
        }
        return ans;
    }

    public AbstractMessageHandler() {

    }


    /**
     * 将要回复的消息，子类需要实现
     */
    protected abstract List<MessageChain> getReplyMessage(MessageEvent event);

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
                if (replyMessage != null) {
                    MessageReceipt<Contact> receipt = contact.sendMessage(replyMessage);
                }
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
        try {
            reply(getReplyMessage(messageEvent), messageEvent.getSubject());
        } catch (CanNotSendMessageException e) {
            e.printStackTrace();
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
    protected static String getContent(MessageEvent event) {
        if (event == null) {
            return null;
        }
        return getContent(event.getMessage());
    }


    /**
     * 该消息转换为 mirai 码
     *
     * @param chain
     * @return mirai 编码消息
     */
    protected static String getContent(MessageChain chain) {
        if (chain == null) {
            return null;
        }
        return chain.serializeToMiraiCode();
    }

    /**
     * 从 mirai 编码转换为 MessageChain
     *
     * @param content
     * @return
     */
    protected MessageChain parseMiraiCode(String content) {
        return MiraiCode.deserializeMiraiCode(content);
    }


    /**
     * 从事件中提取消息，该消息仅包含文本内容，不包含任何 表情、图片、语音等
     * 但是必须注意，at 和 at all 消息仍然以 mirai 编码形式被包含在内
     *
     * @param event
     * @return 纯文本
     * @see #getContent
     */
    protected String getPlantContent(MessageEvent event) {
        if (event == null) {
            return null;
        }
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
     * @param content 源消息(MIRAI编码)
     * @param regex   at 匹配的消息
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
     *
     * @param content (MIRAI编码)
     * @return 如果消息中包含图片信息，则去除它们，否则什么也不做
     * @see AbstractMessageHandler#getPlantContent
     */
    protected String handlerContentIfContainsImage(String content) {
        String ans;
        return (ans = handlerContentIfMatches(content, imageRegex)) == null ? content : ans;
    }

    /**
     * 建造 MessageChain
     *
     * @param m 多个文本消息
     * @return 将多个文本消息结合成 MessageChain
     */
    protected MessageChain buildMessageChain(String... m) {
        MessageChainBuilder builder = new MessageChainBuilder();
        for (String s : m) {
            builder.append(s);
        }
        return builder.build();
    }

    /**
     * 建造 MessageChain，参数是多个 SingleMessage
     *
     * @param m 多个 SingleMessage
     * @return 将多个 SingleMessage 组合成 MessageChain
     */
    protected MessageChain buildMessageChain(Object... m) {
        MessageChainBuilder builder = new MessageChainBuilder();
        for (Object s : m) {
            if (s instanceof String) {
                s = new PlainText((CharSequence) s);
            }
            builder.append((SingleMessage) s);
        }
        return builder.build();
    }


    /**
     * 建造 MessageChain，参数是多个 SingleMessage
     *
     * @param m 多个 SingleMessage
     * @return 将多个 SingleMessage 组合成 MessageChain List，List 的大小只为 1
     */
    protected List<MessageChain> buildMessageChainAsList(Object... m) {
        return List.of(buildMessageChain(m));
    }

    /**
     * 获取发送者的 QQ
     *
     * @param event
     * @return
     */
    protected static String getSenderId(MessageEvent event) {
        return String.valueOf(event.getSender().getId());
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
            return ExternalResource.uploadAsImage(NetUtil.sendAndGetResponseStream(url, "GET", null, null), event.getSubject());
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileUploadException("Can not upload the image from the url: " + url + "\nCause by " + e.getCause().toString());
        }
    }

    /**
     * 获取引用消息事件的源，如果不存在，则返回 null
     *
     * @param event
     * @return MessageSource
     */
    protected MessageSource getQuoteSource(MessageEvent event) {
        return event.getMessage().get(QuoteReply.Key).getSource();
    }

    /**
     * 设置引用回复，如果失败，则返回 null<br/>
     * 如果想回复某消息，你可以这样做：chainBuilder.append(getQuoteReply(e))<br/>或者调用父类方法：buildMessageChain(msg, getQuoteReply(e)) 以构造一条消息链
     *
     * @param event
     * @return MessageSource
     * @see #buildMessageChain(Object...)
     */
    protected QuoteReply getQuoteReply(MessageEvent event) {
        return new QuoteReply(event.getSource());
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

    /**
     * 检查事件消息是否以关键字开头
     *
     * @param event
     * @param keywords
     * @return
     */
    protected boolean startWithKeywords(MessageEvent event, Collection<String> keywords) {
        String content = getPlantContent(event);
        if (content != null) {
            for (String keyword : keywords) {
                if (content.startsWith(keyword)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 发送消息，子类可以提前发送消息，而不必等到由 getReplyMessage 方法被调用，请注意，即使子类提前发送消息，getReplyMessage 仍然会被调用，不过子类可以在 getReplyMessage 方法内返回 null 值以表示不发送消息
     *
     * @param msg   消息链
     * @param event 消息事件
     * @return
     */
    protected void sendMsg(List<MessageChain> msg, MessageEvent event) throws CanNotSendMessageException {
        sendMsg(msg, event.getSubject());
    }


    /**
     * 发送消息，子类可以提前发送消息，而不必等到由 getReplyMessage 方法被调用，请注意，即使子类提前发送消息，getReplyMessage 仍然会被调用，不过子类可以在 getReplyMessage 方法内返回 null 值以表示不发送消息
     *
     * @param msg   消息链
     * @param event 消息事件
     * @return
     */
    protected void sendMsg(MessageChain msg, MessageEvent event) throws CanNotSendMessageException {
        sendMsg(List.of(msg), event.getSubject());
    }


    /**
     * 发送消息，子类可以提前发送消息，而不必等到由 getReplyMessage 方法被调用，请注意，即使子类提前发送消息，getReplyMessage 仍然会被调用，不过子类可以在 getReplyMessage 方法内返回 null 值以表示不发送消息
     *
     * @param msg     消息链
     * @param contact 发送对象
     * @return
     */
    protected void sendMsg(List<MessageChain> msg, Contact contact) throws CanNotSendMessageException {
        reply(msg, contact);
    }


    /**
     * 发送一条将自动撤回的消息，子类可以提前发送消息，而不必等到由 getReplyMessage 方法被调用，请注意，即使子类提前发送消息，getReplyMessage 仍然会被调用，不过子类可以在 getReplyMessage 方法内返回 null 值以表示不发送消息
     *
     * @param msg        消息
     * @param contact    发送对象
     * @param autoRecall 自动撤回等待时间(毫秒)
     * @return
     */
    protected void sendMsg(MessageChain msg, Contact contact, long autoRecall) throws CanNotSendMessageException {
        try {
            contact.sendMessage(msg).recallIn(autoRecall);
        } catch (Exception e) {
            throw new CanNotSendMessageException(e.getMessage());
        }
    }


    /**
     * 提交一条将要发送的消息，此消息将在 waitTime 毫秒后自动发送
     *
     * @param msg      消息
     * @param contact  发送对象
     * @param waitTime 将要等待的事件
     * @return 返回 future，可以调用 future.cancel 以取消事件
     */
    protected ScheduledFuture submitSendMsgTask(MessageChain msg, Contact contact, long waitTime) throws CanNotSendMessageException {
        return service.schedule(() -> {
            try {
                sendMsg(Collections.singletonList(msg), contact);
            } catch (CanNotSendMessageException e) {
                e.printStackTrace();
            }
        }, waitTime, TimeUnit.MILLISECONDS);
    }


    /**
     * 提交一条周期性发送的消息，初始等待时间为 initTime 毫秒，周期为 waitTime 毫秒
     *
     * @param msg       消息
     * @param contact   发送对象
     * @param initTTime 初始等待时间
     * @param waitTime  周期时间
     * @return 返回 future，可以调用 future.cancel 以取消事件
     */
    protected ScheduledFuture submitSendMsgTaskAtFixRate(MessageChain msg, Contact contact, long initTTime, long waitTime) throws CanNotSendMessageException {
        return service.scheduleAtFixedRate(() -> {
            try {
                sendMsg(Collections.singletonList(msg), contact);
            } catch (CanNotSendMessageException e) {
                e.printStackTrace();
            }
        }, initTTime, waitTime, TimeUnit.MILLISECONDS);
    }


    /**
     * 全局可调用的通用 API，用于消息处理失败时记录日志
     *
     * @param event
     * @param errorMsg
     */
    public static void failApi(MessageEvent event, String errorMsg) {
        AbstractMessageHandler.service.execute(() -> {
            String filePath = ConfigUtil.getDataFilePath("error.log");
            try {
                NetUtil.writeToFile(new File(filePath), getLog(event) + "\n：" + errorMsg + "\n");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    protected static String getLog(MessageEvent event) {
        if (event == null) return "";
        String content = getContent(event);
        String sender = getSenderId(event);
        return "[" + sender + "::" + formatTime() + "] -> " + content;
    }

    protected static String formatTime() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }


    /**
     * 判断给定消息事件是否引用了一条消息
     * @param event
     * @return
     */
    protected boolean hasQuote(MessageEvent event) {
        return event.getMessage().get(QuoteReply.Key) != null;
    }

    /**
     * 从消息事件中取出事件所引用的消息
     * @param event
     * @return
     * @throws NullPointerException 如果消息事件不包含引用
     * @see #hasQuote(MessageEvent) 
     */
    protected MessageChain getQuoteMessageChain(MessageEvent event) {
        return getQuoteSource(event).getOriginalMessage();
    }
}
