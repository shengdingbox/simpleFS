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
            if (!fileProperties.getLocalOpen()) {
                throw new ServiceException("[" + storageType + "]尚未开启，文件功能暂时不可用！");
            }
            final LocalApiClient localApiClient = new LocalApiClient().init(localUrl, localFilePath);
            return localApiClient.setProgressListener(progressListener);
        }else if(StorageTypeConst.QINIUYUN.getStorageType().equals(storageType)){
            String qiniuAccessKey = fileProperties.getQiniuAccessKey();
            String qiniuSecretKey = fileProperties.getQiniuSecretKey();
            String qiniuBucketName = fileProperties.getQiniuBucketName();
            String qiniuUrl = fileProperties.getQiniuUrl();
            if (!fileProperties.getQiniuOpen()) {
                throw new ServiceException("[" + storageType + "]尚未开启，文件功能暂时不可用！");
            }
            return new QiniuApiClient().init(qiniuAccessKey, qiniuSecretKey, qiniuBucketName, qiniuUrl);
        }else if(StorageTypeConst.ALIYUN.getStorageType().equals(storageType)){
            String aliEndpoint = fileProperties.getAliEndpoint();
            String aliAccessKey = fileProperties.getAliAccessKey();
            String aliSecretKey = fileProperties.getAliSecretKey();
            String aliUrl = fileProperties.getAliUrl();
            String aliBucketName = fileProperties.getAliBucketName();
            if (!fileProperties.getAliOpen()) {
                throw new ServiceException("[" + storageType + "]尚未开启，文件功能暂时不可用！");
            }
            return new AliyunOssApiClient().init(aliEndpoint, aliAccessKey, aliSecretKey, aliUrl, aliBucketName);
        }else if(StorageTypeConst.YOUPAIYUN.getStorageType().equals(storageType)) {
            String uPaiUserName = fileProperties.getUPaiUserName();
            String uPaiPassWord = fileProperties.getUPaiPassWord();
            String uPaiUrl = fileProperties.getUPaiUrl();
            String uPaiBucketName = fileProperties.getUPaiBucketName();
            if (!fileProperties.getUPaiOpen()) {
                throw new ServiceException("[" + storageType + "]尚未开启，文件功能暂时不可用！");
            }
            return new UpaiyunOssApiClient().init(uPaiUserName, uPaiPassWord,uPaiBucketName,uPaiUrl);
        }else if(StorageTypeConst.TENGXUNYUN.getStorageType().equals(storageType)) {
            String qCloudAccessKey = fileProperties.getQCloudAccessKey();
            String qCloudSecretKey = fileProperties.getQCloudSecretKey();
            String qCloudEndpoint = fileProperties.getQCloudEndpoint();
            String qCloudUrl = fileProperties.getQCloudUrl();
            String qCloudBucketName = fileProperties.getQCloudBucketName();
            if (!fileProperties.getQCloudOpen()) {
                throw new ServiceException("[" + storageType + "]尚未开启，文件功能暂时不可用！");
            }
            return new QCloudOssApiClient().init(qCloudAccessKey, qCloudSecretKey,qCloudEndpoint,qCloudBucketName,qCloudUrl);
        }else if(StorageTypeConst.HUAWEIYUN.getStorageType().equals(storageType)) {
            String huaweiAccessKey = fileProperties.getHuaweiAccessKey();
            String huaweiSecretKey = fileProperties.getHuaweiSecretKey();
            String huaweiEndpoint = fileProperties.getHuaweiEndpoint();
            String huaweiUrl = fileProperties.getHuaweiUrl();
            String huaweiBucketName = fileProperties.getHuaweiBucketName();
            if (!fileProperties.getHuaweiOpen()) {
                throw new ServiceException("[" + storageType + "]尚未开启，文件功能暂时不可用！");
            }
            return new HuaweiCloudOssApiClient().init(huaweiAccessKey, huaweiSecretKey,huaweiEndpoint,huaweiBucketName,huaweiUrl);
        }else if(StorageTypeConst.FASTDFS.getStorageType().equals(storageType)) {
            String fastDFSServerUrl = fileProperties.getFastDFSServerUrl();
            String fastDFSUrl = fileProperties.getFastDFSUrl();
            if (!fileProperties.getFastDFSOpen()) {
                throw new ServiceException("[" + storageType + "]尚未开启，文件功能暂时不可用！");
            }
            return new FastDfsOssApiClient().init(fastDFSServerUrl, fastDFSUrl);
        }else if(StorageTypeConst.SMMS.getStorageType().equals(storageType)) {
            String smmsUserName = fileProperties.getSmmsUserName();
            String smmsPassWord = fileProperties.getSmmsPassWord();
            String smmsToken = fileProperties.getSmmsToken();
            if (!fileProperties.getSmmsOpen()) {
                throw new ServiceException("[" + storageType + "]尚未开启，文件功能暂时不可用！");
            }
            return new SmMsApiClient().init(smmsUserName, smmsPassWord, smmsToken);
        }else if(StorageTypeConst.XMLY.getStorageType().equals(storageType)) {
            String xmlyCookie = fileProperties.getXmlyCookie();
            if (!fileProperties.getXmlyOpen()) {
                throw new ServiceException("[" + storageType + "]尚未开启，文件功能暂时不可用！");
            }
            return new XMLYApiClient().init(xmlyCookie);
        }else if(StorageTypeConst.GITHUB.getStorageType().equals(storageType)) {
            String githubRepository = fileProperties.getGithubRepository();
            String githubToken = fileProperties.getGithubToken();
            String githubUser = fileProperties.getGithubUser();
            if (!fileProperties.getXmlyOpen()) {
                throw new ServiceException("[" + storageType + "]尚未开启，文件功能暂时不可用！");
            }
            return new GithubApiClient().init(githubToken,githubUser,githubRepository);
        }else{
            throw new ServiceException("[文件服务]请选择文件存储类型！");
        }
    }
}
