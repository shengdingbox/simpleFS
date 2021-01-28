package com.zhouzifei.tool.media.video;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.zhouzifei.tool.consts.VideoTypeConst;
import com.zhouzifei.tool.dto.VideoUrlDTO;
import com.zhouzifei.tool.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 周子斐
 * @remark 2021/1/22
 * @Description
 */
@Slf4j
public class KuaishouUtils {

        /**
         * 方法描述: 解析下载视频
         *
         * @param url
         * @author tarzan
         * @remark 2020年08月04日 10:33:40
         */
        public static VideoUrlDTO ksParseUrl(String url){
            HashMap<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Mobile Safari/537.36");
            String redirectUrl = HttpUtil.createGet(url).addHeaders(headers).execute().header("Location");
            String body= HttpUtil.createGet(redirectUrl).addHeaders(headers).execute().body();
            Document doc= Jsoup.parse(body);
            Elements videoElement = doc.select("script[type=text/javascript]");
            String videoInfo = videoElement.get(3).data().replaceAll("window.pageData= ","");
            JSONObject json = JSONObject.parseObject(videoInfo);
            String title = json.getJSONObject("video").getString("caption");
            String videoUrl=json.getJSONObject("video").getString("srcNoMark");
            videoUrl=videoUrl.substring(0,videoUrl.indexOf("?"));
            log.debug(videoUrl);
            log.debug(title);
            VideoUrlDTO videoUrlDTO1 = new VideoUrlDTO();
            videoUrlDTO1.setType(VideoTypeConst.MP4.getType());
            videoUrlDTO1.setCode("200");
            videoUrlDTO1.setUrl(videoUrl);
            videoUrlDTO1.setSuccess("1");
            videoUrlDTO1.setPlayer("ckplayer");
            videoUrlDTO1.setOriginalUrl(url);
            return videoUrlDTO1;
        }

    public static void main(String[] args) throws Exception {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Mobile Safari/537.36");
        HttpResponse httpResponse = HttpUtils
                .doGet("https://v.kuaishou.com/6ScOjz", "", "GET", headers, new HashMap<>());
        Header[] allHeaders = httpResponse.getAllHeaders();
        System.out.println(Arrays.toString(allHeaders));
        HttpRequest request = HttpUtil.createGet("https://v.kuaishou.com/6ScOjz");
        HttpRequest httpRequest = request.addHeaders(headers);
        cn.hutool.http.HttpResponse execute = httpRequest.execute();
        String location = execute.header("Location");
        System.out.println(location);
        URL url=new URL("https://v.kuaishou.com/6ScOjz");
        URLConnection conn=url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("User-Agent","Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Mobile Safari/537.36");
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        System.out.println(headerFields);
        conn.connect();
        //往服务器端写内容 也就是发起http请求需要带的参数
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        Map<String, List<String>> headerField = conn.getHeaderFields();
        System.out.println(headerFields);


        //.execute().header("Location");
        //String redirectUrl = (String) allHeaders


                //createGet(url).addHeaders(headers).execute().header("Location");
    }
}
