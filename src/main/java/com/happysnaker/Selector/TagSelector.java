package com.happysnaker.Selector;


import com.happysnaker.entry.DataBean;
import org.htmlunit.html.HtmlPage;

import java.util.List;

public interface TagSelector {

    void SelectTag(HtmlPage page);

    List<DataBean> getElements(HtmlPage page);
}
