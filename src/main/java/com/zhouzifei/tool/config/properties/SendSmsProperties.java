package com.zhouzifei.tool.config.properties;

import com.zhouzifei.tool.consts.StorageTypeConst;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 短信配置类
 * @author 周子斐
 * @remark 2021/1/26

 * @Description
 */
@Component
@ConfigurationProperties(prefix = "tool.sms")
@Data
@EqualsAndHashCode(callSuper = false)
@Order(-1)
public class SendSmsProperties {
    /**
     * 云存储类型
     */
    private StorageTypeConst storageTypeConst;

    /**
     * 阿里云签名名称
     */
    private String aliyunSignName;
    /**
     * 阿里云模板编号
     */
    private String aliyunTemplateCode;
    /**
     * 阿里云Access Key
     */
    private String aliyunAccessKey;
    /**
     * 阿里云Access Key Secret
     */
    private String aliyunSecret;
}
