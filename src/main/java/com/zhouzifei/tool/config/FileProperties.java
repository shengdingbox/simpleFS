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
@ConfigurationProperties(prefix = "simple-fs")
@Data
@EqualsAndHashCode(callSuper = false)
@Order(-1)
public class FileProperties {
    LocalFileProperties local = new LocalFileProperties();
    OssFileProperties oss = new OssFileProperties();
    FastDfsFileProperties fast = new FastDfsFileProperties();
    HuaweiFileProperties huawei = new HuaweiFileProperties();
    BosFileProperties bos = new BosFileProperties();
    QcloudFileProperties tengxun = new QcloudFileProperties();
    QiniuFileProperties qiniu = new QiniuFileProperties();
    UpaiFileProperties upai = new UpaiFileProperties();
    SmmsFileProperties smms = new SmmsFileProperties();
    XmlyFileProperties xmly = new XmlyFileProperties();
    GithubFileProperties github = new GithubFileProperties();
    AwsFileProperties aws = new AwsFileProperties();

}
