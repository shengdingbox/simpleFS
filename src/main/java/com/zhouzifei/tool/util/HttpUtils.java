package com.zhouzifei.tool.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {

    public static String METHOD_GET = "GET";
    public static String METHOD_POST = "POST";
    public static String METHOD_PUT = "PUT";

    public static int DEF_CONNECT_TIMEOUT = 2 * 1000;
    public static int DEF_READ_TIMEOUT = 8 * 1000;
    public static Charset DEF_CHARSET = StandardCharsets.UTF_8;
    public static void main(String[] args) {
        JSONObject x= HttpUtils.doGetAuthorization("https://amzrealtime.despatchcloud.co.uk/ws/v1/wsfulfilment/list_fulfilment_clients");
        System.out.println(x);
    }
    public static JSONObject doGetAuthorization(String url) {
        Map<String, String> headers=new HashMap<>();
        headers.put("Authorization", "DC 454");
        String xx= HttpUtils.Get(url, headers);
        return JSONObject.parseObject(xx);
    }
    public static TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }
    }};
    public static void trustAll() {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static {
        trustAll();
    }

    public static String Get(String urlString) {
        return HttpsGo(urlString, METHOD_GET, null, null, DEF_CONNECT_TIMEOUT, DEF_READ_TIMEOUT);
    }

    public static String Get(String urlString, Map<String, String> headers) {
        return HttpsGo(urlString, METHOD_GET, headers, null, DEF_CONNECT_TIMEOUT, DEF_READ_TIMEOUT);
    }

    public static String Get(String urlString, Map<String, String> headers, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            StringBuilder url = new StringBuilder(urlString);
            try {
                boolean isFirst = true;
                if (urlString.contains("?")) {
                    if (!urlString.endsWith("&") && urlString.contains("&")) {
                        isFirst = false;
                    }
                } else {
                    url.append('?');
                }
                String paramsEncoding = DEF_CHARSET.name();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        url.append('&');
                    }
                    url.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                    url.append('=');
                    url.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                }
            } catch (Exception e) {
            }
            return Get(url.toString(), headers);
        } else {
            return Get(urlString, headers);
        }
    }

    public static String Post(String urlString, String contentType, byte[] content) {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Content-Type", contentType);
        return HttpsGo(urlString, METHOD_POST, headers, content, DEF_CONNECT_TIMEOUT, DEF_READ_TIMEOUT);
    }

    public static String FormPost(String urlString, String content) {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Content-Type", String.format("application/x-www-form-urlencoded; charset=%s", DEF_CHARSET.name()));
        return HttpsGo(urlString, METHOD_POST, null, content.getBytes(DEF_CHARSET), DEF_CONNECT_TIMEOUT, DEF_READ_TIMEOUT);
    }

    public static String XmlPost(String urlString, String content) {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Content-Type", String.format("text/html; charset=%s", DEF_CHARSET.name()));
        return HttpsGo(urlString, METHOD_POST, headers, content.getBytes(DEF_CHARSET), DEF_CONNECT_TIMEOUT, DEF_READ_TIMEOUT);
    }

    public static String JsonPost(String urlString, Object content) {
        return JsonPost(urlString, JSONObject.toJSONString(content, SerializerFeature.DisableCircularReferenceDetect));
    }

    public static String JsonPost(String urlString, String content) {
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Content-Type", String.format("application/json; charset=%s", DEF_CHARSET.name()));
        return HttpsGo(urlString, METHOD_POST, headers, content.getBytes(DEF_CHARSET), DEF_CONNECT_TIMEOUT, DEF_READ_TIMEOUT);
    }
    public static String JsonPost(String urlString, String content,Map<String, String> headers) {
        headers.put("Content-Type", String.format("application/json; charset=%s", DEF_CHARSET.name()));
        return HttpsGo(urlString, METHOD_POST, headers, content.getBytes(DEF_CHARSET), DEF_CONNECT_TIMEOUT, DEF_READ_TIMEOUT);
    }
    public static String JsonPut(String urlString, String content,Map<String, String> headers) {
        headers.put("Content-Type", String.format("application/json; charset=%s", DEF_CHARSET.name()));
        return HttpsGo(urlString, METHOD_PUT, headers, content.getBytes(DEF_CHARSET), DEF_CONNECT_TIMEOUT, DEF_READ_TIMEOUT);
    }
    public static String DataPost(String url,String param){
        final HashMap<String, String> headers = new HashMap<>();
        return DataPost(url, headers, param);
    }
    public static String DataPost(String url,Map<String, String> headers, String param) {
            PrintWriter out = null;
            BufferedReader in = null;
            StringBuilder result = new StringBuilder();
            try {
                URL realUrl = new URL(url);
                // 打开和URL之间的连接
                URLConnection conn = realUrl.openConnection();
                // 设置通用的请求属性
                conn.setRequestProperty("accept", "*/*");
                conn.setRequestProperty("connection", "Keep-Alive");
                conn.setRequestProperty("user-agent",
                        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                if (headers != null) {
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        conn.addRequestProperty(entry.getKey(), entry.getValue());
                    }
                }
                // 发送POST请求必须设置如下两行
                conn.setDoOutput(true);
                conn.setDoInput(true);
                // 获取URLConnection对象对应的输出流
                out = new PrintWriter(conn.getOutputStream());
                // 发送请求参数
                out.print(param);
                // flush输出流的缓冲
                out.flush();
                // 定义BufferedReader输入流来读取URL的响应
                in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
            } catch (Exception e) {
                System.out.println("发送 POST 请求出现异常！"+e);
                e.printStackTrace();
            }
            //使用finally块来关闭输出流、输入流
            finally{
                try{
                    if(out!=null){
                        out.close();
                    }
                    if(in!=null){
                        in.close();
                    }
                }
                catch(IOException ex){
                    ex.printStackTrace();
                }
            }
            return result.toString();
        }
    public static String filePost(String url, Map<String, String> headers, String bodyName, InputStream inputStream, String fileName) {
        //flush输出流的缓冲
        HttpClient httpclient = new DefaultHttpClient();
        try {
            bodyName = StringUtils.isEmpty(bodyName) ? "file" : bodyName;
            HttpPost httppost = new HttpPost(url);
            InputStreamBody bin = new InputStreamBody(inputStream, fileName);
            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart(bodyName, bin);
            httppost.setEntity(reqEntity);
            for (Map.Entry<String, String> e : headers.entrySet()) {
                httppost.addHeader(e.getKey(), e.getValue());
            }
            HttpResponse response = httpclient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = response.getEntity();
                //EntityUtils.consume(resEntity);
                return EntityUtils.toString(resEntity);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.getConnectionManager().shutdown();
            } catch (Exception ignore) {
            }
        }
        return null;
    }
    public static String HttpsGo(String urlString, String method, Map<String, String> headers, byte[] content, int connectTimeout, int readTimeout) {
        HttpsURLConnection conn = null;
        try {
            conn = (HttpsURLConnection) new URL(urlString).openConnection();
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setRequestMethod(method);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            if (content != null) {
                if (headers == null || !headers.containsKey("Content-Length")) {
                    conn.addRequestProperty("Content-Length", Integer.toString(content.length));
                }
                try (OutputStream output = conn.getOutputStream()){
                    output.write(content);
                    output.flush();
                }
            }
            return readContent(conn.getResponseCode() == 200 ? conn.getInputStream() : conn.getErrorStream(), getCharset(conn));
        } catch (Exception e) {
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String encodeParams(Map<String, String> params, String paramsEncoding) throws Exception {
        boolean isFirst = true;
        StringBuilder encodedParams = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                encodedParams.append('&');
            }
            encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
            encodedParams.append('=');
            encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
        }
        return encodedParams.toString();
    }

    public static String CHARSET_DEF = DEF_CHARSET.name();
    private static final String CHARSET_STR = "charset=";
    private static final int CHARSET_STR_LEN = CHARSET_STR.length();
    private static String getCharset(HttpURLConnection conn) {
        String contentType = conn.getHeaderField("Content-Type");
        int length = contentType != null ? contentType.length() : 0;
        if (length < CHARSET_STR_LEN) {
            return CHARSET_DEF;
        }
        int pos = contentType != null ? contentType.indexOf("charset=") : -1;
        if (pos < 0) {
            return CHARSET_DEF;
        }
        return contentType.substring(pos + CHARSET_STR_LEN);
    }

    private static String readContent(InputStream input, String charset) throws Exception {
        try {
            int APPEND_LEN = 4 * 1024;
            int offset = 0;
            byte[] data = new byte[APPEND_LEN];
            while (true) {
                int len = input.read(data, offset, data.length - offset);
                if (len == -1) {
                    break;
                }
                offset += len;
                if (offset >= data.length) {
                    data = Arrays.copyOf(data, offset + APPEND_LEN);
                }
            }
            return charset != null ? new String(data, 0, offset, charset) : new String(data, 0, offset);
        } finally {
            if (input != null) {
                try { input.close(); } catch (Exception ignored) { }
            }
        }
    }
}