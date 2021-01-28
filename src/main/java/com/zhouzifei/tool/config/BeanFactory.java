package com.zhouzifei.tool.config;

import com.zhouzifei.tool.media.video.ParseUtils;
import com.zhouzifei.tool.util.RedisUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 周子斐 (17600004572@163.com)
 * @remark 2021/1/25
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
