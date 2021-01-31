package com.zhouzifei.tool.config.properties;

import com.zhouzifei.tool.consts.StorageTypeConst;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author 周子斐 (17600004572@163.com)
 * @remark 2021/1/26

 * @Description
 */
@Component
@ConfigurationProperties(prefix = "tool.file")
@Data
@EqualsAndHashCode(callSuper = false)
@Order(-1)
public class FileProperties {
    /**
     * 云存储类型
     */
    private StorageTypeConst storageTypeConst;
    /**
     * 文件前缀
     */
    private String pathPrefix;
    /**
     * 服务器域名
     */
    private String domainUrl;
    /**
     * 文件存储路径
     */
    private String localFilePath;
    /**
     * Bucket 名称
     */
    private String bucketName;
    /**
     * AccessKey
     */
    private String accessKey;
    /**
     * Secret Key
     */
    private String secretKey;
    /**
     * 阿里云地域节点（EndPoint）
     */
    private String endpoint;
    /**
     * 又拍云账号
     */
    private String operatorName;
    /**
     * 又拍云密码
     */
    private String operatorPwd;
}
