package com.zhouzifei.tool.media.file.service;


import com.zhouzifei.tool.config.properties.FileOSSProperties;
import com.zhouzifei.tool.consts.StorageTypeConst;
import com.zhouzifei.tool.exception.GlobalFileException;
import com.zhouzifei.tool.exception.ServiceException;
import com.zhouzifei.tool.media.file.FileClient.AliyunOssApiClient;
import com.zhouzifei.tool.media.file.FileClient.LocalApiClient;
import com.zhouzifei.tool.media.file.FileClient.QiniuApiClient;


/**
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
 * @since 1.0
 */
public class BaseFileUploader {

    protected ApiClient getApiClient(String pathPrefix, FileOSSProperties fileOSSProperties) {
        ApiClient res = null;
        String storageType = fileOSSProperties.getStorageTypeConst().getStorageType();
        if(storageType.equals(StorageTypeConst.LOCAL.getStorageType())) {
            String localFileUrl = fileOSSProperties.getLocalFileUrl();
            String localFilePath = fileOSSProperties.getLocalFilePath();
            res = new LocalApiClient().init(localFileUrl, localFilePath, pathPrefix);
        }else if(storageType.equals(StorageTypeConst.QINIUYUN.getStorageType())){
            String accessKey = fileOSSProperties.getQiniuAccessKey();
            String secretKey = fileOSSProperties.getQiniuSecretKey();
            String qiniuBucketName = fileOSSProperties.getQiniuBucketName();
            String baseUrl = fileOSSProperties.getQiniuBasePath();
            res = new QiniuApiClient().init(accessKey, secretKey, qiniuBucketName, baseUrl, pathPrefix);
        }else if(storageType.equals(StorageTypeConst.ALIYUN.getStorageType())){
            String endpoint = fileOSSProperties.getAliyunEndpoint();
            String accessKeyId = fileOSSProperties.getAliyunAccessKey();
            String accessKeySecret = fileOSSProperties.getAliyunAccessKeySecret();
            String url = fileOSSProperties.getAliyunFileUrl();
            String aliYunBucketName = fileOSSProperties.getAliyunBucketName();
            res = new AliyunOssApiClient().init(endpoint, accessKeyId, accessKeySecret, url, aliYunBucketName, pathPrefix);
        }else{
            throw new ServiceException("[文件服务]请选择文件存储类型！");
        }
        if (null == res) {
            throw new GlobalFileException("[文件服务]当前系统暂未配置文件服务相关的内容！");
        }
        return res;
    }
}
