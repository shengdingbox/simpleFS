package com.zhouzifei.tool.service;

import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.config.FileProperties;
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
            String localUrl = fileProperties.getLocalUrl();
            String localFilePath = fileProperties.getLocalFilePath();
            final LocalApiClient localApiClient = new LocalApiClient().init(localUrl, localFilePath);
            return localApiClient.setProgressListener(progressListener);
        }else if(StorageTypeConst.QINIUYUN.getStorageType().equals(storageType)){
            String qiniuAccessKey = fileProperties.getQiniuAccessKey();
            String qiniuSecretKey = fileProperties.getQiniuSecretKey();
            String qiniuBucketName = fileProperties.getQiniuBucketName();
            String qiniuUrl = fileProperties.getQiniuUrl();
            return new QiniuApiClient().init(qiniuAccessKey, qiniuSecretKey, qiniuBucketName, qiniuUrl);
        }else if(StorageTypeConst.ALIYUN.getStorageType().equals(storageType)){
            String aliEndpoint = fileProperties.getAliEndpoint();
            String aliAccessKey = fileProperties.getAliAccessKey();
            String aliSecretKey = fileProperties.getAliSecretKey();
            String aliUrl = fileProperties.getAliUrl();
            String aliBucketName = fileProperties.getAliBucketName();
            return new AliyunOssApiClient().init(aliEndpoint, aliAccessKey, aliSecretKey, aliUrl, aliBucketName);
        }else if(StorageTypeConst.YOUPAIYUN.getStorageType().equals(storageType)) {
            String uPaiUserName = fileProperties.getUPaiUserName();
            String uPaiPassWord = fileProperties.getUPaiPassWord();
            String uPaiUrl = fileProperties.getUPaiUrl();
            String uPaiBucketName = fileProperties.getUPaiBucketName();
            return new UpaiyunOssApiClient().init(uPaiUserName, uPaiPassWord,uPaiBucketName,uPaiUrl);
        }else if(StorageTypeConst.TENGXUNYUN.getStorageType().equals(storageType)) {
            String qCloudAccessKey = fileProperties.getQCloudAccessKey();
            String qCloudSecretKey = fileProperties.getQCloudSecretKey();
            String qCloudEndpoint = fileProperties.getQCloudEndpoint();
            String qCloudUrl = fileProperties.getQCloudUrl();
            String qCloudBucketName = fileProperties.getQCloudBucketName();
            return new QCloudOssApiClient().init(qCloudAccessKey, qCloudSecretKey,qCloudEndpoint,qCloudBucketName,qCloudUrl);
        }else if(StorageTypeConst.HUAWEIYUN.getStorageType().equals(storageType)) {
            String huaweiAccessKey = fileProperties.getHuaweiAccessKey();
            String huaweiSecretKey = fileProperties.getHuaweiSecretKey();
            String huaweiEndpoint = fileProperties.getHuaweiEndpoint();
            String huaweiUrl = fileProperties.getHuaweiUrl();
            String huaweiBucketName = fileProperties.getHuaweiBucketName();
            return new HuaweiCloudOssApiClient().init(huaweiAccessKey, huaweiSecretKey,huaweiEndpoint,huaweiBucketName,huaweiUrl);
        }else if(StorageTypeConst.FASTDFS.getStorageType().equals(storageType)) {
            String fastDFSServerUrl = fileProperties.getFastDFSServerUrl();
            String fastDFSUrl = fileProperties.getFastDFSUrl();
            return new FastDfsOssApiClient().init(fastDFSServerUrl, fastDFSUrl);
        }else if(StorageTypeConst.SMMS.getStorageType().equals(storageType)) {
            String smmsUserName = fileProperties.getSmmsUserName();
            String smmsPassWord = fileProperties.getSmmsPassWord();
            String smmsToken = fileProperties.getSmmsToken();
            return new SmMsApiClient().init(smmsUserName, smmsPassWord, smmsToken);
        }else if(StorageTypeConst.XMLY.getStorageType().equals(storageType)) {
            String xmlyCookie = fileProperties.getXmlyCookie();
            return new XMLYApiClient().init(xmlyCookie);
        }else{
            throw new ServiceException("[文件服务]请选择文件存储类型！");
        }
    }
}
