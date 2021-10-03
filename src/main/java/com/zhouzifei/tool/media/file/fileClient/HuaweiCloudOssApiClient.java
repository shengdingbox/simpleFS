package com.zhouzifei.tool.media.file.fileClient;


import com.obs.services.ObsClient;
import com.obs.services.model.DeleteObjectResult;
import com.obs.services.model.ObsObject;
import com.obs.services.model.PutObjectResult;
import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.html.util.Randoms;
import com.zhouzifei.tool.media.file.util.FileUtil;
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

    private ObsClient obsClient;
    private String bucket;
    private String path;

    public HuaweiCloudOssApiClient() {
        super("华为云");
    }

    public HuaweiCloudOssApiClient init(String accessKey, String secretKey, String endpoint, String bucketName, String baseUrl, String uploadType) {
        if (StringUtils.isNullOrEmpty(accessKey) || StringUtils.isNullOrEmpty(secretKey) || StringUtils.isNullOrEmpty(endpoint)) {
            throw new ServiceException("[" + this.storageType + "]尚未配置华为云，文件上传功能暂时不可用！");
        }
        // 创建ObsClient实例
        obsClient = new ObsClient(accessKey, secretKey, endpoint);
        this.bucket = bucketName;
        this.path = baseUrl;
        super.folder = StringUtils.isEmpty(uploadType) ? "" : uploadType + "/";
        return this;
    }

    @Override
    public VirtualFile uploadFile(InputStream is, String fileName) {
        try {
            Date startTime = new Date();
            final boolean exist = obsClient.doesObjectExist(bucket, fileName);
            if(exist){
                this.suffix = FileUtil.getSuffix(fileName);
                fileName = Randoms.alpha(16) + this.suffix;
            }
            this.createNewFileName(fileName);
            PutObjectResult putObjectResult = obsClient.putObject(bucket, this.newFileName, is);
            return new VirtualFile()
                    .setOriginalFileName(fileName)
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
    public boolean removeFile(String fileName) {
        if (StringUtils.isNullOrEmpty(fileName)) {
            throw new ServiceException("[" + this.storageType + "]删除文件失败：文件key为空");
        }
        final boolean exist = obsClient.doesObjectExist(bucket, fileName);
        if(!exist){
            throw new ServiceException("[阿里云OSS] 文件删除失败！文件不存在：" + bucket + "/" + fileName);
        }
        // 删除文件
        DeleteObjectResult deleteObjectResult = obsClient.deleteObject(bucket, fileName);
        return deleteObjectResult.isDeleteMarker();
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
        ObsObject obsObject = obsClient.getObject(bucket, key);
        System.out.println("Object content:");
        return obsObject.getObjectContent();

    }
}
