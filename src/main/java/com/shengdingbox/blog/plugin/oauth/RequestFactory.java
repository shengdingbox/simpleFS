package com.shengdingbox.blog.plugin.oauth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import me.zhyd.oauth.exception.AuthException;

/**
 * request工厂类， 生产具体的authRequest
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public class RequestFactory {

    public static Map<String, OauthRequest> requestMap = new HashMap<>();

    public static OauthRequest getInstance(String source) {
        if (StringUtils.isEmpty(source)) {
            throw new AuthException("请指定第三方平台");
        }
        OauthRequest request = requestMap.get(source);
        if (null == request) {
            throw new AuthException("当前系统暂不支持该平台[" + source + "]的授权登录");
        }
        return request;
    }

    public static void registerRequest(String type, OauthRequest request) {
        requestMap.put(type, request);
    }

}
