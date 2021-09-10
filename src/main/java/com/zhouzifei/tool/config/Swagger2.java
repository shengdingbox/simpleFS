package com.zhouzifei.tool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 功能描述：swagger配置类
 *
 * @author 周子斐 (17600004572@163.com)
 * @date: 2019/02/21
 * @version: V1.0.0
 */
@Configuration
@EnableSwagger2
public class Swagger2 {

	/**
	 * 选择只启用制定包下的api
	 *
	 * @return Docket
	 */
	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
				.apis(RequestHandlerSelectors.basePackage("com.zhouzifei.tool.controller"))
				.paths(PathSelectors.any()).build();
	}

	/**
	 * 构建文档基本信息
	 *
	 * @return 文档基本信息
	 */
	private ApiInfo apiInfo() {
		Contact contact = new Contact("商户管理服务组", "/v1/about", "");
		return new ApiInfoBuilder().title("商户管理服务API文档").description("商户管理服务1.0").termsOfServiceUrl("icitic")
				.contact(contact).version("1.0").build();
	}
}
