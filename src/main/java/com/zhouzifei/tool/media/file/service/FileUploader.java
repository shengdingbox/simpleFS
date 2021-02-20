package com.zhouzifei.tool.media.file.service;

import com.zhouzifei.tool.config.properties.FileProperties;
import com.zhouzifei.tool.consts.StorageTypeConst;
import com.zhouzifei.tool.exception.ServiceException;
import com.zhouzifei.tool.media.file.fileClient.*;
import com.zhouzifei.tool.media.file.listener.ProgressListener;


/**
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
 * @since 1.0
 */
public class FileUploader {

    ProgressListener progressListener;

    public FileUploader setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }
    public ApiClient getApiClient(FileProperties fileProperties) {
        String storageType = fileProperties.getStorageTypeConst().getStorageType();
        if(storageType.equals(StorageTypeConst.LOCAL.getStorageType())) {
            String domainUrl = fileProperties.getDomainUrl();
            String localFilePath = fileProperties.getLocalFilePath();
            final LocalApiClient localApiClient = new LocalApiClient().init(domainUrl, localFilePath, fileProperties.getPathPrefix());
            return localApiClient.setProgressListener(progressListener);
        }else if(storageType.equals(StorageTypeConst.QINIUYUN.getStorageType())){
            String accessKey = fileProperties.getAccessId();
            String secretKey = fileProperties.getSecretKey();
            String bucketName = fileProperties.getBucketName();
            String baseUrl = fileProperties.getDomainUrl();
            return new QiniuApiClient().init(accessKey, secretKey, bucketName, baseUrl, fileProperties.getPathPrefix());
        }else if(storageType.equals(StorageTypeConst.ALIYUN.getStorageType())){
            String endpoint = fileProperties.getEndpoint();
            String accessKeyId = fileProperties.getAccessId();
            String accessKeySecret = fileProperties.getSecretKey();
            String domainUrl = fileProperties.getDomainUrl();
            String bucketName = fileProperties.getBucketName();
            return new AliyunOssApiClient().init(endpoint, accessKeyId, accessKeySecret, domainUrl, bucketName,fileProperties.getPathPrefix());
        }else if(storageType.equals(StorageTypeConst.YOUPAIYUN.getStorageType())) {
            String operatorName = fileProperties.getOperatorName();
            String operatorPwd = fileProperties.getOperatorPwd();
            String url = fileProperties.getDomainUrl();
            String bucketName = fileProperties.getBucketName();
            return new UpaiyunOssApiClient().init(operatorName, operatorPwd,bucketName,url, fileProperties.getPathPrefix());
        }else if(storageType.equals(StorageTypeConst.TENGXUNYUN.getStorageType())) {
            String accessKeyId = fileProperties.getAccessId();
            String accessKeySecret = fileProperties.getSecretKey();
            String endpoint = fileProperties.getEndpoint();
            String url = fileProperties.getDomainUrl();
            String bucketName = fileProperties.getBucketName();
            return new QCloudOssApiClient().init(accessKeyId, accessKeySecret,endpoint,bucketName,url, fileProperties.getPathPrefix());
        }else if(storageType.equals(StorageTypeConst.HUAWEIYUN.getStorageType())) {
            String accessKeyId = fileProperties.getAccessId();
            String accessKeySecret = fileProperties.getSecretKey();
            String endpoint = fileProperties.getEndpoint();
            String url = fileProperties.getDomainUrl();
            String bucketName = fileProperties.getBucketName();
            return new HuaweiCloudOssApiClient().init(accessKeyId, accessKeySecret,endpoint,bucketName,url, fileProperties.getPathPrefix());
        }else{
            throw new ServiceException("[文件服务]请选择文件存储类型！");
        }
    }
}
