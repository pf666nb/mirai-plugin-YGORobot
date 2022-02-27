package com.happysnaker.utils;

import com.alibaba.fastjson.JSONObject;
import sun.net.www.protocol.https.HttpsURLConnectionImpl;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Map;

/**
 * @author Happysnaker
 * @description
 * @date 2022/1/16
 * @email happysnaker@foxmail.com
 */
public class NetUtil {

    private static HttpURLConnection getUrlConnection(URL obj, String method, Map<String, String> heads, String postParam) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setRequestMethod(method.toUpperCase(Locale.ROOT));
        conn.setRequestProperty("Connection", "keep-Alive");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("User-Agent", "Happysnaker-HPRobot-v1.002");
        if (heads != null) {
            for (Map.Entry<String, String> it : heads.entrySet()) {
                conn.setRequestProperty(it.getKey(), it.getValue());
            }
        }
        conn.setInstanceFollowRedirects(true);
        conn.setConnectTimeout(3000);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.connect();
        if (postParam != null) {
            OutputStream out = conn.getOutputStream();
            out.write(postParam.getBytes());
            out.flush();
        }
        return conn;
    }

    /**
     * 发送 http 请求并获取回复信息，注意回复格式必须是 json，否则该函数无法正常工作
     *
     * @param obj       URL
     * @param method
     * @param heads
     * @param postParam body 参数，若无可填 null
     * @return JSON 对应的 map 对象
     * @throws IOException
     */
    public static Map<String, Object> sendAndGetResponseMap(URL obj, String method, Map<String, String> heads, String postParam) throws IOException {
        try {
            HttpURLConnection conn = getUrlConnection(obj, method, heads, postParam);
            int code = conn.getResponseCode();
            String msg = "";
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    msg += line + "\n";
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            Map map = JSONObject.parseObject(msg, Map.class);
            map.put("code", code);
            return map;
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 发送 http 请求并获取回复信息，注意回复格式为原始二进制流
     *
     * @param obj       URL
     * @param method
     * @param heads
     * @param postParam body 参数，若无可填 null
     * @return InputStream
     * @throws IOException
     */
    public static InputStream sendAndGetResponseStream(URL obj, String method, Map<String, String> heads, String postParam) throws IOException {
        try {
            HttpURLConnection conn = getUrlConnection(obj, method, heads, postParam);
            return conn.getInputStream();
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 发送 http 请求并获取回复信息，返回类型为 String 类型
     *
     * @param obj       URL
     * @param method
     * @param heads
     * @param postParam body 参数，若无可填 null
     * @return String
     * @throws IOException
     */
    public static String sendAndGetResponseString(URL obj, String method, Map<String, String> heads, String postParam) throws IOException {
        try {
            HttpURLConnection conn = getUrlConnection(obj, method, heads, postParam);
            String msg = "";
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    msg += line + "\n";
                }
            } finally {
                conn.disconnect();
            }
            return msg;
        } catch (Exception e) {
            throw e;
        }
    }

    public synchronized static void writeToFile(File file, String message) throws FileNotFoundException {
        try (FileOutputStream out = new FileOutputStream(file, true)) {
            out.write(message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
