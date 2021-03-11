package com.zhouzifei.tool.config.autoconfiguratiom;

import com.zhouzifei.tool.util.JedisUtils;
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
public class BeanAutoConfiguration {

    @Bean
    public RedisUtils RedisUtils() {
        return new RedisUtils();
    }
    @Bean
    public JedisUtils JedisUtils() {
        return new JedisUtils();
    }
}
