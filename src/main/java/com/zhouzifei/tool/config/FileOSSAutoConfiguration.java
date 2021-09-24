package com.zhouzifei.tool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 周子斐
 * @date 2021/1/26
 * @Description
 */
@Configuration
public class FileOSSAutoConfiguration {

    @Bean
    public ComponetImport globalFileUploader(){
        return new ComponetImport();
    }
}
