package com.zhouzifei.tool.media.video;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhouzifei.tool.consts.VideoTypeConst;
import com.zhouzifei.tool.dto.VideoUrlDTO;
import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public  class PPXUtil {

    public static VideoUrlDTO pPXParse(String url) {
        try {
        url = Jsoup.connect(url).get().baseUri();
        boolean lzl = url.contains("cell_id");
        String root = lzl ? getSubString(url, "cell_id=", "&") : getSubString(url, "item/", "?");
        JSONObject object = JSONObject.parseObject(get("https://is.snssdk.com/bds/cell/detail/?cell_type="
                + (lzl ? "8" : "1") + "&aid=1319&app_name=super&cell_id=" + root))
                .getJSONObject("data").getJSONObject("data");

            object = lzl ? object.getJSONObject("comment_info") : object.getJSONObject("item");
            String video = getVideo(lzl ? object.getJSONObject("video").getString("uri")
                    : object.getJSONObject("video").getString("video_id"));
            VideoUrlDTO videoUrlDTO = new VideoUrlDTO();
            videoUrlDTO.setType(VideoTypeConst.MP4.getType());
            videoUrlDTO.setCode("200");
            videoUrlDTO.setUrl(video);
            videoUrlDTO.setSuccess("1");
            videoUrlDTO.setPlayer("ckplayer");
            videoUrlDTO.setOriginalUrl(url);
            return videoUrlDTO;
        } catch (Exception e) {
            return null;
//            try {
//                return getImages(object, lzl);
//            } catch (Exception e1) {
//                return "Error";
//            }
        }
    }

    private static String getVideo(String video_id) throws IOException {
        long t = System.currentTimeMillis();
        String str = "https://i.snssdk.com/video/play/1/bds/" + t + "/"
                + DigestUtils.md5Hex("ts" + t + "userbdsversion1video" + video_id
                + "vtypemp4f425df23905d4ee38685e276072faa0c")
                + "/mp4/" + video_id;
        JSONObject object = JSONObject.parseObject(get(str))
                .getJSONObject("video_info").getJSONObject("data")
                .getJSONObject("video_list");
        return new String(Base64.getDecoder().decode(object.getJSONObject("video_"
                + (object.getJSONObject("video_2") == null ? "1" : "2"))
                .getString("main_url")), StandardCharsets.UTF_8);
    }

    private static String getImages(JSONObject object, boolean lzl) throws Exception {
        JSONArray images = lzl ? object.getJSONArray("images")
                : object.getJSONObject("note").getJSONArray("multi_image");
        int size = images.size();
        if (size == 0) {
            throw new Exception();
        }
        StringBuilder ret = new StringBuilder().append("img,");
        for (int i = 0; i < size; i++) {
            boolean fromList = true;
            JSONObject object2 = images.getJSONObject(i);
            if (!isThumb(object2, (lzl ? object.getJSONArray("image_thumbs") : object.getJSONObject("note").getJSONArray("multi_thumb")).getJSONObject(i))) {
                String url = object.getJSONObject(lzl ? "user" : "author").getJSONObject("avatar")
                        .getJSONArray("download_list").getJSONObject(0).getString("url");
                url = url.substring(0, url.lastIndexOf("obj")) + "obj/" + object2.getString("uri");
                if (!getType(url).equals("image/webp")) {
                    ret.append(url);
                    fromList = false;
                }
            }
            if (fromList) {
                String url = object2.getJSONArray("url_list").getJSONObject(0).getString("url");
                ret.append(url, 0, url.lastIndexOf(".")).append(object2.getBooleanValue("is_gif") ? ".gif" : ".png");
            }
            if (i != size - 1) {
                ret.append(",");
            }
        }
        return ret.toString();
    }

    private static boolean isThumb(JSONObject object, JSONObject object2) {
        return Math.abs(object.getInteger("width") - object2.getInteger("width")) < 5 && Math.abs(object.getInteger("height") - object2.getInteger("height")) < 5;
    }

    private static String get(String Url) throws IOException {
        return Jsoup.connect(Url).ignoreContentType(true).ignoreHttpErrors(true).execute().body();
    }

    private static String getType(String Url) throws IOException {
        return Jsoup.connect(Url).ignoreContentType(true).ignoreHttpErrors(true).execute().contentType();
    }

    private static String getSubString(String text, String left, String right) {
        int index = text.indexOf(left) + left.length();
        return text.substring(index, text.indexOf(right, index));
    }

}
