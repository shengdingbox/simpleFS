package com.zhouzifei.tool.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 周子斐 (17600004572@163.com)
 * @remark 2021/1/26
 * @Description
 */
@Component
@Data
@ConfigurationProperties(prefix = "simple-fs.upai")
public class UpaiFileProperties {
    private String url;
    private String bucketName;
    private String userName;
    private String passWord;
}