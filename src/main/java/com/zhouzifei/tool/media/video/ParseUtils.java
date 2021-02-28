package com.zhouzifei.tool.media.video;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhouzifei.tool.consts.VideoTypeConst;
import com.zhouzifei.tool.dto.VideoUrlDTO;
import com.zhouzifei.tool.util.HttpData;
import com.zhouzifei.tool.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 周子斐 (17600004572@163.com)
 * @remark 2020/10/23

 * @Description
 */
@Slf4j
public class ParseUtils {
    /**
     *
     * @param url 解析的地址
     * @param parseName 解析的名称
     * @param parseUrl 解析接口地址
     */
    public VideoUrlDTO parseVideo(String url, String parseName, String parseUrl, Map<String,String> hears,int readTime) {
        log.info("{}解析视频,解析地址为{}", parseName, url);
        String data = HttpData.getData(parseUrl + url,hears,readTime);
        if (data == null) {
            VideoUrlDTO videoUrlDTO = new VideoUrlDTO();
            videoUrlDTO.setCode("404");
            return videoUrlDTO;
        }
        data = data.replace("\n", "");
        data = data.replace("\r", "");
        VideoUrlDTO videoUrlDTO = JSONObject.parseObject(data, VideoUrlDTO.class);
        if (videoUrlDTO == null) {
            videoUrlDTO = new VideoUrlDTO();
            videoUrlDTO.setCode("404");
        }
        videoUrlDTO.setParser(parseName);
        log.info("{}解析完成,结果为{}", parseName, videoUrlDTO);
        String key = VideoTypeConst.getKey(url);
        videoUrlDTO.setOriginalUrl(url);
        videoUrlDTO.setPrefixType(key);
        if ("200".equals(videoUrlDTO.getCode())) {
            if(VideoTypeConst.XML.getType().equals(videoUrlDTO.getType())){
                String escapeJava2 = StringEscapeUtils.unescapeJava(videoUrlDTO.getUrl());
                JSONArray objects = JSONObject.parseArray(escapeJava2);
                if(objects.size() > 0){
                    JSONObject jsonObject = (JSONObject) objects.get(0);
                    videoUrlDTO.setUrl((String) jsonObject.get("purl"));
                    videoUrlDTO.setType(VideoTypeConst.M3U8.getType());
                }
            }
            //保存m3u8文件
            if(VideoTypeConst.M3U8.getType().equals(videoUrlDTO.getType())||VideoTypeConst.HLS.getType().equals(videoUrlDTO.getType())){
                //VideoSaveUtils.saveVideo(videoUrlDTO, fileLocal, fileUrl);
                videoUrlDTO.setType(VideoTypeConst.M3U8.getType());
            }
            if (StringUtils.isEmpty(videoUrlDTO.getUrl())) {
                return null;
            }
            log.info("当前线程{},任务{},响应为{}", Thread.currentThread().getName(), videoUrlDTO.getParser(), videoUrlDTO);
            videoUrlDTO.setParser("蜜蜂解析");
            videoUrlDTO.setMsg("解析成功");
            videoUrlDTO.setPlayer("dplayer");
            videoUrlDTO.setSuccess("1");
            if (videoUrlDTO.getUrl().contains("http://")){
                videoUrlDTO.setUrl(videoUrlDTO.getUrl().replace("http://","https://"));
            }
            return videoUrlDTO;
        } else {
            log.info("当前线程{},任务{},解析失败", Thread.currentThread().getName(), videoUrlDTO.getParser());
            return null;
        }
    }
}
