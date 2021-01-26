package com.zhouzifei.tool.media.file.service;


import com.zhouzifei.tool.consts.FileOSSConfig;
import com.zhouzifei.tool.exception.GlobalFileException;
import com.zhouzifei.tool.exception.ServiceException;
import com.zhouzifei.tool.media.file.FileClient.AliyunOssApiClient;
import com.zhouzifei.tool.media.file.FileClient.LocalApiClient;
import com.zhouzifei.tool.media.file.FileClient.QiniuApiClient;
import org.springframework.util.StringUtils;


/**
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @website https://www.zhouzifei.com
 * @date 2019年7月16日
 * @since 1.0
 */
public class BaseFileUploader {

    protected ApiClient getApiClient(String pathPrefix, String storageType,FileOSSConfig ossConfig) {
        if (StringUtils.isEmpty(storageType)) {
            throw new ServiceException("[文件服务]当前系统暂未配置文件服务相关的内容！");
        }
        ApiClient res = null;
        switch (storageType) {
            case "local":
                String localFileUrl = ossConfig.getLocalFileUrl();
                String localFilePath = ossConfig.getLocalFilePath();
                res = new LocalApiClient().init(localFileUrl, localFilePath, pathPrefix);
                break;
            case "qiniu":
                String accessKey = ossConfig.getQiniuAccessKey();
                String secretKey = ossConfig.getQiniuSecretKey();
                String qiniuBucketName = ossConfig.getQiniuBucketName();
                String baseUrl = ossConfig.getQiniuBasePath();
                res = new QiniuApiClient().init(accessKey, secretKey, qiniuBucketName, baseUrl, pathPrefix);
                break;
            case "aliyun":
                String endpoint = ossConfig.getAliyunEndpoint();
                String accessKeyId = ossConfig.getAliyunAccessKey();
                String accessKeySecret = ossConfig.getAliyunAccessKeySecret();
                String url = ossConfig.getAliyunFileUrl();
                String aliYunBucketName = ossConfig.getAliyunBucketName();
                res = new AliyunOssApiClient().init(endpoint, accessKeyId, accessKeySecret, url, aliYunBucketName, pathPrefix);
                break;
            case "youpaiyun":
                break;
            default:
                break;
        }
        if (null == res) {
            throw new GlobalFileException("[文件服务]当前系统暂未配置文件服务相关的内容！");
        }
        return res;
    }
}
