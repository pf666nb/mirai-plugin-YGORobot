package com.happysnaker;


import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.happysnaker.Selector.Imp.*;
import com.happysnaker.config.WebClientConfig;
import com.happysnaker.entry.DataBean;
import com.happysnaker.utils.GenerateYgoPieChartUtil;
import org.htmlunit.BrowserVersion;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;

import java.util.HashMap;
import java.util.List;

/**
 * @author happysnaker
 * @date 2022/10/24
 * @email happysnaker@foxmail.com
 */
public class Test {
    private  static HashMap<String,Integer> todayMap = new HashMap<>();
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

//    public static void main(String[] args) {
//        // 这里举例找了个vue页面（小牛翻译、b站页面）的来测试
//        String nowHtml = "https://kooriookami.github.io/yugioh-card/";
////		String nowHtml = "https://www.bilibili.com";
//        getWebBody(nowHtml);
//    }
    public static void getWebBody(String nowHtml) {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setActiveXNative(false);// 不启用ActiveX
        webClient.getOptions().setCssEnabled(false);// 是否启用CSS，因为不需要展现页面，所以不需要启用
        webClient.getOptions().setUseInsecureSSL(true); // 设置为true，客户机将接受与任何主机的连接，而不管它们是否有有效证书
        webClient.getOptions().setJavaScriptEnabled(true); // 很重要，启用JS
        webClient.getOptions().setDownloadImages(false);// 不下载图片
        webClient.getOptions().setThrowExceptionOnScriptError(false);// 当JS执行出错的时候是否抛出异常，这里选择不需要
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);// 当HTTP的状态非200时是否抛出异常，这里选择不需要
        webClient.getOptions().setTimeout(15 * 1000); // 等待15s
        webClient.getOptions().setConnectionTimeToLive(15 * 1000);
        webClient.waitForBackgroundJavaScript(10 * 1000);// 异步JS执行需要耗时，所以这里线程要阻塞30秒，等待异步JS执行结束

        HtmlPage page = null;
        try {
            page = webClient.getPage(nowHtml);// 加载网页
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webClient.close();
        }
        String htmlStr = page.getBody().asXml();
        System.out.println(htmlStr);
    }

    public static void main(String[] args) throws Exception {
//        StringBuilder trueBuilder = new StringBuilder();
//        trueBuilder.append("宜：");
//        StringBuilder falseBuilder = new StringBuilder();
//        falseBuilder.append("禁：");
//        todayMap.forEach((k,v)->{
//            todayMap.put(k, RandomUtil.randomInt(0,2));
//        });
//        todayMap.forEach((k,v)->{
//            Integer i = todayMap.get(k);
//            if(i==1){
//                trueBuilder.append(k).append(",");
//            }else{
//                falseBuilder.append(k).append(",");
//            }
//        });
//        trueBuilder.append("\n");
//        System.out.print(trueBuilder.toString());
//        System.out.print(falseBuilder.toString());

//        System.out.println(  DateUtil.getZodiac(DateUtil.month(DateUtil.date()),DateUtil.dayOfMonth(DateUtil.date())));

//
//                String nowHtml = "https://kooriookami.github.io/yugioh-card/";
//
//        getWebBody(nowHtml);
        WebClient webClient = WebClientConfig.GetClient();
        HtmlPage page = null;
        try {
            page = webClient.getPage("https://mycard.moe/ygopro/arena/#/cards");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            webClient.close();
        }

        webClient.waitForBackgroundJavaScript(5000);//异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束
//        DeckTag deckTag = new DeckTag();
//        deckTag.SelectTag(page);
//        deckTag.getElements(page);

//        MonsterTag monsterTag = new MonsterTag();
//        monsterTag.SelectTag(page);
//        monsterTag.getElements(page);

//        SpellTag spellTag = new SpellTag();
//        spellTag.SelectTag(page);
//        spellTag.getElements(page);

        ExTag trapTag = new ExTag();
        trapTag.SelectTag(page);
        List<DataBean> elements = trapTag.getElements(page);
        GenerateYgoPieChartUtil.generateYgoPie(elements,"额外");
    }





}
