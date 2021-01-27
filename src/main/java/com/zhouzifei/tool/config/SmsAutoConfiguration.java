package com.zhouzifei.tool.config;

import com.zhouzifei.tool.config.properties.FileOSSProperties;
import com.zhouzifei.tool.config.properties.SendSmsProperties;
import com.zhouzifei.tool.media.file.FileUploadUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 周子斐
 * @date 2021/1/27
 * @Description
 */
@Configuration
@EnableConfigurationProperties({SendSmsProperties.class})
public class SmsAutoConfiguration {
    public SmsAutoConfiguration() {
    }

    @Bean
    public SendSmsProperties sendSmsProperties(SendSmsProperties sendSmsProperties) {
        return sendSmsProperties;
    }

}
