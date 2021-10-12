package com.zhouzifei.tool.config.properties;

import com.zhouzifei.tool.consts.StorageTypeConst;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.Serializable;

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
public class FileProperties implements Serializable {
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
     * Bucket 名称
     */
    private String bucketName;
    /**
     * AccessKey
     */
    private String accessId;
    /**
     * Secret Key
     */
    private String secretKey;
    /**
     * 地域节点（EndPoint）
     */
    private String endpoint;
    /**
     * 账号（username）
     */
    private String username;
    /**
     * 密码（password）
     */
    private String password;
    /**
     * 登陆凭证（token）
     */
    private String token;

}
