//package com.happysnaker.handler.impl;
//
//import com.happysnaker.handler.handler;
//import com.happysnaker.proxy.Context;
//import com.happysnaker.utils.RandomStringUtil;
//import net.mamoe.mirai.event.events.MessageEvent;
//import net.mamoe.mirai.message.data.MessageChain;
//import net.mamoe.mirai.message.data.MessageChainBuilder;
//import org.apache.http.HttpEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.util.EntityUtils;
//
//import java.io.*;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
///**
// *
// * 请不要再使用该功能，后端api已经移除，以后某个版本将会彻底移除这个功能
// *
// * */
//@handler(priority = 1)
//@Deprecated
//public class YgoPdfMessageEventHandler extends GroupMessageEventHandler{
//
//
//    public static final String YGOPDF = "YGOPDF";
//    private final Set<String> keywords = new HashSet<>();
//    public YgoPdfMessageEventHandler() {
//        keywords.add(YGOPDF);
//
//    }
//
//    @Override
//    public List<MessageChain> handleMessageEvent(MessageEvent event, Context ctx) {
//        String content = getPlantContent(event);
//        List<MessageChain> ans = new ArrayList<>();
//        try {
//            // 鸡汤
//
//
//            if (content.startsWith(YGOPDF)){
//                ans.add(doParseYgoPdf(event,content));
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            logError(event, e);
//            ans.add(new MessageChainBuilder().append("意外地失去了与地球上的通信...\n错误原因：").append(e.getMessage()).build());
//        }
//        return ans;
//    }
//
//    @Deprecated
//    private MessageChain doParseYgoPdf(MessageEvent event,String content) throws IOException {
//
//
//        String filename = RandomStringUtil.getRandomString(4);
//        File file = File.createTempFile(filename, ".ydk");
//        FileOutputStream outputStream = new FileOutputStream(file);
//        byte[] strToBytes = content.getBytes();
//        outputStream.write(strToBytes);
//        outputStream.close();
//        // Prepare URL for the file upload
//        String url = "http://1.117.84.152:8080/upload/test";
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        HttpPost post = new HttpPost(url);
//        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//        builder.addBinaryBody("file", file);
//        builder.addBinaryBody("en", "cn".getBytes());
//        HttpEntity entity = builder.build();
//        post.setEntity(entity);
//        CloseableHttpResponse response = httpclient.execute(post);
//        String responseString;
//        try {
//            System.out.println(response.getStatusLine());
//            HttpEntity responseEntity = response.getEntity();
//            responseString = EntityUtils.toString(responseEntity);
//
//        } finally {
//            response.close();
//            httpclient.close();
//        }
//
//
//        return new MessageChainBuilder()
//                .append("http://1.117.84.152:8080/pdf/" + responseString)
//                .build();
//    }
//
//    @Override
//    public boolean shouldHandle(MessageEvent event, Context ctx) {
//        return startWithKeywords(event, keywords);
//    }
//}
