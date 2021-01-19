package com.shengdingbox.blog.business.enums;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public enum ExtraCommentTypeEnum {

    GUESTBOOK(-1L, "/guestbook", "留言板 "),
    LINKS(-2L, "/links", "友情链接 "),
    ABOUT(-3L, "/about", "关于"),
    ARTICLE(null, "/article/", ""),
    ;

    private Long sid;
    private String url;
    private String title;

    ExtraCommentTypeEnum(Long sid, String url, String title) {
        this.sid = sid;
        this.url = url;
        this.title = title;
    }

    public static ExtraCommentTypeEnum getBySid(Long sid) {
        if (sid == null) {
            return null;
        }
        for (ExtraCommentTypeEnum value : ExtraCommentTypeEnum.values()) {
            if (sid.equals(value.getSid())) {
                return value;
            }
        }
        return ARTICLE;
    }

    public Long getSid() {
        return sid;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

}
