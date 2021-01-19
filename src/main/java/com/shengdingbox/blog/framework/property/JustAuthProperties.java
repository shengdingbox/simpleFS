package com.shengdingbox.blog.framework.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.zhyd.oauth.config.AuthConfig;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
@Component
@ConfigurationProperties(prefix = "com.shengdingbox.blog")
@Data
@EqualsAndHashCode(callSuper = false)
@Order(-1)
public class JustAuthProperties {

    private AuthConfig gitee;
    private AuthConfig github;
    private AuthConfig weibo;
    private AuthConfig dingtalk;
    private AuthConfig baidu;
    private AuthConfig coding;
    private AuthConfig tencentCloud;
    private AuthConfig oschina;
    private AuthConfig alipay;
    private AuthConfig qq;
    private AuthConfig wechat;
    private AuthConfig taobao;
    private AuthConfig google;
    private AuthConfig facebook;
    private AuthConfig csdn;
}
