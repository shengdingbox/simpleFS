package com.zhouzifei.tool.config;

import com.zhouzifei.tool.config.properties.FileOSSProperties;
import com.zhouzifei.tool.media.file.FileUploadUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 周子斐 (17600004572@163.com)
 * @remark 2021/1/27
 */
@Configuration
@EnableConfigurationProperties({FileOSSProperties.class})
public class FileOssAutoConfiguration {
    public FileOssAutoConfiguration() {
    }

    @Bean
    public FileOSSProperties fileOSSProperties(FileOSSProperties fileOSSProperties) {
        return fileOSSProperties;
    }
    @Bean
    public FileUploadUtil fileUploadUtil(){
        return new FileUploadUtil();
    }
}
