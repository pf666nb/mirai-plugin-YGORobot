package com.happysnaker;

import com.happysnaker.entry.RowMapper;
import com.happysnaker.utils.SqliteHelper;
import it.grabz.grabzit.GrabzItClient;
import org.htmlunit.BrowserVersion;
import org.htmlunit.NicelyResynchronizingAjaxController;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author happysnaker
 * @date 2022/10/24
 * @email happysnaker@foxmail.com
 */
public class Test {

    public static void test(Object... args) throws Exception {

    }

    public static void main(String[] args) throws Exception {
//        SqliteHelper h = new SqliteHelper("/Users/apple/Desktop/mcl/config/com.happysnaker.HRobot/cards.cdb");
//        List<String> sList = h.executeQuery("select id from texts where name like '下%' ", new RowMapper<String>() {
//            @Override
//            public String mapRow(ResultSet rs, int index)
//                    throws SQLException {
//                return rs.getString("id");
//            }
//        });
//        System.out.println(sList.get(0));
//        GrabzItClient grabzIt = new GrabzItClient("ZDg4OGMyNzc3MTU1NDA5MTk1ZWI0Y2IzN2FmNzA1NGM=", "Pyg/Pz9zdQU/BT9AS00lCSQCPz5mPxQ/P08/PyFvSAQ=");
//        grabzIt.URLToImage("https://ygo.ygosgs.com/#/share/yugioh?language=sc&source=&font=&color=&align=left&gradient=false&gradientColor1=%23999999&gradientColor2=%23ffffff&descriptionZoom=1&descriptionWeight=0&password=89631139&copyright=&laser=&rare=&twentieth=false&radius=true");
//        grabzIt.SaveTo("/Users/apple/IdeaProjects/mirai-plugin-HRobot/images/result.jpg");

        final WebClient webClient = new WebClient(BrowserVersion.CHROME);//新建一个模拟谷歌Chrome浏览器的浏览器客户端对象

        webClient.getOptions().setThrowExceptionOnScriptError(false);//当JS执行出错的时候是否抛出异常, 这里选择不需要
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);//当HTTP的状态非200时是否抛出异常, 这里选择不需要
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setCssEnabled(false);//是否启用CSS, 因为不需要展现页面, 所以不需要启用
        webClient.getOptions().setJavaScriptEnabled(true); //很重要，启用JS
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());//很重要，设置支持AJAX

        HtmlPage page = null;
        try {
            page = webClient.getPage("https://ygo.ygosgs.com/#/share/yugioh?language=sc&source=&font=&color=&align=left&gradient=false&gradientColor1=%23999999&gradientColor2=%23ffffff&descriptionZoom=1&descriptionWeight=0&password=89631139&copyright=&laser=&rare=&twentieth=false&radius=true");//尝试加载上面图片例子给出的网页
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            webClient.close();
        }

        webClient.waitForBackgroundJavaScript(30000);//异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束
        Thread.sleep(9000);
        String pageXml = page.asXml();//直接将加载完成的页面转换成xml格式的字符串

        //TODO 下面的代码就是对字符串的操作了,常规的爬虫操作,用到了比较好用的Jsoup库

        Document document = Jsoup.parse(pageXml);//获取html文档
        System.out.println(document.toString());
    }
}
