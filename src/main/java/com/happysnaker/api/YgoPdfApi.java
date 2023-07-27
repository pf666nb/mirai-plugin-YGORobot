package com.happysnaker.api;

import com.happysnaker.utils.IOUtil;

import java.io.IOException;
import java.net.URL;


import java.io.File;
import java.util.HashMap;

public class YgoPdfApi {


    public static final String api = "http://1.117.84.152:8080/test/upload";


    public static String getPdf(File file) throws IOException {
        URL url = new URL(api);
        String post = IOUtil.sendAndGetResponseString(url, "POST", new HashMap<>(), "111", 1000);
        return "";
    }
}
