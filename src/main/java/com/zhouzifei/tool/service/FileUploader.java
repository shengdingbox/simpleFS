package com.zhouzifei.tool.service;

import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.config.*;
import com.zhouzifei.tool.consts.StorageTypeConst;
import com.zhouzifei.tool.fileClient.*;
import com.zhouzifei.tool.listener.ProgressListener;
import org.springframework.stereotype.Component;


/**
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
 * @since 1.0
 */
@Component
public class FileUploader {

    ProgressListener progressListener;

    public ApiClient getApiClient(StorageTypeConst storageTypeConst,FileProperties fileProperties) {
        String storageType = storageTypeConst.getStorageType();
        if(StorageTypeConst.LOCAL.getStorageType().equals(storageType)) {
            final LocalApiClient localApiClient = new LocalApiClient(fileProperties);
            return localApiClient.setProgressListener(progressListener);
        }else if(StorageTypeConst.QINIUYUN.getStorageType().equals(storageType)){
            return new QiniuApiClient(fileProperties);
        }else if(StorageTypeConst.ALIYUN.getStorageType().equals(storageType)){
            return new AliyunOssApiClient(fileProperties);
        }else if(StorageTypeConst.AWSS3.getStorageType().equals(storageType)) {
            return new AwsS3ApiClient(fileProperties);}
        else if(StorageTypeConst.BAIDUBOS.getStorageType().equals(storageType)) {
            return new BaiduBosApiClient(fileProperties);}
        else if(StorageTypeConst.YOUPAIYUN.getStorageType().equals(storageType)) {
            return new UpaiyunOssApiClient(fileProperties);
        }else if(StorageTypeConst.TENGXUNYUN.getStorageType().equals(storageType)) {
            return new QCloudOssApiClient().init(fileProperties);
        }else if(StorageTypeConst.HUAWEIYUN.getStorageType().equals(storageType)) {
            return new HuaweiCloudOssApiClient().init(fileProperties);
        }else if(StorageTypeConst.FASTDFS.getStorageType().equals(storageType)) {
            return new FastDfsOssApiClient(fileProperties);
        }else if(StorageTypeConst.SMMS.getStorageType().equals(storageType)) {
            return new SmMsApiClient().init(fileProperties);
        }else if(StorageTypeConst.GITHUB.getStorageType().equals(storageType)) {
            return new GithubApiClient(fileProperties);
        }else{
            throw new ServiceException("[文件服务]请选择文件存储类型！");
        }
    }
}
