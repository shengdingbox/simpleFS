package com.zhouzifei.tool.config.autoconfiguratiom;

import com.zhouzifei.tool.config.properties.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author 周子斐 (17600004572@163.com)
 * @remark 2021/1/27
 */
@Configuration
@EnableConfigurationProperties({RedisProperties.class})
@Slf4j
public class RedisAutoConfiguration {
    public RedisAutoConfiguration() {
    }

    @Bean
    public JedisPool redisPoolFactory(RedisProperties redisProperties){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(redisProperties.getMaxIdle());
        jedisPoolConfig.setMaxWaitMillis(redisProperties.getMaxWaitMillis());
        jedisPoolConfig.setMaxTotal(redisProperties.getMaxActive());
        jedisPoolConfig.setMinIdle(redisProperties.getMinIdle());
        JedisPool jedisPool = new JedisPool(jedisPoolConfig
                ,redisProperties.getHost()
                ,redisProperties.getPort()
                ,redisProperties.getTimeout(),null);
        log.info("JedisPool注入成功！");
        log.info("redis地址：" + redisProperties.getHost() + ":" + redisProperties.getPort());
        return  jedisPool;
    }
}
