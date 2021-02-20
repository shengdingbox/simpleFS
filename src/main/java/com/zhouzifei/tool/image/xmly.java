package com.zhouzifei.tool.image;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhouzifei.tool.media.file.FileUtil;
import com.zhouzifei.tool.util.StringUtils;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author 周子斐
 * @date 2021/2/7
 * @Description
 */
public class xmly {
    //@Test
    public static String pic(String tsUrl) throws Exception{
        final InputStream inputStream = FileUtil.getInputStreamByUrl(tsUrl, "");
        if(null == inputStream){
            throw new RuntimeException();
        }
        String result = post("https://upload.ximalaya.com/dtres/headerThumb/upload"
                , "_xmLog=h5&2872be80-f5d5-4747-9b75-cb9e34503cf0&2.2.17; Hm_lvt_4a7d8ec50cfd6af753c4f8aee3425070=1612664140; trackType=web; fds_otp=8172383124598533914; 1&remember_me=y; 1&_token=296993092&6B62AF70140N62AD80A28DA7DB1479A11D32B64352BEB4ECBC37887968F72543742F4606E28F13M5F3014F4AE8BE20_; 1_l_flag=296993092&6B62AF70140N62AD80A28DA7DB1479A11D32B64352BEB4ECBC37887968F72543742F4606E28F13M5F3014F4AE8BE20__2021-02-0710:16:26; x_xmly_traffic=utm_source%253A%2526utm_medium%253A%2526utm_campaign%253A%2526utm_content%253A%2526utm_term%253A%2526utm_from%253A; Hm_lpvt_4a7d8ec50cfd6af753c4f8aee3425070=1612664187"
                , inputStream);
        final JSONObject parse = JSONObject.parseObject(result);
        if(null == parse){
            throw new RuntimeException();
        }
        final Object data = parse.get("data");
        if(null == data){
            throw new RuntimeException();
        }
        final Object url = ((JSONArray) data).get(0);
        if(null == url){
            throw new RuntimeException();
        }
        final Object url1 = ((JSONObject) url).get("url");
        return (String) url1;
    }

    public static  String post(String url, String cookie, InputStream inputStream){
        DataOutputStream out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            //打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            //发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            String BOUNDARY = "----WebKitFormBoundary07I8UIuBx6LN2KyY";
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("connection", "Keep-Alive");
            //            conn.setRequestProperty("user-agent", "Mozilla/4.0 (conpatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            conn.setRequestProperty("Cookie", cookie);
            conn.connect();

            out = new DataOutputStream(conn.getOutputStream());
            byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            //添加参数
            StringBuffer sb1 = new StringBuffer();
            sb1.append("--");
            sb1.append(BOUNDARY);
            sb1.append("\r\n");
            sb1.append("Content-Disposition: form-data;name=\"luid\"");
            sb1.append("\r\n");
            sb1.append("\r\n");
            sb1.append("123");
            sb1.append("\r\n");
            out.write(sb1.toString().getBytes());
            //添加参数file
            StringBuffer sb = new StringBuffer();
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"file\";filename=\"1.jpg\"");
            sb.append("\r\n");
            sb.append("Content-Type: application/octet-stream");
            sb.append("\r\n");
            sb.append("\r\n");
            out.write(sb.toString().getBytes());
            DataInputStream in1 = new DataInputStream(inputStream);
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in1.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            out.write("\r\n".getBytes());
            in1.close();
            out.write(end_data);
            //flush输出流的缓冲
            out.flush();
            //定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                // TODO: handle exception
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) throws FileNotFoundException {
        final File file = new File("/Users/Dabao/Downloads/123.jpg");
        final FileInputStream inputStream = new FileInputStream(file);
        String result = post("https://upload.ximalaya.com/dtres/headerThumb/upload"
                , "_xmLog=h5&2872be80-f5d5-4747-9b75-cb9e34503cf0&2.2.17; Hm_lvt_4a7d8ec50cfd6af753c4f8aee3425070=1612664140; trackType=web; fds_otp=8172383124598533914; 1&remember_me=y; 1&_token=296993092&6B62AF70140N62AD80A28DA7DB1479A11D32B64352BEB4ECBC37887968F72543742F4606E28F13M5F3014F4AE8BE20_; 1_l_flag=296993092&6B62AF70140N62AD80A28DA7DB1479A11D32B64352BEB4ECBC37887968F72543742F4606E28F13M5F3014F4AE8BE20__2021-02-0710:16:26; x_xmly_traffic=utm_source%253A%2526utm_medium%253A%2526utm_campaign%253A%2526utm_content%253A%2526utm_term%253A%2526utm_from%253A; Hm_lpvt_4a7d8ec50cfd6af753c4f8aee3425070=1612664187"
                , inputStream);
        System.out.println(result);
    }
}
