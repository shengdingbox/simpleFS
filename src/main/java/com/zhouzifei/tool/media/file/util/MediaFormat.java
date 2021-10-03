package com.zhouzifei.tool.media.file.util;

import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author 周子斐 (17600004572@163.com)
 * @remark  2019/12/21 0:08
 */

public class MediaFormat {

    private static Set<String> set = new HashSet<>();

    static {
        set.add("mp4");
        set.add("mkv");
        set.add("webm");
        set.add("gif");
        set.add("mov");
        set.add("ogg");
        set.add("flv");
        set.add("avi");
        set.add("3gp");
        set.add("wmv");
        set.add("mpg");
        set.add("vob");
        set.add("swf");
        set.add("m3u8");
    }

    private MediaFormat() {

    }

    public static String getMediaFormat(String url) {
        if (!StringUtils.isUrl(url)) {
            throw new ServiceException(url + "不是一个完整URL链接！");
        }
        url = url.substring(url.lastIndexOf("/") - 1);
        for (String s : set) {
            if (url.contains(s)) {
                return s;
            }
        }
        throw new ServiceException("非视频链接！");
    }
}
