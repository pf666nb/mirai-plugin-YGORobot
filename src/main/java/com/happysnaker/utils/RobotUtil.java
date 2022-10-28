package com.happysnaker.utils;

import com.happysnaker.cron.PeriodCronJob;
import com.happysnaker.cron.RobotCronJob;
import com.happysnaker.exception.CanNotSendMessageException;
import com.happysnaker.exception.FileUploadException;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;

import javax.naming.CannotProceedException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Happysnaker
 * @description
 * @date 2022/6/30
 * @email happysnaker@foxmail.com
 */
public class RobotUtil {

    /**
     * 读取机器人所有的群
     *
     * @return
     */
    public static Set<String> getBotsAllGroupId() {
        List<Bot> bots = Bot.getInstances();
        Set<String> ans = new HashSet<>();
        for (Bot bot : bots) {
            for (Group group : bot.getGroups()) {
                ans.add(String.valueOf(group.getId()));
            }
        }
        return ans;
    }

    /**
     * 对消息中的 {face:num} 表情进行解析，并将表情用实际的 Face 类替代，封装进 MessageChain 中，MessageChain 中仍然保持原消息中表情和其他消息的相对位置
     *
     * @param text 要解析的文本
     * @return MessageChain
     */
    public static MessageChain replaceFaceFromContent(String text) {
        String regex = "\\{face:\\d+\\}";
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
    public static String getContent(MessageEvent event) {
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
    public static String getContent(MessageChain chain) {
        if (chain == null) {
            return null;
        }
        return chain.serializeToMiraiCode();
    }

    /**
     * <P>某些时候需要自定义上传一些图片，可以调用此方法获取 Bot 自身的 Contact</P>
     * <P>如果没有机器人登录，这个方法会返回 NULL</P>
     */
    public static net.mamoe.mirai.contact.Contact getAdaptContact() {
        for (Bot bot : Bot.getInstances()) {
            return bot.getFriend(bot.getId());
        }

        return null;
    }

    /**
     * 从 mirai 编码转换为 MessageChain，并解析 HRobot 自带标签
     *
     * @param content
     * @return
     */
    public static MessageChain parseMiraiCode(String content) throws CannotProceedException {
        return parseMiraiCode(content, getAdaptContact());
    }

    /**
     * 从 mirai 编码转换为 MessageChain，并解析 HRobot 自带标签
     *
     * @param content 编码内容
     * @param contact HRobot 某些标签（例如图片）需要上传，使用的上传对象
     * @return MessageChain
     */
    public static MessageChain parseMiraiCode(String content, Contact contact) throws CannotProceedException {
        Pattern pattern = Pattern.compile("(\\[\\$.*?])(\\((.*?)\\))");
        Matcher matcher = pattern.matcher(content);
        int fromIndex = 0;
        MessageChainBuilder builder = new MessageChainBuilder();
        while (matcher.find()) {
            String tag = matcher.group(1);
            String val = matcher.group(2);
            tag = tag.substring(2, tag.length() - 1);
            val = val.substring(1, val.length() - 1);
            if (tag.isEmpty() || val.isEmpty()) {
                continue;
            }
            builder.append(MiraiCode.deserializeMiraiCode(content.substring(fromIndex, matcher.start())));
            fromIndex = matcher.end();
            try {
                switch (tag) {
                    case "img":
                        if (val.startsWith("http")) {
                            builder.append(uploadImage(contact, new URL(val)));
                        } else {
                            builder.append(uploadImage(contact, val));
                        }
                        break;
                    case "face":
                        break;
                    default:
                        builder.append(MiraiCode.deserializeMiraiCode(content.substring(matcher.start(), matcher.end())));
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new CannotProceedException(String.format("解析语义标签 %s 出错，异常原因 %s, 可能是网络超时或者值的格式不正确"
                        , matcher.group(0), e.getCause()));
            }
        }
        builder.append(MiraiCode.deserializeMiraiCode(content.substring(fromIndex)));
        return builder.build();
    }

    /**
     * 从事件中提取消息，该消息仅包含纯文本内容
     *
     * @param event
     * @return 纯文本
     * @see #getContent
     */
    public static String getOnlyPlainContent(MessageEvent event) {
        if (event == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
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
     * @param event    对应事件
     * @param filename 图片文件路径名
     * @return net.mamoe.mirai.message.data.Image
     */
    public static net.mamoe.mirai.message.data.Image uploadImage(MessageEvent event, String filename) throws FileUploadException {
        try {
            return ExternalResource.uploadAsImage(new File(filename), event.getSubject());
        } catch (Exception e) {
            throw new FileUploadException("Can not upload the image from the file: " + filename + "\nCause by " + e.getCause().toString());
        }
    }

    /**
     * 读取文件（图片）并上传至腾讯服务器
     *
     * @param contact  上传对象，上传对象可以是任意对象，仅上传并不会发送图片
     * @param filename 图片文件路径名
     * @return net.mamoe.mirai.message.data.Image
     */
    public static net.mamoe.mirai.message.data.Image uploadImage(Contact contact, String filename) throws FileUploadException {
        try {
            return ExternalResource.uploadAsImage(new File(filename), contact);
        } catch (Exception e) {
            throw new FileUploadException("Can not upload the image from the file: " + filename + "\nCause by " + e.getCause().toString());
        }
    }

    /**
     * 建造 MessageChain
     *
     * @param m 多个文本消息
     * @return 将多个文本消息结合成 MessageChain
     */
    public static MessageChain buildMessageChain(String... m) {
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
    public static MessageChain buildMessageChain(Object... m) {
        MessageChainBuilder builder = new MessageChainBuilder();
        for (Object s : m) {
            if (s == null) {
                continue;
            }
            if (s instanceof String) {
                s = new PlainText((CharSequence) s);
            }
            if (s instanceof StringBuilder) {
                s = new PlainText((CharSequence) s.toString());
            }
            if (s instanceof SingleMessage) {
                builder.append((SingleMessage) s);
            }
        }
        return builder.build();
    }

    /**
     * 建造 MessageChain，参数是多个 SingleMessage
     *
     * @param m 多个 SingleMessage
     * @return 将多个 SingleMessage 组合成 MessageChain List，List 的大小只为 1
     */
    public static List<MessageChain> buildMessageChainAsSingletonList(Object... m) {
        return OfUtil.ofList(buildMessageChain(m));
    }

    /**
     * 建造 MessageChainList，参数是多个 MessageChain
     *
     * @param m 多个 MessageChain
     * @return 将多个 MessageChain 组合成多条消息 MessageChain
     */
    public static List<MessageChain> buildMessageChainAsList(MessageChain... m) {
        return OfUtil.ofList(m);
    }

    /**
     * 获取发送者的 QQ
     *
     * @param event
     * @return
     */
    public static String getSenderId(MessageEvent event) {
        return String.valueOf(event.getSender().getId());
    }

    public static long getSenderId2(MessageEvent event) {
        return event.getSender().getId();
    }

    /**
     * 网络图片并上传至腾讯服务器
     *
     * @param event
     * @param url   网络图片 URL
     * @return net.mamoe.mirai.message.data.Image
     */
    public static net.mamoe.mirai.message.data.Image uploadImage(MessageEvent event, URL url) throws FileUploadException {
        return uploadImage(event.getSubject(), url);
    }

    /**
     * 网络图片并上传至腾讯服务器
     *
     * @param contact 要发送的对象，仅会上传而不会实际发送
     * @param url     网络图片 URL
     * @return net.mamoe.mirai.message.data.Image
     */
    public static net.mamoe.mirai.message.data.Image uploadImage(Contact contact, URL url) throws FileUploadException {
        try (InputStream stream = IOUtil.sendAndGetResponseStream(
                url,
                "GET",
                null,
                null
        )) {
            return Contact.uploadImage(contact, stream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileUploadException("Can not upload the image from the url: " + url + ", cause by " + e.getCause().toString());
        }
    }

    /**
     * 获取引用消息事件的源，如果不存在，则返回 null
     *
     * @param event
     * @return MessageSource
     */
    public static MessageSource getQuoteSource(MessageEvent event) {
        return Objects.requireNonNull(event.getMessage().get(QuoteReply.Key)).getSource();
    }

    /**
     * 设置引用回复，如果失败，则返回 null<br/>
     * 如果想回复某消息，你可以这样做：chainBuilder.append(getQuoteReply(e))<br/>或者调用父类方法：buildMessageChain(getQuoteReply(e), msg) 以构造一条消息链<br/>或者使用 getQuoteReply 方法回复一条简单文本信息
     *
     * @param event
     * @return MessageSource
     * @see #buildMessageChain(Object...)
     * @see #quoteReply(MessageEvent, String)
     */
    public static QuoteReply getQuoteReply(MessageEvent event) {
        return new QuoteReply(event.getMessage());
    }

    /**
     * 简单回复一条字符串消息
     *
     * @param event
     * @param msg
     * @return
     */
    public static MessageChain quoteReply(MessageEvent event, String msg) {
        return buildMessageChain(getQuoteReply(event), msg);
    }

    /**
     * 获取一个消息链中的 Images
     *
     * @param chain
     * @return List&lt;SingleMessage&gt;，可以将 SingleMessage 强转为 Image 类
     */
    public static List<Image> getImagesFromMessage(MessageChain chain) {
        return chain.stream().filter(Image.class::isInstance).map(v -> (Image) v).collect(Collectors.toList());
    }

    /**
     * 发送消息，子类可以提前发送消息，而不必等到由 getReplyMessage 方法被调用，请注意，即使子类提前发送消息，getReplyMessage 仍然会被调用，不过子类可以在 getReplyMessage 方法内返回 null 值以表示不发送消息
     *
     * @param msg   消息链
     * @param event 消息事件
     * @return
     */
    public static void sendMsg(List<MessageChain> msg, MessageEvent event) throws CanNotSendMessageException {
        sendMsg(msg, event.getSubject());
    }

    /**
     * 发送消息，子类可以提前发送消息，而不必等到由 getReplyMessage 方法被调用，请注意，即使子类提前发送消息，getReplyMessage 仍然会被调用，不过子类可以在 getReplyMessage 方法内返回 null 值以表示不发送消息
     *
     * @param msg   消息链
     * @param event 消息事件
     * @return
     */
    public static void sendMsg(MessageChain msg, MessageEvent event) throws CanNotSendMessageException {
        sendMsg(OfUtil.ofList(msg), event.getSubject());
    }

    /**
     * 发送多条消息，<strong>注意此方法并不能保证发送消息的顺序<strong/>
     *
     * @param msg     消息链
     * @param contact 发送对象
     * @return
     */
    public static void sendMsg(List<MessageChain> msg, Contact contact) throws CanNotSendMessageException {
        try {
            for (MessageChain chain : msg) {
                contact.sendMessage(chain);
            }
        } catch (Exception e) {
            throw new CanNotSendMessageException(e.getMessage());
        }
    }

    /**
     * 发送一条将自动撤回的消息，子类可以提前发送消息，而不必等到由 getReplyMessage 方法被调用，请注意，即使子类提前发送消息，getReplyMessage 仍然会被调用，不过子类可以在 getReplyMessage 方法内返回 null 值以表示不发送消息
     *
     * @param msg        消息
     * @param contact    发送对象
     * @param autoRecall 自动撤回等待时间(毫秒)
     * @return
     */
    public static void sendMsg(MessageChain msg, Contact contact, long autoRecall) throws CanNotSendMessageException {
        try {
            contact.sendMessage(msg).recallIn(autoRecall);
        } catch (Exception e) {
            throw new CanNotSendMessageException(e.getMessage());
        }
    }

    /**
     * 发送一条将自动撤回的消息，子类可以提前发送消息，而不必等到由 getReplyMessage 方法被调用，请注意，即使子类提前发送消息，getReplyMessage 仍然会被调用，不过子类可以在 getReplyMessage 方法内返回 null 值以表示不发送消息
     *
     * @param msg        消息
     * @param e          消息事件
     * @param autoRecall 自动撤回等待时间(毫秒)
     * @return
     */
    public static void sendMsg(MessageChain msg, MessageEvent e, long autoRecall) throws CanNotSendMessageException {
        try {
            e.getSubject().sendMessage(msg).recallIn(autoRecall);
        } catch (Exception ee) {
            throw new CanNotSendMessageException(ee.getMessage());
        }
    }

    /**
     * 提交一条基于 cron 定时发送的消息
     *
     * @param cronExpression cron 表达式
     * @param messages       消息列表，会随机选择一条消息，消息为 mirai 编码
     * @param count          执行次数
     * @throws CanNotSendMessageException
     */
    public static void submitSendMsgTask(String cronExpression, int count, List<String> messages, Contact contact) throws CanNotSendMessageException, SchedulerException, InterruptedException {
        JobDataMap jobData = PeriodCronJob.PeriodCronJobData.getJobDataMap(count, messages, contact);
        RobotCronJob.submitCronJob(PeriodCronJob.class,
                CronScheduleBuilder.cronSchedule(cronExpression), jobData);
    }

    /**
     * 提交一条每天定时发送的消息
     *
     * @param hour     0-23
     * @param minute   0-59
     * @param count    需要执行几次，大于等于 1
     * @param messages 消息列表，机器人会随机选择一条消息发送，消息为 mirai 编码
     * @throws CanNotSendMessageException
     */
    public static void submitSendMsgTask(int hour, int minute, int count, List<String> messages, Contact contact) throws Exception {
        JobDataMap jobData = PeriodCronJob.PeriodCronJobData.getJobDataMap(count, messages, contact);
        RobotCronJob.submitCronJob(PeriodCronJob.class,
                CronScheduleBuilder.dailyAtHourAndMinute(hour, minute), jobData);
    }

    /**
     * 提交一条将要发送的消息，此消息将在 waitTime 毫秒后自动发送
     *
     * @param msg      消息
     * @param contact  发送对象
     * @param waitTime 将要等待的事件
     * @return 返回 future，可以调用 future.cancel 以取消事件
     */
    public static void submitSendMsgTask(MessageChain msg, Contact contact, long waitTime) throws CanNotSendMessageException {
        RobotCronJob.service.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    sendMsg(Collections.singletonList(msg), contact);
                } catch (CanNotSendMessageException e) {
                    e.printStackTrace();
                }
            }
        }, waitTime);
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
    public static void submitSendMsgTaskAtFixRate(MessageChain msg, Contact contact, long initTTime, long waitTime) throws CanNotSendMessageException {
        RobotCronJob.service.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    sendMsg(Collections.singletonList(msg), contact);
                } catch (CanNotSendMessageException e) {
                    e.printStackTrace();
                }
            }
        }, initTTime, waitTime);
    }

