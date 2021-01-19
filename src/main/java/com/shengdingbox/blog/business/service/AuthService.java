package com.shengdingbox.blog.business.service;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public interface AuthService {

    boolean login(String source, String code, String auth_code);

    boolean revoke(String source, Long userId);

    void logout();
}
