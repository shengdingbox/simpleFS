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
@Component
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "simple-fs.github")
public class GithubFileProperties extends FileProperties{
    private String token;
    private String user;
    private String repository;
}
