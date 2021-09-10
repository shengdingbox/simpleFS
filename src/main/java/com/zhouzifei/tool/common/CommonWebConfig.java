package com.zhouzifei.tool.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CommonWebConfig implements WebMvcConfigurer {
    public CommonWebConfig() {
    }

    @Bean
    public WebExceptionHandler exceptionHandler() {
        return new WebExceptionHandler();
    }
}
