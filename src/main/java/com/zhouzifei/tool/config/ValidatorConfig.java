package com.zhouzifei.tool.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @website https://www.zhouzifei.com
 * @date 2019年7月16日
 * @since 1.0
 */
@Configuration
@Order(0)
public class ValidatorConfig implements WebMvcConfigurer {

    @Override
    public Validator getValidator() {
        return new LocalValidatorFactoryBean();
    }

    @Override
    public MessageCodesResolver getMessageCodesResolver() {
        return null;
    }
}
