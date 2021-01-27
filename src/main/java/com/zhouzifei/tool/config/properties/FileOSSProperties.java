package com.zhouzifei.tool.config.properties;

import com.zhouzifei.tool.consts.StorageTypeConst;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author 周子斐
 * @date 2021/1/26
 * @Description
 */
@Component
@ConfigurationProperties(prefix = "tool.oss")
@Data
@EqualsAndHashCode(callSuper = false)
@Order(-1)
public class FileOSSProperties {
    /**
     * 云存储类型
     */
    private StorageTypeConst storageTypeConst;
    /**
     * 文件前缀
     */
    private String pathPrefix;
    /**
     * 文件服务器域名
     */
    private String localFileUrl;
    /**
     * 文件存储路径
     */
    private String localFilePath;
    /**
     * 七牛云Bucket 名称
     */
    private String qiniuBucketName;
    /**
     * 七牛云AccessKey
     */
    private String qiniuAccessKey;
    /**
     * 七牛云Secret Key
     */
    private String qiniuSecretKey;
    /**
     * 七牛云cdn域名
     */
    private String qiniuBasePath;
    /**
     * 阿里云Bucket 名称
     */
    private String aliyunBucketName;
    /**
     * 阿里云地域节点（EndPoint）
     */
    private String aliyunEndpoint;
    /**
     * 阿里云Bucket 域名
     */
    private String aliyunFileUrl;
    /**
     * 阿里云Access Key
     */
    private String aliyunAccessKey;
    /**
     * 阿里云Access Key Secret
     */
    private String aliyunAccessKeySecret;
}
