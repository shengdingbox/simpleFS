package com.zhouzifei.tool.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 周子斐 (17600004572@163.com)
 * @remark 2021/1/26
 * @Description
 */
@EqualsAndHashCode(callSuper = true)
@Component
@Data
@ConfigurationProperties(prefix = "simple-fs.aws")
public class AwsFileProperties extends FileProperties{
    private String domainUrl;
    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String region;
    private String bucketName;
}
