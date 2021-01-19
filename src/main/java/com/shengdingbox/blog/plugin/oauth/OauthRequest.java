package com.shengdingbox.blog.plugin.oauth;

import me.zhyd.oauth.request.AuthRequest;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public interface OauthRequest {

    AuthRequest getRequest();
}
