package com.zhouzifei.tool.media.video;


import com.zhouzifei.tool.consts.VideoTypeConst;
import com.zhouzifei.tool.dto.VideoUrlDTO;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DouyinUtils {

    /**
     * 抖音解析
     * @param info 解析的地址
     * @return 解析实体
     */
    public static VideoUrlDTO dyParse(String info) {
        try {
            //获取短连接
            String regex = "https://v.douyin.com/(.*?)/";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(info);
            String url = null;
            while (m.find()) {
                url = m.group().trim();
            }
            if (url != null) {
                //连接短链接，将短连接重定向原链接
                Connection conn = Jsoup.connect(url).ignoreContentType(true);
                conn.header("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 10; COL-AL10 Build/HUAWEICOL-AL10)");
                String src = conn.execute().url().toString();

                //获取原链接中的video的id
                String regex2 = "video/(.*?)/";
                Pattern p2 = Pattern.compile(regex2);
                Matcher m2 = p2.matcher(src);
                String id = null;
                while (m2.find()) {
                    id = m2.group(1).trim();
                }

                if (id != null) {
                    //将此id在接口中调用,并获取真正的视频链接
                    String jk = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + id;
                    Document doc = (Document) Jsoup.connect(jk)
                            //.header("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 10; COL-AL10 Build/HUAWEICOL-AL10)")
                            .ignoreContentType(true)
                            .get();
                    String json = doc.html();

                    //在返回的json中获取真正的视频地址
                    String regex3 = "play_addr\":\\{\"uri\":\"(.*?)\",\"url_list\":\\[\"(.*?)\"";
                    Pattern p3 = Pattern.compile(regex3);
                    Matcher m3 = p3.matcher(json);
                    String rel_url = null;
                    while (m3.find()) {
                        rel_url = m3.group(2).trim();
                    }

                    if (rel_url != null) {
                        //使用字符串替换，将真正的链接中的playwm替换为play,以此达到去水印的目的
                        String video_url = rel_url.replaceAll("playwm", "play");
                        //调用解析函数，解析并下载
                        log.info("\n开始解析...");
                        return jiexi(video_url);

                    } else {
                        log.info("获取真正的视频链接失败！");
                    }
                } else {
                    log.info("获取视频id失败！");
                }

            } else {
                log.info("提取视频链接失败！");
            }
            VideoUrlDTO videoUrlDTO = new VideoUrlDTO();
            videoUrlDTO.setType(VideoTypeConst.MP4.getType());
            videoUrlDTO.setCode("404");
            videoUrlDTO.setSuccess("1");
            videoUrlDTO.setPlayer("ckplayer");
            videoUrlDTO.setUrl("https://api.周子斐tv.cn/404.mp4");
            videoUrlDTO.setOriginalUrl(info);
            return videoUrlDTO;
        } catch (Exception e) {
            return null;
        }
    }


    //连接视频，重定向到无水印视频
    public static VideoUrlDTO jiexi(String url) throws IOException {
        Connection conn = Jsoup.connect(url).ignoreContentType(true);
        conn.header("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 10; COL-AL10 Build/HUAWEICOL-AL10)");
        String src = conn.execute().url().toString();
        log.info("\n解析完成,开始下载！\n");
        VideoUrlDTO videoUrlDTO = new VideoUrlDTO();
        videoUrlDTO.setType(VideoTypeConst.MP4.getType());
        videoUrlDTO.setCode("200");
        videoUrlDTO.setUrl(src);
        videoUrlDTO.setSuccess("1");
        videoUrlDTO.setPlayer("ckplayer");
        videoUrlDTO.setOriginalUrl(url);
        return videoUrlDTO;
        //down(src ,path,name);
    }
}

