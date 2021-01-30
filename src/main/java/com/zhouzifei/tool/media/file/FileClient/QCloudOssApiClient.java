package com.zhouzifei.tool.media.file.FileClient;


import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.exception.OssApiException;
import com.zhouzifei.tool.exception.QiniuApiException;
import com.zhouzifei.tool.exception.ServiceException;
import com.zhouzifei.tool.media.file.FileUtil;
import com.zhouzifei.tool.media.file.service.UpaiManager;
import com.zhouzifei.tool.util.StringUtils;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 周子斐
 * @date 2021/1/30
 * @Description
 */
public class QCloudOssApiClient extends BaseApiClient {

    private static final String DEFAULT_PREFIX = "Qcloud/";
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String path;
    private String pathPrefix;
    private String endpoint;

    public QCloudOssApiClient() {
        super("腾讯云");
    }

    public QCloudOssApiClient init(String accessKey, String secretKey,String endpoint, String bucketName, String baseUrl, String uploadType) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucket = bucketName;
        this.path = baseUrl;
        this.endpoint = endpoint;
        this.pathPrefix = StringUtils.isNullOrEmpty(uploadType) ? DEFAULT_PREFIX : uploadType.endsWith("/") ? uploadType : uploadType + "/";
        return this;
    }

    @Override
    protected void check() {
        if (StringUtils.isNullOrEmpty(this.accessKey) || StringUtils.isNullOrEmpty(this.secretKey) || StringUtils.isNullOrEmpty(this.bucket)) {
            throw new QiniuApiException("[" + this.storageType + "]尚未配置腾讯云，文件上传功能暂时不可用！");
        }
    }

    @Override
    public VirtualFile uploadImg(InputStream is, String imageUrl) {
        this.check();
        COSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);
        Region region = new Region(endpoint);
        ClientConfig clientConfig = new ClientConfig(region);
        COSClient cosClient = new COSClient(cred, clientConfig);
        Date startTime = new Date();
        String key = FileUtil.generateTempFileName(imageUrl);
        this.createNewFileName(key, this.pathPrefix);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, this.newFileName, is,objectMetadata);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        return new VirtualFile()
                .setOriginalFileName(key)
                .setSuffix(this.suffix)
                .setUploadStartTime(startTime)
                .setUploadEndTime(new Date())
                .setFilePath(this.newFileName)
                .setFileHash(putObjectResult.getCrc64Ecma())
                .setFullFilePath(this.path + this.newFileName);
    }

    @Override
    public boolean removeFile(String key) {
        this.check();
        if (StringUtils.isNullOrEmpty(key)) {
            throw new OssApiException("[" + this.storageType + "]删除文件失败：文件key为空");
        }
        COSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);
        Region region = new Region(endpoint);
        ClientConfig clientConfig = new ClientConfig(region);
        COSClient cosClient = new COSClient(cred, clientConfig);
        // 删除文件
        cosClient.deleteObject(bucket, key);
        return true;
    }
}
