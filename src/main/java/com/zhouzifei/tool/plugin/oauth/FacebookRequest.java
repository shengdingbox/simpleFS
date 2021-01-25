package com.zhouzifei.tool.plugin.oauth;

import com.zhouzifei.tool.config.JustAuthProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.request.AuthFacebookRequest;
import me.zhyd.oauth.request.AuthRequest;

/**
 * 
 * @author Dabao (17600004572@163.com)
 * @version 1.0
 * @website https://www.zhouzifei.com
 * @date 2019年7月16日
 * @since 1.0
 */
@Component
public class FacebookRequest implements OauthRequest, InitializingBean {

    @Autowired
    private JustAuthProperties properties;

    @Override
    public AuthRequest getRequest() {
        AuthConfig authConfig = properties.getFacebook();
        return new AuthFacebookRequest(AuthConfig.builder()
                .clientId(authConfig.getClientId())
                .clientSecret(authConfig.getClientSecret())
                .redirectUri(authConfig.getRedirectUri())
                .build());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RequestFactory.registerRequest("facebook", this);
    }
}
