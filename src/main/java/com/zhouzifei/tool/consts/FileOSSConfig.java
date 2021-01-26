package com.zhouzifei.tool.consts;

import lombok.Data;

/**
 *
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @website https://www.zhouzifei.com
 * @date 2019年7月16日
 * @since 1.0
 */
@Data
public class FileOSSConfig {
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
