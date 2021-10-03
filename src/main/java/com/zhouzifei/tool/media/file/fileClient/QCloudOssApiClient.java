package com.zhouzifei.tool.media.file.fileClient;


import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.html.util.Randoms;
import com.zhouzifei.tool.media.file.util.FileUtil;
import com.zhouzifei.tool.util.StringUtils;

import java.io.*;
import java.util.Date;

/**
 * @author 周子斐
 * @date 2021/1/30
 * @Description
 */
public class QCloudOssApiClient extends BaseApiClient {

    private COSClient cosClient;
    private String bucket;
    private String path;

    public QCloudOssApiClient() {
        super("腾讯云");
    }

    public QCloudOssApiClient init(String accessKey, String secretKey,String endpoint, String bucketName, String baseUrl, String uploadType) {
        if (StringUtils.isNullOrEmpty(accessKey) || StringUtils.isNullOrEmpty(secretKey) || StringUtils.isNullOrEmpty(bucketName)) {
            throw new ServiceException("[" + this.storageType + "]尚未配置腾讯云，文件上传功能暂时不可用！");
        }
        COSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);
        Region region = new Region(endpoint);
        ClientConfig clientConfig = new ClientConfig(region);
        cosClient = new COSClient(cred, clientConfig);
        this.bucket = bucketName;
        this.path = baseUrl;
        super.folder = StringUtils.isEmpty(uploadType) ? "" : uploadType + "/";
        return this;
    }

    @Override
    public VirtualFile uploadFile(InputStream is, String fileName) {
        Date startTime = new Date();
        final boolean exists = cosClient.doesObjectExist(bucket, fileName);
        if(exists){
            this.suffix = FileUtil.getSuffix(fileName);
            fileName = Randoms.alpha(16) + this.suffix;
        }
        this.createNewFileName(fileName);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, this.newFileName, is,objectMetadata);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        return new VirtualFile()
                .setOriginalFileName(fileName)
                .setSuffix(this.suffix)
                .setUploadStartTime(startTime)
                .setUploadEndTime(new Date())
                .setFilePath(this.newFileName)
                .setFileHash(putObjectResult.getCrc64Ecma())
                .setFullFilePath(this.path + this.newFileName);
    }

    @Override
    public boolean removeFile(String key) {
        if (StringUtils.isNullOrEmpty(key)) {
            throw new ServiceException("[" + this.storageType + "]删除文件失败：文件key为空");
        }
        // 删除文件
        cosClient.deleteObject(bucket, key);
        return true;
    }

    @Override
    public VirtualFile multipartUpload(File file) {
        return null;
    }

    @Override
    public VirtualFile multipartUpload(InputStream inputStream, String fileName) {
        return null;
    }

    @Override
    protected void check() {

    }

    @Override
    public InputStream downloadFileStream(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, key);
        COSObject object = cosClient.getObject(getObjectRequest);
        return object.getObjectContent();
    }
}
