package com.zhouzifei.tool.util.media.images.service;


import com.zhouzifei.tool.consts.ConfigKeyEnum;
import com.zhouzifei.tool.exception.ServiceException;
import com.zhouzifei.tool.exception.GlobalFileException;
import com.zhouzifei.tool.util.media.images.FileClient.AliyunOssApiClient;
import com.zhouzifei.tool.util.media.images.FileClient.LocalApiClient;
import com.zhouzifei.tool.util.media.images.FileClient.QiniuApiClient;
import org.springframework.util.StringUtils;

import java.util.Map;


/**
 *
 * @author Dabao (17600004572@163.com)
 * @version 1.0
 * @website https://www.zhouzifei.com
 * @date 2019年7月16日
 * @since 1.0
 */
public class BaseFileUploader {

    protected ApiClient getApiClient(String uploadType, Map<String, Object> config) {
        String storageType = null;
        if (null == config || StringUtils.isEmpty((storageType = (String) config.get(ConfigKeyEnum.STORAGE_TYPE.getKey())))) {
            throw new ServiceException("[文件服务]当前系统暂未配置文件服务相关的内容！");
        }

        ApiClient res = null;
        switch (storageType) {
            case "local":
                String localFileUrl = (String) config.get(ConfigKeyEnum.LOCAL_FILE_URL.getKey()),
                        localFilePath = (String) config.get(ConfigKeyEnum.LOCAL_FILE_PATH.getKey());
                res = new LocalApiClient().init(localFileUrl, localFilePath, uploadType);
                break;
            case "qiniu":
                String accessKey = (String) config.get(ConfigKeyEnum.QINIU_ACCESS_KEY.getKey()),
                        secretKey = (String) config.get(ConfigKeyEnum.QINIU_SECRET_KEY.getKey()),
                        qiniuBucketName = (String) config.get(ConfigKeyEnum.QINIU_BUCKET_NAME.getKey()),
                        baseUrl = (String) config.get(ConfigKeyEnum.QINIU_BASE_PATH.getKey());
                res = new QiniuApiClient().init(accessKey, secretKey, qiniuBucketName, baseUrl, uploadType);
                break;
            case "aliyun":
                String endpoint = (String) config.get(ConfigKeyEnum.ALIYUN_ENDPOINT.getKey()),
                        accessKeyId = (String) config.get(ConfigKeyEnum.ALIYUN_ACCESS_KEY.getKey()),
                        accessKeySecret = (String) config.get(ConfigKeyEnum.ALIYUN_ACCESS_KEY_SECRET.getKey()),
                        url = (String) config.get(ConfigKeyEnum.ALIYUN_FILE_URL.getKey()),
                        aliYunBucketName = (String) config.get(ConfigKeyEnum.ALIYUN_BUCKET_NAME.getKey());
                res = new AliyunOssApiClient().init(endpoint, accessKeyId, accessKeySecret, url, aliYunBucketName, uploadType);
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
