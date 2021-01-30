package com.zhouzifei.tool.media.file.FileClient;


import com.obs.services.ObsClient;
import com.obs.services.model.PutObjectResult;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.exception.OssApiException;
import com.zhouzifei.tool.exception.QiniuApiException;
import com.zhouzifei.tool.media.file.FileUtil;
import com.zhouzifei.tool.util.StringUtils;

import java.io.InputStream;
import java.util.Date;

/**
 * @author 周子斐
 * @date 2021/1/30
 * @Description
 */
public class HuaweiCloudOssApiClient extends BaseApiClient {

    private static final String DEFAULT_PREFIX = "huaweiCloud/";
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String path;
    private String pathPrefix;
    private String endpoint;

    public HuaweiCloudOssApiClient() {
        super("华为云");
    }

    public HuaweiCloudOssApiClient init(String accessKey, String secretKey, String endpoint, String bucketName, String baseUrl, String uploadType) {
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
            throw new QiniuApiException("[" + this.storageType + "]尚未配置华为云，文件上传功能暂时不可用！");
        }
    }

    @Override
    public VirtualFile uploadImg(InputStream is, String imageUrl) {
        this.check();
        // 创建ObsClient实例
        ObsClient obsClient = new ObsClient(accessKey, secretKey, endpoint);
        Date startTime = new Date();
        String key = FileUtil.generateTempFileName(imageUrl);
        this.createNewFileName(key, this.pathPrefix);
        PutObjectResult putObjectResult = obsClient.putObject(bucket, this.newFileName, is);
        return new VirtualFile()
                .setOriginalFileName(key)
                .setSuffix(this.suffix)
                .setUploadStartTime(startTime)
                .setUploadEndTime(new Date())
                .setFilePath(this.newFileName)
                .setFileHash(putObjectResult.getObjectUrl())
                .setFullFilePath(this.path + this.newFileName);
    }

    @Override
    public boolean removeFile(String key) {
        this.check();
        if (StringUtils.isNullOrEmpty(key)) {
            throw new OssApiException("[" + this.storageType + "]删除文件失败：文件key为空");
        }
        ObsClient obsClient = new ObsClient(accessKey, secretKey, endpoint);
        // 删除文件
        obsClient.deleteObject(bucket, key);
        return true;
    }
}
