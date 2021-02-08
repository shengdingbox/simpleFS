package com.zhouzifei.tool.media.file.fileClient;


import com.obs.services.ObsClient;
import com.obs.services.model.DeleteObjectResult;
import com.obs.services.model.ObsObject;
import com.obs.services.model.PutObjectResult;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.exception.OssApiException;
import com.zhouzifei.tool.exception.QiniuApiException;
import com.zhouzifei.tool.media.file.FileUtil;
import com.zhouzifei.tool.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * @author 周子斐
 * @date 2021/1/30
 * @Description
 */
public class HuaweiCloudOssApiClient extends BaseApiClient {

    private static final String DEFAULT_PREFIX = "huaweiCloud/";
    private ObsClient obsClient;
    private String bucket;
    private String path;
    private String pathPrefix;

    public HuaweiCloudOssApiClient() {
        super("华为云");
    }

    public HuaweiCloudOssApiClient init(String accessKey, String secretKey, String endpoint, String bucketName, String baseUrl, String uploadType) {
        if (StringUtils.isNullOrEmpty(accessKey) || StringUtils.isNullOrEmpty(secretKey) || StringUtils.isNullOrEmpty(endpoint)) {
            throw new QiniuApiException("[" + this.storageType + "]尚未配置华为云，文件上传功能暂时不可用！");
        }
        // 创建ObsClient实例
        obsClient = new ObsClient(accessKey, secretKey, endpoint);
        this.bucket = bucketName;
        this.path = baseUrl;
        this.pathPrefix = StringUtils.isNullOrEmpty(uploadType) ? DEFAULT_PREFIX : uploadType.endsWith("/") ? uploadType : uploadType + "/";
        return this;
    }

    @Override
    public VirtualFile uploadFile(InputStream is, String fileName) {
        try {
            Date startTime = new Date();
            String key = FileUtil.generateTempFileName(fileName);
            this.createNewFileName(key);
            PutObjectResult putObjectResult = obsClient.putObject(bucket, this.newFileName, is);
            return new VirtualFile()
                    .setOriginalFileName(key)
                    .setSuffix(this.suffix)
                    .setUploadStartTime(startTime)
                    .setUploadEndTime(new Date())
                    .setFilePath(this.newFileName)
                    .setFileHash(putObjectResult.getObjectUrl())
                    .setFullFilePath(this.path + this.newFileName);
        } finally {
            try {
                obsClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean removeFile(String key) {
        if (StringUtils.isNullOrEmpty(key)) {
            throw new OssApiException("[" + this.storageType + "]删除文件失败：文件key为空");
        }
        // 删除文件
        DeleteObjectResult deleteObjectResult = obsClient.deleteObject(bucket, key);
        return deleteObjectResult.isDeleteMarker();
    }

    @Override
    public VirtualFile multipartUpload(File file) {
        return null;
    }

    @Override
    protected void check() {

    }

    @Override
    public InputStream downloadFileStream(String key) {
        ObsObject obsObject = obsClient.getObject(bucket, key);
        System.out.println("Object content:");
        return obsObject.getObjectContent();

    }
}
