package com.happysnaker;

import org.quartz.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class HelloJob implements Job {
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //工作内容
        System.out.println("context.getJobDetail().getKey() = " + context.getJobDetail().getKey());
        JobDataMap map = context.getJobDetail().getJobDataMap();
        ((Map) map.get("count")).put("count", System.currentTimeMillis());
        System.out.println(map.get("count"));

        System.out.println(Thread.currentThread());
    }
}