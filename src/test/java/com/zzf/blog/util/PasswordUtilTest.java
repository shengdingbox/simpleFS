package com.zzf.blog.util;

import org.junit.Test;

import com.zhouzifei.tool.util.PasswordUtil;

/**
 * 密码加密测试工具类
 * @author Dabao (17600004572@163.com)
 * @version 1.0
 * @website https://www.zhouzifei.com
 * @date 2019年7月16日
 * @since 1.0
 */
public class PasswordUtilTest {

    @Test
    public void passwordTest() throws Exception {
        encryptTest("123456", "admin");
    }

    public void encryptTest(String password, String salt) throws Exception {
        String encrypt = PasswordUtil.encrypt(password, salt);
        System.out.println(encrypt);
        String decrypt = PasswordUtil.decrypt("VpavsFi6DaRqF5o3nziCgw==", "root");
        System.out.println(decrypt);
    }

}