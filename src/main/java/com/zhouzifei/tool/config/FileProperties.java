package com.zhouzifei.tool.config;

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
    private Boolean localOpen;
    private String localUrl;
    private String localFilePath;

    private Boolean aliOpen;
    private String aliUrl;
    private String aliBucketName;
    private String aliEndpoint;
    private String aliAccessKey;
    private String aliSecretKey;

    private Boolean fastDFSOpen;
    private String fastDFSUrl;
    private String fastDFSServerUrl;
    private String fastDFSUserName;
    private String fastDFSPassWord;

    private Boolean huaweiOpen;
    private String huaweiUrl;
    private String huaweiEndpoint;
    private String huaweiBucketName;
    private String huaweiAccessKey;
    private String huaweiSecretKey;

    private Boolean qCloudOpen;
    private String qCloudUrl;
    private String qCloudBucketName;
    private String qCloudEndpoint;
    private String qCloudAccessKey;
    private String qCloudSecretKey;

    private Boolean qiniuOpen;
    private String qiniuUrl;
    private String qiniuBucketName;
    private String qiniuAccessKey;
    private String qiniuSecretKey;


    private Boolean smmsOpen;
    private String smmsToken;
    private String smmsUserName;
    private String smmsPassWord;

    private Boolean uPaiOpen;
    private String uPaiUrl;
    private String uPaiBucketName;
    private String uPaiUserName;
    private String uPaiPassWord;

    private Boolean xmlyOpen;
    private String xmlyCookie;
}
