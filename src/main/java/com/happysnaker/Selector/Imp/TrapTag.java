package com.happysnaker.Selector.Imp;


import com.happysnaker.CardEnum.CardType;
import com.happysnaker.Selector.TagSelector;
import com.happysnaker.entry.DataBean;
import com.happysnaker.utils.GetBeanUtil;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * gecco
 * 陷阱卡的数据检索
 *
 * @author : wpf
 * @date : 2023-08-03 14:44
 **/
public class TrapTag implements TagSelector {
    public final  String TAG_XPATH = "//*[@id=\"app\"]/div[2]/div/div/div[1]/div[2]/ul/li[4]/a";
    public final  String TAG = "trap";

    public final  String TD = "td";

    public final  String TR = "tr";
    @Override
    public void SelectTag(HtmlPage page) {
        List<DomElement> byXPath = page.getByXPath(TAG_XPATH);
        try {
            byXPath.get(0).click();
        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DataBean> getElements(HtmlPage page) {
        ArrayList<DataBean> list = new ArrayList<>();
        Document document = Jsoup.parse(page.asXml());
        List<Element> elementList = document.getElementById(TAG).getElementsByTag(TR);//获取元素节点等
        for (int i = 1; i < elementList.size(); i++) {
          list.add(  doTrParser(elementList.get(i)));
        }
        return list;
    }

    private DataBean doTrParser(Element element){
        Elements elementsByTag = element.getElementsByTag(TD);
        return  GetBeanUtil.build(elementsByTag, CardType.MONSTER);

    }
}
