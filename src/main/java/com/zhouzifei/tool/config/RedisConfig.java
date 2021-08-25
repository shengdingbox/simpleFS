package com.zhouzifei.tool.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName RedisConfig
 * @Description RedisConfig
 * @Author 孙博
 * @Date 2020/1/2 17:14
 * @Version 1.0.0
 */
@Configuration
@ComponentScan(value = "com.zhouzifei.tool.util.JedisUtils")
public class RedisConfig {
}