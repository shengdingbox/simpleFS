package com.zhouzifei.tool.config;

import com.zhouzifei.tool.annotation.FileTypeName;
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
@ConfigurationProperties(prefix = "simple-fs.oss")
@FileTypeName("阿里云OSS")
public class OssFileProperties extends FileProperties{
    private String domainUrl;
    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String bucketName;
}
