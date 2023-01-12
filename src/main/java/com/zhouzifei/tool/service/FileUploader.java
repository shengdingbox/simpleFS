package com.zhouzifei.tool.service;

import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.config.FileProperties;
import com.zhouzifei.tool.config.SimpleFsProperties;
import com.zhouzifei.tool.consts.StorageTypeConst;
import com.zhouzifei.tool.fileClient.*;
import com.zhouzifei.tool.listener.ProgressListener;
import com.zhouzifei.tool.util.StringUtils;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
 * @since 1.0
 */
@Builder
public class FileUploader {


    SimpleFsProperties simpleFsProperties;
    ProgressListener progressListener;
    private String domainUrl;
    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String region;
    private String bucketName;
    public String storageType;

    public ApiClient execute() {
        final BaseApiClient apiClient = getApiClient();
        this.storageType = StringUtils.isEmpty(storageType)?simpleFsProperties.getStorageType():storageType;
        final FileProperties fileProperties = getFileProperties();
        return apiClient.init(fileProperties);
    }

    private BaseApiClient getApiClient() {
        if (StorageTypeConst.LOCAL.getStorageType().equals(storageType)) {
            return new LocalApiClient();
        } else if (StorageTypeConst.QINIUYUN.getStorageType().equals(storageType)) {
            return new QiniuApiClient();
        } else if (StorageTypeConst.ALIYUN.getStorageType().equals(storageType)) {
            return new AliyunOssApiClient();
        } else if (StorageTypeConst.AWSS3.getStorageType().equals(storageType)) {
            return new AwsS3ApiClient();
        } else if (StorageTypeConst.BAIDUBOS.getStorageType().equals(storageType)) {
            return new BaiduBosApiClient();
        } else if (StorageTypeConst.YOUPAIYUN.getStorageType().equals(storageType)) {
            return new UpaiyunOssApiClient();
        } else if (StorageTypeConst.TENGXUNYUN.getStorageType().equals(storageType)) {
            return new QCloudOssApiClient();
        } else if (StorageTypeConst.HUAWEIYUN.getStorageType().equals(storageType)) {
            return new HuaweiCloudOssApiClient();
        } else if (StorageTypeConst.FASTDFS.getStorageType().equals(storageType)) {
            return new FastDfsOssApiClient();
        } else if (StorageTypeConst.SMMS.getStorageType().equals(storageType)) {
            return new SmMsApiClient();
        } else if (StorageTypeConst.GITHUB.getStorageType().equals(storageType)) {
            return new GithubApiClient();
        } else {
            throw new ServiceException("[文件服务]请选择文件存储类型！");
        }
    }

    private FileProperties getFileProperties() {
        if (StorageTypeConst.LOCAL.getStorageType().equals(storageType)) {
            return simpleFsProperties.getLocal();
        } else if (StorageTypeConst.QINIUYUN.getStorageType().equals(storageType)) {
            return simpleFsProperties.getQiniu();
        } else if (StorageTypeConst.ALIYUN.getStorageType().equals(storageType)) {
            return simpleFsProperties.getOss();
        } else if (StorageTypeConst.AWSS3.getStorageType().equals(storageType)) {
            return simpleFsProperties.getAws();
        } else if (StorageTypeConst.BAIDUBOS.getStorageType().equals(storageType)) {
            return simpleFsProperties.getBos();
        } else if (StorageTypeConst.YOUPAIYUN.getStorageType().equals(storageType)) {
            return simpleFsProperties.getUpai();
        } else if (StorageTypeConst.TENGXUNYUN.getStorageType().equals(storageType)) {
            return simpleFsProperties.getTengxun();
        } else if (StorageTypeConst.HUAWEIYUN.getStorageType().equals(storageType)) {
            return simpleFsProperties.getHuawei();
        } else if (StorageTypeConst.FASTDFS.getStorageType().equals(storageType)) {
            return simpleFsProperties.getFast();
        } else if (StorageTypeConst.SMMS.getStorageType().equals(storageType)) {
            return simpleFsProperties.getSmms();
        } else if (StorageTypeConst.GITHUB.getStorageType().equals(storageType)) {
            return simpleFsProperties.getGithub();
        } else {
            throw new ServiceException("[文件服务]请选择文件存储类型！");
        }
    }
}
