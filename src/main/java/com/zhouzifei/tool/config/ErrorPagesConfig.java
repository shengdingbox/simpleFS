package com.zhouzifei.tool.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * 自定义的异常页面配置
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @note https://www.zhouzifei.com
 * @remark 2019年7月16日
 * @since 1.0
 */
@Component
public class ErrorPagesConfig {
    /**
     * 自定义异常处理路径
     *
     * @return
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> containerCustomizer() {
        return factory -> {
			factory.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/error/400"));
			factory.addErrorPages(new ErrorPage(HttpStatus.UNAUTHORIZED, "/error/401"));
			factory.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, "/error/403"));
			factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error/404"));
			factory.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500"));
			factory.addErrorPages(new ErrorPage(Throwable.class, "/error/500"));
		};
    }
}
