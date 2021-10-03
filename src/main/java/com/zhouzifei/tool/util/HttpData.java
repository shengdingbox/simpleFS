package com.zhouzifei.tool.util;

import com.zhouzifei.tool.media.file.util.StreamUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class HttpData {

    /**
     * 直接获取接口返回值
     * @param urlPath 要访问的地址
     * @return
     */
    public static String getData(String urlPath, Map<String,String> hears,Integer readTime) {
        StringBuilder sb = new StringBuilder();
        try {
            // 统一资源
            URL url = new URL(urlPath);
            // 连接类的父类，抽象类
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpURLConnection connection = (HttpURLConnection) urlConnection;
            // 设定请求的方法，默认是GET
            connection.setRequestMethod("GET");
            if(null != readTime){
                connection.setReadTimeout(readTime);
            }
            // 设置字符编码
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            hears.forEach(connection::setRequestProperty);
//            connection.setInstanceFollowRedirects(false);
            // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
            connection.connect();
            //BufferedInputStream bin = new BufferedInputStream(connection.getInputStream());
            final InputStream inputStream = connection.getInputStream();
            final String string = StreamUtil.toString(inputStream, "UTF-8");
            connection.disconnect();
            return string;
//            byte[] buf = new byte[4096];
//            while (bin.read(buf) != -1) {
//                String temp = new String(buf);
//                sb.append(temp);
//            }
//            bin.close();

        } catch (IOException e) {
            return null;
        }
    }

}
