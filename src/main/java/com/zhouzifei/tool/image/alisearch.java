package com.zhouzifei.tool.image;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhouzifei.tool.html.Randoms;
import com.zhouzifei.tool.media.file.FileUtil;

import java.io.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * @author 周子斐
 * @date 2021/2/7
 * @Description
 */
public class alisearch {
    //@Test
    public static String pic(String tsUrl) throws Exception {
        final InputStream inputStream = FileUtil.getInputStreamByUrl(tsUrl, "");
        if (null == inputStream) {
            throw new RuntimeException();
        }
        String result = post("https://icbu-picture-sh.oss-cn-shanghai.aliyuncs.com", inputStream);
        final JSONObject parse = JSONObject.parseObject(result);
        if (null == parse) {
            throw new RuntimeException();
        }
        final Object data = parse.get("data");
        if (null == data) {
            throw new RuntimeException();
        }
        final Object url = ((JSONArray) data).get(0);
        if (null == url) {
            throw new RuntimeException();
        }
        final Object url1 = ((JSONObject) url).get("url");
        return (String) url1;
    }

    public static String post(String url, InputStream inputStream) {
        final String fileName = Randoms.alpha(6) + "/" + Randoms.alpha(20) + ".jpg";
        //flush输出流的缓冲
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpPost httppost = new HttpPost(url);
            InputStreamBody bin = new InputStreamBody(inputStream, fileName);
            MultipartEntity reqEntity = new MultipartEntity();
            StringBody name = new StringBody(Randoms.alpha(6) + ".jpg");
            StringBody key = new StringBody(fileName);
            StringBody policy = new StringBody("eyJleHBpcmF0aW9uIjoiMjAyMS0wMi0xOVQxMzo0OToyNi4yNzBaIiwiY29uZGl0aW9ucyI6W1siY29udGVudC1sZW5ndGgtcmFuZ2UiLDAsMTA0ODU3NjAwMF1dfQ==");
            StringBody OSSAccessKeyId = new StringBody("LTAI05Gp6w3DJmjJ");
            StringBody success_action_status = new StringBody("200");
            StringBody signature = new StringBody("vNlV8uYz5sZyZaiCkvj6TPjcsZU=");
            //reqEntity.addPart("file", bin);//file1为请求后台的File upload;
            reqEntity.addPart("name", name);
            reqEntity.addPart("key", key);
            reqEntity.addPart("policy", policy);
            reqEntity.addPart("OSSAccessKeyId", OSSAccessKeyId);
            reqEntity.addPart("success_action_status", success_action_status);
            reqEntity.addPart("signature", signature);
            httppost.setEntity(reqEntity);
            HttpResponse response = httpclient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                System.out.println("服务器正常响应.....");
                HttpEntity resEntity = response.getEntity();
                System.out.println(EntityUtils.toString(resEntity));//httpclient自带的工具类读取返回数据
                System.out.println(resEntity.getContent());
                EntityUtils.consume(resEntity);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.getConnectionManager().shutdown();
            } catch (Exception ignore) {
            }
        }
        return fileName;
    }

        public static void main (String[]args) throws FileNotFoundException {
            final File file = new File("/Users/Dabao/Downloads/123.jpg");
            final FileInputStream inputStream = new FileInputStream(file);
            String result = post("https://icbu-picture-sh.oss-cn-shanghai.aliyuncs.com"
                    , inputStream);
            System.out.println(result);
        }
    }
