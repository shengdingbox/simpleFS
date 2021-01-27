package com.zhouzifei.tool.config;

import com.zhouzifei.tool.config.properties.FileOSSProperties;
import com.zhouzifei.tool.config.properties.SendSmsProperties;
import com.zhouzifei.tool.media.file.FileUploadUtil;
import com.zhouzifei.tool.media.video.ParseUtils;
import com.zhouzifei.tool.util.RedisUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 周子斐 (zhouzf@asp.citic.com)
 * @date 2021/1/25
 * @Description
 * @Copyright 中信网络科技有限公司 Copyright (c)
 */
@Configuration
@ConditionalOnWebApplication
public class BeanFactory {

    @Bean
    public ParseUtils ParseUtil() {
        return new ParseUtils();
    }
    @Bean
    public RedisUtils RedisUtils() {
        return new RedisUtils();
    }
}
