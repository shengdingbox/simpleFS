package com.zhouzifei.tool.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhouzifei.tool.consts.VideoTypeConst;
import com.zhouzifei.tool.dto.VideoUrl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 周子斐
 * parse
 * @date 2020/10/23
 * @Description
 */
@Component
@Slf4j
public class ParseUtils {

    @Autowired
    RedisUtils redisUtils;
    @Autowired
    VideoSaveUtils videoSaveUtils;

    /**
     *
     * @param url 解析的地址
     * @param parseName 解析的名称
     * @param parseUrl 解析接口地址
     * @param fileLocal 本地路径
     * @param fileUrl 返回的域名
     */
    public void parseVideo(String url, String parseName, String parseUrl,String fileLocal,String fileUrl) {
        log.info("{}解析视频,解析地址为{}", parseName, url);
        String data = HttpData.getData(parseUrl + url);
        VideoUrl videoUrl = JSONObject.parseObject(data, VideoUrl.class);
        if (videoUrl == null) {
            videoUrl = new VideoUrl();
            videoUrl.setCode("404");
        }
        if (redisUtils.hasKey(url)) {
            videoUrl = new VideoUrl();
            videoUrl.setCode("404");
        }
        videoUrl.setParser(parseName);
        log.info("{}解析完成,结果为{}", parseName, videoUrl);
        String key = VideoTypeConst.getKey(url);
        videoUrl.setOriginalUrl(url);
        videoUrl.setPrefixType(key);
        if ("200".equals(videoUrl.getCode())) {
            if(VideoTypeConst.XML.getType().equals(videoUrl.getType())){
                String escapeJava2 = StringEscapeUtils.unescapeJava(videoUrl.getUrl());
                JSONArray objects = JSONObject.parseArray(escapeJava2);
                if(objects.size() > 0){
                    JSONObject jsonObject = (JSONObject) objects.get(0);
                    videoUrl.setUrl((String) jsonObject.get("purl"));
                    videoUrl.setType(VideoTypeConst.M3U8.getType());
                }
            }
            //保存m3u8文件
            if(VideoTypeConst.M3U8.getType().equals(videoUrl.getType())||VideoTypeConst.HLS.getType().equals(videoUrl.getType())){
                videoUrl = videoSaveUtils.saveVideo(videoUrl,fileLocal,fileUrl);
                videoUrl.setType(VideoTypeConst.M3U8.getType());
            }
            if (redisUtils.hasKey(url)) {
                return;
            }
            if (StringUtils.isEmpty(videoUrl.getUrl())) {
                return;
            }
            log.info("当前线程{},任务{},响应为{}", Thread.currentThread().getName(), videoUrl.getParser(), videoUrl);
            videoUrl.setParser("蜜蜂解析");
            videoUrl.setMsg("解析成功");
            videoUrl.setPlayer("dplayer");
            videoUrl.setSuccess("1");
            if (videoUrl.getUrl().contains("http://")){
                videoUrl.setUrl(videoUrl.getUrl().replace("http://","https://"));
            }
            String s = JSONObject.toJSONString(videoUrl);
            redisUtils.set(videoUrl.getOriginalUrl(), s, 3600);
        } else {
            log.info("当前线程{},任务{},解析失败", Thread.currentThread().getName(), videoUrl.getParser());
        }
    }
}
