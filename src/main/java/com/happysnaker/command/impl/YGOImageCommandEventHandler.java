package com.happysnaker.command.impl;

import com.happysnaker.Selector.Imp.*;
import com.happysnaker.Selector.TagSelector;
import com.happysnaker.config.WebClientConfig;
import com.happysnaker.entry.DataBean;
import com.happysnaker.exception.CanNotParseCommandException;
import com.happysnaker.exception.InsufficientPermissionsException;
import com.happysnaker.handler.handler;
import com.happysnaker.utils.GenerateYgoPieChartUtil;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.htmlunit.Page;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;

import java.util.List;
@handler(priority = 1024)
public class YGOImageCommandEventHandler extends DefaultCommandEventHandlerManager{
    public static String refresh = "刷新饼图";

    public YGOImageCommandEventHandler() {
        super.registerKeywords(refresh);

    }
    @Override
    public List<MessageChain> parseCommand(MessageEvent event)  {
        String content = getPlantContent(event);

            if (content.equals(refresh)) {
                return doRefresh();

            }
        return null;


}

    private List<MessageChain> doRefresh() {
        buildPieChart(new MonsterTag(),"怪兽");
        buildPieChart(new DeckTag(),"卡组");
        buildPieChart(new SpellTag(),"魔法");
        buildPieChart(new TrapTag(),"陷阱");
        buildPieChart(new SideTag(),"SIDE");
        buildPieChart(new ExTag(),"额外");

        return buildMessageChainAsSingletonList("饼图刷新成功！");
    }

    private void buildPieChart(TagSelector tagSelector,String title){
        WebClient client = WebClientConfig.GetClient();

        HtmlPage page = null;
        try {
            page = client.getPage("https://mycard.moe/ygopro/arena/#/cards");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            client.close();
        }
        client.waitForBackgroundJavaScript(5000);
        tagSelector.SelectTag(page);
         List<DataBean> elements = tagSelector.getElements(page);
        try {
            GenerateYgoPieChartUtil.generateYgoPie(elements,title);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
