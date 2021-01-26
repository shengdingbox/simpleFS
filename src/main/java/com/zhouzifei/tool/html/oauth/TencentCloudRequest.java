package com.zhouzifei.tool.html.oauth;

import com.zhouzifei.tool.config.properties.JustAuthProperties;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.request.AuthTencentCloudRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @website https://www.zhouzifei.com
 * @date 2019年7月16日
 * @since 1.0
 */
@Component
public class TencentCloudRequest implements OauthRequest, InitializingBean {

    @Autowired
    private JustAuthProperties properties;

    @Override
    public AuthRequest getRequest() {
        AuthConfig authConfig = properties.getTencentCloud();
        return new AuthTencentCloudRequest(AuthConfig.builder()
                .clientId(authConfig.getClientId())
                .clientSecret(authConfig.getClientSecret())
                .redirectUri(authConfig.getRedirectUri())
                .build());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RequestFactory.registerRequest("tencentCloud", this);
    }
}
