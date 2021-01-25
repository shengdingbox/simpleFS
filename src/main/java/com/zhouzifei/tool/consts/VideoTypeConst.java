package com.zhouzifei.tool.consts;

/**
 * @author 周子斐
 * parse
 * @date 2020/7/24
 * @Description
 */

public enum VideoTypeConst {

    YOUKU("youku.com", "Y"),
    TUDOU("tudou.com", "Y"),
    AQIYI("iqiyi.com", "Y"),
    MGTV("mgtv.com", "Y"),
    LETV("le.com", "Y"),
    QQ("qq.com", "Y"),
    SOHU("sohu.com", "Y"),
    PPTV("pptv.com", "Y"),
    M1905("1905.com", "Y"),
    MIGU("miguvideo.com", "Y"),
    M360("360kan.com", "Y"),
    WASU("wasu.cn", "Y"),
    CCTV("cctv.com", "N"),
    SIYA("sina.com.cn", "Y"),

    M3U8("m3u8", "Y"),
    XML("xml", "Y"),
    MP4("mp4", "Y"),
    HLS("hls", "Y"),
    PPX("pipix.com", "Y"),
    XIGUA("xgsp", "Y"),
    YIYUETAI("yinyuetai.com", "Y"),
    DOUYIN("douyin.com", "Y"),
    KAIYAN("eyepetizer.net", "Y"),
    NAVER("naver.com", "Y"),
    TANGTOU("tangdou.com", "Y"),
    QICHE("autohome.com.cn", "Y"),
    ECHOMV("app-echo.com", "Y"),
    BAOMIHUA("baomihua.com", "Y"),
    KUGOU("kugou.com", "Y"),

//    梨视频
//    https://www.pearvideo.com/video_1352720
//    第一视频
//    http://www.v1.cn/video/14906477.shtml
//    东方头条
//    http://video.eastday.com/a/180610095032571464694.html
//    触手视频
//    https://chushou.tv/gamezone/video/play/4178557.htm


    ;
    private String type;
    private String isSave;

    VideoTypeConst(String type, String isSave) {
        this.type = type;
        this.isSave = isSave;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsSave() {
        return isSave;
    }

    public void setIsSave(String isSave) {
        this.isSave = isSave;
    }

    public static String getKey(String url) {
        VideoTypeConst[] videoTypeConsts = values();
        for (VideoTypeConst videoType : videoTypeConsts) {
            if (url.contains(videoType.getType())) {
                return videoType.getType();
            }
        }
        return "defult";
    }

    public static String getSave(String url) {
        VideoTypeConst[] videoTypeConsts = values();
        for (VideoTypeConst videoType : videoTypeConsts) {
            if (url.contains(videoType.getType())) {
                return videoType.getIsSave();
            }
        }
        return "Y";
    }
}