    /**
     * 判断给定消息事件是否引用了一条消息
     *
     * @param event
     * @return
     */
    public static boolean hasQuote(MessageEvent event) {
        return event.getMessage().get(QuoteReply.Key) != null;
    }

    /**
     * 从消息事件中取出事件所引用的消息
     *
     * @param event
     * @return
     * @throws NullPointerException 如果消息事件不包含引用
     * @see #hasQuote(MessageEvent)
     */
    public static MessageChain getQuoteMessageChain(MessageEvent event) {
        return getQuoteSource(event).getOriginalMessage();
    }

    public static boolean equals(MessageSource source1, MessageSource source2) {
        if (source1 == null && source2 == null) return true;
        if (source1 == null || source2 == null) return false;
        return source1.getFromId() == source2.getFromId()
                && source1.getTargetId() == source2.getTargetId()
                && source1.getTime() == source2.getTime();
    }

    @Deprecated
    public static List<MessageChain> doHelp(MessageEvent event) throws MalformedURLException, FileUploadException {
        event.getSubject().sendMessage("正在上传帮助图片，此操作可能较慢，请稍等。如此操作出错，请前往 https://github.com/happysnaker/mirai-plugin-HRobot 查阅相关信息");
        // 版本变更
        Image image0 = uploadImage(event, new URL("https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora/image-20220228125440601.png"));

        List<MessageChain> ans = new ArrayList<>();
        ans.add(buildMessageChain("—— HRobot v2.0-beta ——\n", image0));

        String desc1 = "以下为表格关键字及示例，部分关键字需要 @ 机器人才有效。\n" +
                "主页地址：https://github.com/happysnaker/mirai-plugin-HRobot\n";
        Image image1 = uploadImage(event, new URL("https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora/image-20220228130359523.png"));
        Image image2 = uploadImage(event, new URL("https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora/image-20220228131341431.png"));
        Image image3 = uploadImage(event, new URL("https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora/image-20220228131533107.png"));
        ans.add(buildMessageChain(desc1, image1, image2, image3));

        String desc2 = "以下为表格命令及示例，发送对应命令到群内即可识别，命令必须要以特殊前缀 # 开头。\n";
        Image image4 = uploadImage(event, new URL("https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora/image-20220228131050819.png"));
        Image image5 = uploadImage(event, new URL("https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora/image-20220228131128660.png"));
        Image image6 = uploadImage(event, new URL("https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora/image-20220228131229491.png"));
        ans.add(buildMessageChain(desc2, image4, image5, image6));

        String desc3 = "如果相关 BUG 或有更好的创意，请于 https://github.com/happysnaker/mirai-plugin-HRobot/issues 提出相关 ISSUE\n";
        ans.add(buildMessageChain((Object) desc3));
        return ans;
    }
}
