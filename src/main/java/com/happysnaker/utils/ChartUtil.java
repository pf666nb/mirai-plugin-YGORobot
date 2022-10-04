package com.happysnaker.utils;

import com.happysnaker.config.RobotConfig;
import com.happysnaker.cron.RobotCronJob;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.imageio.ImageIO;
import java.awt.*;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 图表实用类，为坎公骑冠剑生成会展报表，方法返回文件名
 * @author Happysnaker
 * @description 待重构
 * @date 2022/2/26
 * @email happysnaker@foxmail.com
 */
@Deprecated
public class ChartUtil {
    static {
        RobotConfig.logger.info("注册后台清理任务...");
        RobotCronJob.addCronTask(() -> {
            File file = new File(ConfigUtil.getDataFilePath("img"));
            if (!file.exists() || !file.isDirectory()) {
                file.mkdir();
            } else {
                for (File listFile : file.listFiles()) {
                    if (listFile.isFile()) {
                        listFile.delete();
                    }
                }
                RobotConfig.logger.info("已清理不要的图片");
            }
        });
    }

    public static final String directionPath = ConfigUtil.getDataFilePath("img/");

    public static String generateAPieChart(Map<String, Long> dataset, String title) throws IOException {
        try {
            //如果不使用Font,中文将显示不出来
            Font font = new Font("宋体", Font.BOLD, 15);

            DefaultPieDataset pds = new DefaultPieDataset();
            for (Map.Entry<String, Long> it : dataset.entrySet()) {
                pds.setValue(it.getKey(), it.getValue());
            }
            JFreeChart chart = ChartFactory.createPieChart(title, pds, true, false, true);
            //设置图片标题的字体
            chart.getTitle().setFont(font);

            //得到图块,准备设置标签的字体
            PiePlot plot = (PiePlot) chart.getPlot();

            //设置标签字体
            plot.setLabelFont(font);

            //设置图例项目字体
            chart.getLegend().setItemFont(font);

            plot.setStartAngle(3.14f / 2f);

            //设置plot的前景色透明度
            plot.setForegroundAlpha(0.7f);

            //设置plot的背景色透明度
            plot.setBackgroundAlpha(0.0f);

            //设置标签生成器(默认{0})
            //{0}:key {1}:value {2}:百分比 {3}:sum
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}({1})/{2}"));

            //将内存中的图片写到本地硬盘
            String path = directionPath + UUID.randomUUID() + ".jpg";
            ChartUtilities.saveChartAsJPEG(new File(path), chart, 800, 400);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String generateALineChart(List<Pair<String, List<Pair<String, Double>>>> datasets, String title, String rowTitle, String colTitle) throws IOException {
        try {
            //种类数据集
            DefaultCategoryDataset ds = new DefaultCategoryDataset();
            for (Pair<String, List<Pair<String, Double>>> dataset : datasets) {
                for (Pair<String, Double> it : dataset.getValue()) {
                    ds.setValue(it.getValue(), dataset.getKey(), it.getKey());
                }
            }

            Font font = new Font("宋体", Font.BOLD, 20);
            //创建柱状图
            JFreeChart chart = ChartFactory.createLineChart3D(title, rowTitle, colTitle, ds, PlotOrientation.VERTICAL, true, true, true);

            //设置整个图片的标题字体
            chart.getTitle().setFont(font);

            //设置提示条字体
            font = new Font("宋体", Font.BOLD, 15);
            chart.getLegend().setItemFont(font);

            //得到绘图区
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            //得到绘图区的域轴(横轴),设置标签的字体
            plot.getDomainAxis().setLabelFont(font);

            //设置横轴标签项字体
            plot.getDomainAxis().setTickLabelFont(font);

            //设置范围轴(纵轴)字体
            plot.getRangeAxis().setLabelFont(font);
            //存储成图片
            try {
                chart.setBackgroundImage(ImageIO.read(new URL("https://img.ixintu.com/download/jpg/201911/d57bee34508e4424145f0a376445edf0.jpg?x-oss-process=image/crop,x_0,y_0,w_960,h_1025")));
//            chart.setBackgroundImage(ImageIO.read(new File("D:/JFreeChart/背景图-白色.jpg")));

                plot.setBackgroundImage(ImageIO.read(new URL("https://tse1-mm.cn.bing.net/th/id/R-C.055d9409e760c9b7d312035935ec9dd3?rik=oKLKEIOJLEjiaw&riu=http%3a%2f%2fpic31.photophoto.cn%2f20140613%2f0008020944439977_b.jpg&ehk=3JoY27qXd1Os6r9qPinZtuhBDtGL%2bCK9ZKVfG9tRbkU%3d&risl=&pid=ImgRaw&r=0")));

            } catch (Exception e) {

            }
            plot.setForegroundAlpha(1.0f);
            String path = directionPath + UUID.randomUUID() + ".jpg";
            ChartUtilities.saveChartAsJPEG(new File(path), chart, 800, 400);
            return path;
        } catch (Exception e) {
            throw e;
        }
    }


    public static String generateHistogram(List<Pair<String, List<Pair<String, Double>>>> datasets, String title, String rowTitle, String colTitle) throws IOException {
        try {
            //种类数据集
            DefaultCategoryDataset ds = new DefaultCategoryDataset();

            for (Pair<String, List<Pair<String, Double>>> dataset : datasets) {
                for (Pair<String, Double> it : dataset.getValue()) {
                    ds.setValue(it.getValue(), dataset.getKey(), it.getKey());
                }
            }

            Font font = new Font("宋体", Font.BOLD, 20);
            //创建柱状图,柱状图分水平显示和垂直显示两种
            JFreeChart chart = ChartFactory.createBarChart(title, rowTitle, colTitle, ds, PlotOrientation.VERTICAL, true, true, true);

            //设置整个图片的标题字体
            chart.getTitle().setFont(font);

            //设置提示条字体
            font = new Font("宋体", Font.BOLD, 15);
            chart.getLegend().setItemFont(font);

            //得到绘图区
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            //得到绘图区的域轴(横轴),设置标签的字体
            plot.getDomainAxis().setLabelFont(font);

            //设置横轴标签项字体
            plot.getDomainAxis().setTickLabelFont(font);

            //设置范围轴(纵轴)字体
            plot.getRangeAxis().setLabelFont(font);
            //存储成图片

//            chart.setBackgroundImage(ImageIO.read(new URL("https://img.ixintu.com/download/jpg/201911/d57bee34508e4424145f0a376445edf0.jpg?x-oss-process=image/crop,x_0,y_0,w_960,h_1025")));
////            chart.setBackgroundImage(ImageIO.read(new File("D:/JFreeChart/背景图-白色.jpg")));
//
//            plot.setBackgroundImage(ImageIO.read(new URL("https://tse1-mm.cn.bing.net/th/id/R-C.055d9409e760c9b7d312035935ec9dd3?rik=oKLKEIOJLEjiaw&riu=http%3a%2f%2fpic31.photophoto.cn%2f20140613%2f0008020944439977_b.jpg&ehk=3JoY27qXd1Os6r9qPinZtuhBDtGL%2bCK9ZKVfG9tRbkU%3d&risl=&pid=ImgRaw&r=0")));

            plot.setForegroundAlpha(1.0f);
            String path = directionPath + UUID.randomUUID() + ".jpg";
            ChartUtilities.saveChartAsJPEG(new File(path), chart, 800, 500);
            return path;
        } catch (Exception e) {
            throw e;
//            e.printStackTrace();
        }

    }
}
