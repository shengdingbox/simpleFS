package com.zhouzifei.tool.util;

import java.text.MessageFormat;

import com.zhouzifei.tool.consts.ApiUrlConst;

/**
 * Url构建工具类
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @since 1.0
 */
public class UrlBuildUtil {

    private static final String GET_LOCATION_BY_IP = "{0}?ak={1}&ip={2}&coor=bd09ll";
    private static final String BAIDU_PUSH_URL_PATTERN = "{0}{1}?site={2}&token={3}";

    /**
     * 根据ip获取定位信息的接口地址
     *
     * @param ip
     *         用户IP
     * @return
     */
    public static String getLocationByIp(String ip, String baiduApiAk) {
        return MessageFormat.format(GET_LOCATION_BY_IP, ApiUrlConst.BAIDU_API_GET_LOCATION_BY_IP, baiduApiAk, ip);
    }

    /**
     * 提交链接到百度的接口地址
     *
     * @param pushType
     *         urls: 推送, update: 更新, del: 删除
     * @param site
     *         待提交的站点
     * @param baiduPushToken
     *         百度推送的token，百度站长平台获取
     * @return
     */
    public static String getBaiduPushUrl(String pushType, String site, String baiduPushToken) {
        return MessageFormat.format(BAIDU_PUSH_URL_PATTERN, ApiUrlConst.BAIDU_PUSH_URL, pushType, site, baiduPushToken);
    }

    public static void main(String[] args) {
        final String zoXgYGZd6pk5G1hO8iTUGAKgFx5lOWUC = getLocationByIp("103.242.215.96", "zoXgYGZd6pk5G1hO8iTUGAKgFx5lOWUC");
        System.out.println(zoXgYGZd6pk5G1hO8iTUGAKgFx5lOWUC);
    }
}
