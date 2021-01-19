package com.shengdingbox.blog.business.enums;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public enum CachePrefixEnum {

    BIZ("biz_cache_"),
    VIEW("view_cache_"),
    DDOS("ddos_cache_"),
    WX("wx_api_cache_"),
    SPIDER("spider_cache_"),
    ;
    private String prefix;

    CachePrefixEnum(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
