package com.happysnaker.utils;import com.alibaba.fastjson.JSONObject;import com.happysnaker.config.Logger;import com.happysnaker.config.RobotConfig;import java.io.*;import java.net.HttpURLConnection;import java.net.URL;import java.nio.charset.StandardCharsets;import java.util.Locale;import java.util.Map;public class IOUtil {    private static HttpURLConnection getUrlConnection(URL obj, String method, Map<String, String> heads, String postParam, long timeout) throws IOException {        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();        conn.setRequestMethod(method.toUpperCase(Locale.ROOT));        conn.setRequestProperty("Connection", "keep-Alive");        conn.setRequestProperty("Content-Type", "application/json");        conn.setRequestProperty("User-Agent", "Happysnaker-HPRobot-v1.002");        if (heads != null) {            for (Map.Entry<String, String> it : heads.entrySet()) {                conn.setRequestProperty(it.getKey(), it.getValue());            }        }        conn.setInstanceFollowRedirects(true);        if (timeout > 0) {            conn.setConnectTimeout((int) timeout);        }        conn.setDoOutput(true);        conn.setDoInput(true);        conn.connect();        if (postParam != null) {            OutputStream out = conn.getOutputStream();            out.write(postParam.getBytes());            out.flush();        }        return conn;    }    /**     * 发送 http 请求并获取回复信息，注意回复格式必须是 json，否则该函数无法正常工作     *     * @param obj       URL     * @param method     * @param heads     * @param postParam body 参数，若无可填 null     * @return JSON 对应的 map 对象     * @throws IOException     */    public static Map<String, Object> sendAndGetResponseMap(URL obj, String method, Map<String, String> heads, String postParam, long timeout) throws IOException {        try {            HttpURLConnection conn = getUrlConnection(obj, method, heads, postParam, timeout);            int code = conn.getResponseCode();            StringBuilder msg = new StringBuilder();            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {                String line = null;                while ((line = reader.readLine()) != null) {                    msg.append(line).append("\n");                }            } catch (Exception e) {                e.printStackTrace();            } finally {                conn.disconnect();            }            Map map = JSONObject.parseObject(msg.toString(), Map.class);            map.put("code", code);            return map;        } catch (Exception e) {            throw e;        }    }    /**     * 发送 http 请求并获取回复信息，注意回复格式必须是 json，否则该函数无法正常工作，默认的超时时间为配置所指定的     *     * @param obj       URL     * @param method     * @param heads     * @param postParam body 参数，若无可填 null     * @return JSON 对应的 map 对象     * @throws IOException     */    public static Map<String, Object> sendAndGetResponseMap(URL obj, String method, Map<String, String> heads, String postParam) throws IOException {        return sendAndGetResponseMap(obj, method, heads, postParam, RobotConfig.timeout);    }    /**     * 发送 http 请求并获取回复信息，注意回复格式必须是 json，否则该函数无法正常工作，默认的超时时间为配置所指定的     *     * @param obj       URL     * @param method     * @param heads     * @param postParam body 参数，若无可填 null     * @return JSON 对应的 map getter 对象     * @throws IOException     */    public static MapGetter sendAndGetResponseMapGetter(URL obj, String method, Map<String, String> heads, String postParam) throws IOException {        return new MapGetter(sendAndGetResponseMap(obj, method, heads, postParam, RobotConfig.timeout));    }    /**     * 发送 http 请求并获取回复信息，注意回复格式为原始二进制流     *     * @param obj       URL     * @param method     * @param heads     * @param postParam body 参数，若无可填 null     * @return InputStream     * @throws IOException     */    public static InputStream sendAndGetResponseStream(URL obj, String method, Map<String, String> heads, String postParam, long timeout) throws IOException {        try {            HttpURLConnection conn = getUrlConnection(obj, method, heads, postParam, timeout);            return conn.getInputStream();        } catch (Exception e) {            throw e;        }    }    /**     * 发送 http 请求并获取回复信息，注意回复格式为原始二进制流，默认的超时时间为配置所指定的     *     * @param obj       URL     * @param method     * @param heads     * @param postParam body 参数，若无可填 null     * @return InputStream     * @throws IOException     */    public static InputStream sendAndGetResponseStream(URL obj, String method, Map<String, String> heads, String postParam) throws IOException {        return sendAndGetResponseStream(obj, method, heads, postParam, RobotConfig.timeout);    }    /**     * 发送 http 请求并获取回复信息，返回类型为 String 类型     *     * @param obj       URL     * @param method     * @param heads     * @param postParam body 参数，若无可填 null     * @return String     * @throws IOException     */    public static String sendAndGetResponseString(URL obj, String method, Map<String, String> heads, String postParam, long timeout) throws IOException {        HttpURLConnection conn = getUrlConnection(obj, method, heads, postParam, timeout);        StringBuilder msg = new StringBuilder();        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {            String line = null;            while ((line = reader.readLine()) != null) {                msg.append(line).append("\n");            }        } finally {            conn.disconnect();        }        Logger.debug("Send request to " + obj + " and get response " + msg);        return msg.toString();    }    /**     * 发送 http 请求并获取回复信息，返回类型为 String 类型，默认的超时时间为配置所指定的     *     * @param obj       URL     * @param method     * @param heads     * @param postParam body 参数，若无可填 null     * @return String     * @throws IOException     */    public static String sendAndGetResponseString(URL obj, String method, Map<String, String> heads, String postParam) throws IOException {        return sendAndGetResponseString(obj, method, heads, postParam, RobotConfig.timeout);    }    /**     * 写入 string 内容到文件中，此方法将追加写     *     * @param file     * @param message     * @throws FileNotFoundException     */    public synchronized static void writeToFile(File file, String message) throws FileNotFoundException {        writeToFile(file, message, true);    }    /**     * 写入 string 内容到文件中     *     * @param file     * @param message     * @throws FileNotFoundException     */    public synchronized static void writeToFile(File file, String message, boolean append) throws FileNotFoundException {        try (FileOutputStream out = new FileOutputStream(file, append)) {            out.write(message.getBytes());        } catch (IOException e) {            e.printStackTrace();        }    }    /**     * 写入 字节流 内容到文件中     *     * @param file     * @param in     * @throws FileNotFoundException     */    public synchronized static void writeToFile(File file, InputStream in) throws FileNotFoundException {        try (FileOutputStream out = new FileOutputStream(file, true)) {            int b;            while ((b = in.read()) != -1) {                out.write(b);            }        } catch (IOException e) {            e.printStackTrace();        }    }    /**     * 读取文件，返回字符串     *     * @param file     * @throws FileNotFoundException     */    public synchronized static String readFromFile(File file) {        try (FileInputStream in = new FileInputStream(file)) {            BufferedReader reader = new BufferedReader(new InputStreamReader(in));            StringBuilder msg = new StringBuilder();            String line = null;            while ((line = reader.readLine()) != null) {                msg.append(line).append("\n");            }            return msg.toString();        } catch (IOException e) {            e.printStackTrace();        }        return null;    }    public synchronized static void replaceFile(File file, String seq, String target) throws FileNotFoundException {        String content = readFromFile(file);        assert content != null;        writeToFile(file, content.replaceAll(seq, target), false);    }}