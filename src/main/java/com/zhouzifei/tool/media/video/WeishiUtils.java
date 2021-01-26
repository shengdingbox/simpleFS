package com.zhouzifei.tool.media.video;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhouzifei.tool.consts.VideoTypeConst;
import com.zhouzifei.tool.dto.VideoUrlDTO;
import com.zhouzifei.tool.exception.ServiceException;
import com.zhouzifei.tool.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 周子斐
 * @date 2021/1/22
 * @Description
 */
@Slf4j
public class WeishiUtils {

    public static VideoUrlDTO wsParse(String url) {
//@喵大仙带你停药带你菲：‘才华’的重要性 >>https://h5.qzone.qq.com/weishi/feed/XnRuSSdlBeNy01db/wsfeed?_proxy=1&_wv=1&id=XnRuSSdlBeNy01db
        try {
            String substring = url.substring(url.indexOf("&id="));
            String wsId = substring.replace("&id=", "");
            Map<String, String> hears = Maps.newHashMap();
            Map<String, String> body = Maps.newHashMap();
            body.put("feedid", wsId);
            body.put("recommendtype", "0");
            body.put("datalvl", "all");
            body.put("_weishi_mapExt", "{}");
            HttpResponse httpResponse = HttpUtils.doPost("https://h5.weishi.qq.com/webapp/json/weishi/WSH5GetPlayPage?t=0.10597698498408281&g_tk="
                    , "", "POST", hears, new HashMap<>(), body);
            String post = EntityUtils.toString(httpResponse.getEntity());
            JSONObject res = JSON.parseObject(post);
            if (0 == res.getIntValue("ret")) {
                JSONObject data = res.getJSONObject("data");
                JSONArray feeds = data.getJSONArray("feeds");
                JSONObject feed = feeds.getJSONObject(0);
                VideoUrlDTO videoUrlDTO = new VideoUrlDTO();
                videoUrlDTO.setType(VideoTypeConst.MP4.getType());
                videoUrlDTO.setCode("200");
                videoUrlDTO.setUrl(feed.getString("video_url"));
                videoUrlDTO.setSuccess("1");
                videoUrlDTO.setPlayer("ckplayer");
                videoUrlDTO.setOriginalUrl(url);
                return videoUrlDTO;
            } else {
                log.info("卫视失败了{}", res);
                return new VideoUrlDTO();
            }
        } catch (Exception e) {
            log.info("卫视失败了{}", ServiceException.getTrace(e));
            return new VideoUrlDTO();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(wsParse("https://h5.qzone.qq.com/weishi/feed/XnRuSSdlBeNy01db/wsfeed?_proxy=1&_wv=1&id=XnRuSSdlBeNy01db"));
    }
}
