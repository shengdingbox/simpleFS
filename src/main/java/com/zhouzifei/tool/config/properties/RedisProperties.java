package com.zhouzifei.tool.config.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * redis属性配置文件
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0


 * @remark 2019年7月16日
 * @since 1.0
 */
@Component
@ConfigurationProperties(prefix = "spring.redis")
@Data
@EqualsAndHashCode(callSuper = false)
@Order(-1)
public class RedisProperties extends CachingConfigurerSupport {
    private Integer database;
    private String host;
    private Integer port;
    private String password;
    private int timeout;
    private int maxIdle;
    private long maxWaitMillis;
    private int maxActive;
    private int minIdle;
    /**
     * 默认30天 = 2592000s
     */
    private Integer expire = 2592000;
}
