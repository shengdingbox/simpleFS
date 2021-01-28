package com.zhouzifei.tool.config;

import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tomcat相关配置
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0


 * @remark 2019年7月16日
 * @since 1.0
 */
@Configuration
public class TomcatConfig {
	@Bean
	public ConfigurableServletWebServerFactory webServerFactory() {
		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
		factory.addConnectorCustomizers(
				connector -> {
					Http11NioProtocol protocol =
							(Http11NioProtocol) connector.getProtocolHandler();
					protocol.setDisableUploadTimeout(false);
				}
		);
		return factory;
	}


}
