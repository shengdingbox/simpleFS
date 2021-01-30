package com.zhouzifei.tool.config.autoconfiguratiom;

import com.zhouzifei.tool.config.properties.FileProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 周子斐 (17600004572@163.com)
 * @remark 2021/1/27
 */
@Configuration
@EnableConfigurationProperties({FileProperties.class})
public class FileAutoConfiguration {
    public FileAutoConfiguration() {
    }

    @Bean
    public FileProperties fileProperties(FileProperties fileProperties) {
        return fileProperties;
    }
}
