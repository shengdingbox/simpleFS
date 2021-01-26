package com.zhouzifei.tool.config;

import com.zhouzifei.tool.config.properties.FileOSSProperties;
import com.zhouzifei.tool.consts.FileOSSConfig;
import com.zhouzifei.tool.media.file.GlobalFileUploader;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 周子斐
 * @date 2021/1/26
 * @Description
 */
@Configuration
@EnableConfigurationProperties(FileOSSProperties.class)
public class FileOSSAutoConfiguration {

    @Bean
    public FileOSSConfig fileossconfig(FileOSSProperties fileossproperties) {
        //创建组件实例
        FileOSSConfig fileossconfig = new FileOSSConfig();
        fileossconfig.setPathPrefix(fileossproperties.getPathPrefix());
        fileossconfig.setLocalFileUrl(fileossproperties.getLocalFileUrl());
        fileossconfig.setLocalFilePath(fileossproperties.getLocalFilePath());
        fileossconfig.setQiniuAccessKey(fileossproperties.getQiniuAccessKey());
        fileossconfig.setQiniuSecretKey(fileossproperties.getQiniuSecretKey());
        fileossconfig.setQiniuBasePath(fileossproperties.getQiniuBasePath());
        fileossconfig.setQiniuBucketName(fileossproperties.getQiniuBucketName());
        fileossconfig.setAliyunAccessKey(fileossproperties.getAliyunAccessKey());
        fileossconfig.setAliyunAccessKeySecret(fileossproperties.getAliyunAccessKeySecret());
        fileossconfig.setAliyunBucketName(fileossproperties.getAliyunBucketName());
        fileossconfig.setAliyunEndpoint(fileossproperties.getAliyunEndpoint());
        fileossconfig.setAliyunFileUrl(fileossproperties.getAliyunFileUrl());
        return fileossconfig;
    }
    @Bean
    public GlobalFileUploader globalFileUploader(){
        return new GlobalFileUploader();
    }
}
