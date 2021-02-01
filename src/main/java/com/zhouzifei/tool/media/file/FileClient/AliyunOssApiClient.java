package com.zhouzifei.tool.media.file.FileClient;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.exception.OssApiException;
import com.zhouzifei.tool.exception.QiniuApiException;
import com.zhouzifei.tool.media.file.FileUtil;
import com.zhouzifei.tool.media.file.StreamUtil;
import com.zhouzifei.tool.util.StringUtils;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月23日
 * @since 1.0
 */
public class AliyunOssApiClient extends BaseApiClient {
    private static final String DEFAULT_PREFIX = "blog/";
    private OSSClient client;
    private String url;
    private String bucketName;
    private String pathPrefix;

    public AliyunOssApiClient() {
        super("阿里云OSS");
    }

    public AliyunOssApiClient init(String endpoint, String accessKey, String secretKey, String url, String bucketName, String uploadType) {
        if (StringUtils.isNullOrEmpty(accessKey) || StringUtils.isNullOrEmpty(secretKey) || StringUtils.isNullOrEmpty(this.bucketName)) {
            throw new QiniuApiException("[" + this.storageType + "]尚未配置阿里云，文件上传功能暂时不可用！");
        }
        this.client = new OSSClient(endpoint, accessKey, secretKey);
        this.url = url;
        this.bucketName = bucketName;
        this.pathPrefix = StringUtils.isEmpty(uploadType) ? DEFAULT_PREFIX : uploadType.endsWith("/") ? uploadType : uploadType + "/";
        return this;
    }

    @Override
    public VirtualFile uploadFile(InputStream is, String imageUrl) {
        String key = FileUtil.generateTempFileName(imageUrl);
        this.createNewFileName(key, this.pathPrefix);
        Date startTime = new Date();
        try (InputStream uploadIs = StreamUtil.clone(is);
             InputStream fileHashIs = StreamUtil.clone(is)) {
            if (!this.client.doesBucketExist(bucketName)) {
                throw new OssApiException("[阿里云OSS] 无法上传文件！Bucket不存在：" + bucketName);
            }
            this.client.putObject(bucketName, this.newFileName, uploadIs);
            return new VirtualFile()
                    .setOriginalFileName(FileUtil.getName(key))
                    .setSuffix(this.suffix)
                    .setUploadStartTime(startTime)
                    .setUploadEndTime(new Date())
                    .setFilePath(this.newFileName)
                    .setFileHash(DigestUtils.md5DigestAsHex(fileHashIs))
                    .setFullFilePath(this.url + this.newFileName);
        } catch (IOException e) {
            throw new OssApiException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.client.shutdown();
        }
    }
    /**
     * 删除文件
     *
     * @param fileName   OSS中保存的文件名
     */
    @Override
    public boolean removeFile(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            throw new OssApiException("[" + this.storageType + "]删除文件失败：文件key为空");
        }
        try {
            boolean exists = this.client.doesBucketExist(bucketName);
            if (!exists) {
                throw new OssApiException("[阿里云OSS] 文件删除失败！Bucket不存在：" + bucketName);
            }
            if (!this.client.doesObjectExist(bucketName, fileName)) {
                throw new OssApiException("[阿里云OSS] 文件删除失败！文件不存在：" + bucketName + "/" + fileName);
            }
            this.client.deleteObject(bucketName, fileName);
            return true;
        } catch (Exception e) {
            throw new OssApiException(e.getMessage());
        } finally {
            this.client.shutdown();
        }
    }
    /**
     * 下载文件
     *
     * @param fileName   OSS中保存的文件名
     */
    @Override
    public InputStream downloadFileStream(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            throw new OssApiException("[" + this.storageType + "]下载文件失败：文件key为空");
        }
        try {
            boolean exists = this.client.doesBucketExist(bucketName);
            if (!exists) {
                throw new OssApiException("[阿里云OSS] 文件删除失败！Bucket不存在：" + bucketName);
            }
            if (!this.client.doesObjectExist(bucketName, fileName)) {
                throw new OssApiException("[阿里云OSS] 文件下载失败！文件不存在：" + bucketName + "/" + fileName);
            }
            OSSObject object = this.client.getObject(bucketName, fileName);
            return object.getObjectContent();
        } finally {
            this.client.shutdown();
        }
    }
}
