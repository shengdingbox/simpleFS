package com.zhouzifei.tool.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * image自动装配
 * @author 闫明辉(mh.yan02@zuche.com)
 * @date 2021/01/25 15:32
 */
@Configuration
@EnableConfigurationProperties(FileProperties.class)
public class FileAutoConfiguration {

    @Bean
    public FileProperties fileProperties(FileProperties fileProperties){
        return fileProperties;
    }
}
