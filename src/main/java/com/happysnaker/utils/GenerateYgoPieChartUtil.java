package com.happysnaker.utils;


import com.happysnaker.config.RobotConfig;
import com.happysnaker.entry.DataBean;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author apple
 */
public class GenerateYgoPieChartUtil {


    //饼图生成的本地地址
    private static final String imagePath = RobotConfig.configFolder+"";

    public static  void generateYgoPie(List<DataBean> beanList,String title) throws Exception {
        //获取百分比的集合
        List<String> collect = beanList.stream().map(DataBean::getAttribute2).collect(Collectors.toList());

        //获取名字的集合
        List<String> name = beanList.stream().map(DataBean::getName).collect(Collectors.toList());

        //删除对应%然后再放回去
        List<Object> collects = collect.stream().map(s -> s.substring(0, s.length() - 1)).collect(Collectors.toList());

        //处理集合，只取前5
        List<Object> values = collects.subList(0, 5);
        List<String> keys = name.subList(0, 5);
        //计算百分比
        double other = 100.0;
        for (Object s : collects) {
            String str = (String) s;
            //计算其他的占比，
            other -= Double.parseDouble(str.substring(0, str.length() - 1));
        }
        keys.add("其他");
        values.add(other);

        //颜色list
        List<Color> legendColorList = new ArrayList<>(Arrays.asList(Color.YELLOW, Color.GRAY, Color.green, Color.cyan, Color.ORANGE));


        List<Double> explodePercentList = new ArrayList<>(Arrays.asList(0.1, 0.1, 0.1, 0.1, 0.1));
        JFreeChart chart = GeneratePieChartUtil.createPieChart("YGO今日饼图", keys, values
                , JFreeChartUtil.createChartTheme("宋体"), legendColorList, explodePercentList);
        //在本地目录下生成图片
        File p = new File(imagePath);
        if (!p.exists()) {
            p.mkdirs();
        }
        String imageName = "YGO"+ title +"饼图.jpeg";
        File file = new File(p.getPath() + "/pic/" + imageName);
        try {
            if(file.exists()) {
                file.delete();
            }
            ChartUtils.saveChartAsJPEG(file, chart, 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
