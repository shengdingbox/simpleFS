package com.shengdingbox.blog.business.enums;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public enum UserPrivacyEnum {
    PUBLIC(1, "公开"),
    PRIVATE(2, "不公开"),;
    private Integer code;
    private String desc;

    UserPrivacyEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static UserPrivacyEnum get(Integer code) {
        if (null == code) {
            return PUBLIC;
        }
        UserPrivacyEnum[] enums = UserPrivacyEnum.values();
        for (UserPrivacyEnum anEnum : enums) {
            if (anEnum.getCode().equals(code)) {
                return anEnum;
            }
        }
        return PUBLIC;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
