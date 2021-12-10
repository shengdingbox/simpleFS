package com.zhouzifei.tool.fileClient;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.ServiceException;
import com.aliyun.oss.model.*;
import com.zhouzifei.tool.consts.StorageTypeConst;
import com.zhouzifei.tool.dto.CheckFileResult;
import com.zhouzifei.tool.dto.VirtualFile;
import com.zhouzifei.tool.entity.MetaDataRequest;
import com.zhouzifei.tool.media.file.util.StreamUtil;
import com.zhouzifei.tool.util.FileUtil;
import com.zhouzifei.tool.util.RandomsUtil;
import com.zhouzifei.tool.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月23日
 * @since 1.0
 */
@Slf4j
public class AliyunOssApiClient extends BaseApiClient {

    private OSSClient client;
    private String domainUrl;
    private String bucketName;
    private String endpoint;
    private String accessKey;
    private String secretKey;

    public AliyunOssApiClient() {
        super("阿里云OSS");
    }

    public AliyunOssApiClient init(String endpoint, String accessKey, String secretKey, String domainUrl, String bucketName) {
        this.domainUrl = domainUrl;
        this.bucketName = bucketName;
        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        return this;
    }

    @Override
    public VirtualFile uploadFile(InputStream is, String imageUrl) {
        this.check();
        Date startTime = new Date();
        try (InputStream uploadIs = StreamUtil.clone(is);
             InputStream fileHashIs = StreamUtil.clone(is)) {
            imageUrl = getName(imageUrl);
            this.client.putObject(bucketName, this.newFileName, uploadIs);
            return new VirtualFile()
                    .setOriginalFileName(FileUtil.getName(imageUrl))
                    .setSuffix(this.suffix)
                    .setUploadStartTime(startTime)
                    .setUploadEndTime(new Date())
                    .setFilePath(this.newFileName)
                    .setFileHash(DigestUtils.md5DigestAsHex(fileHashIs))
                    .setFullFilePath(this.domainUrl + this.newFileName);
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        } finally {
            this.client.shutdown();
        }
    }

    private String getName(String fileName) {
        if (!this.client.doesBucketExist(bucketName)) {
            throw new ServiceException("[阿里云OSS] 无法上传文件！Bucket不存在：" + bucketName);
        }
        boolean exists = this.client.doesObjectExist(bucketName, fileName);
        if (exists) {
            this.suffix = FileUtil.getSuffix(fileName);
            fileName = RandomsUtil.alpha(16) + this.suffix;
        }
        this.createNewFileName(fileName);
        return fileName;
    }

    /**
     * 删除文件
     *
     * @param fileName OSS中保存的文件名
     */
    @Override
    public boolean removeFile(String fileName) {
        this.check();
        if (StringUtils.isEmpty(fileName)) {
            throw new ServiceException("[" + this.storageType + "]删除文件失败：文件key为空");
        }
        try {
            boolean exists = this.client.doesBucketExist(bucketName);
            if (!exists) {
                throw new ServiceException("[阿里云OSS] 文件删除失败！Bucket不存在：" + bucketName);
            }
            if (!this.client.doesObjectExist(bucketName, fileName)) {
                throw new ServiceException("[阿里云OSS] 文件删除失败！文件不存在：" + bucketName + "/" + fileName);
            }
            this.client.deleteObject(bucketName, fileName);
            return true;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        } finally {
            this.client.shutdown();
        }
    }
    @Override
    public VirtualFile multipartUpload(InputStream inputStream, MetaDataRequest metaDataRequest) {
        final Date startDate = new Date();
        this.check();
        final String md5 = metaDataRequest.getFileMd5();
        final Integer name = metaDataRequest.getName();
        String fileName = getName(String.valueOf(name));
        final Integer chunkSize = metaDataRequest.getChunkSize();
        final Integer chunk = metaDataRequest.getChunk();
        synchronized (LOCK) {
            final String storageType = StorageTypeConst.ALIYUN.getStorageType();
            final Object o = super.cacheEngine.get(storageType, md5);
            if (Objects.isNull(o)) {
                InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, fileName);
                InitiateMultipartUploadResult initiateMultipartUploadResult = client.initiateMultipartUpload(request);
                cacheEngine.add(storageType, md5, initiateMultipartUploadResult.getUploadId());
                cacheEngine.add(storageType, md5 + SLASH + fileName, fileName);
                cacheEngine.add(storageType, md5 + SLASH + TAG, new CopyOnWriteArrayList<>());
            }
        }
        UploadPartRequest uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setBucketName(bucketName);
        uploadPartRequest.setKey(String.valueOf(cacheEngine.get(storageType, md5 + SLASH + fileName)));
        uploadPartRequest.setUploadId(String.valueOf(cacheEngine.get(storageType,md5)));
        uploadPartRequest.setInputStream(inputStream);
        // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
        uploadPartRequest.setPartSize(chunkSize);
        // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出此范围，OSS将返回InvalidArgument错误码。
        uploadPartRequest.setPartNumber(chunk + ONE_INT);
        // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
        UploadPartResult uploadPartResult = client.uploadPart(uploadPartRequest);
        // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
        ((CopyOnWriteArrayList) cacheEngine.get(storageType, md5 + SLASH + TAG)).add(uploadPartResult.getPartETag());
        // 关闭OSSClient。
        client.shutdown();
        final VirtualFile virtualFile = new VirtualFile()
                .setOriginalFileName(this.newFileName)
                .setSuffix(suffix)
                .setUploadStartTime(startDate)
                .setUploadEndTime(new Date())
                .setFilePath(this.newFileName)
                .setFileHash("")
                .setFullFilePath(this.domainUrl + this.newFileName);
        progressListener.end(virtualFile);
        return virtualFile;
    }
    @Override
    public VirtualFile resumeUpload(InputStream inputStream, String fileName) {
        return null;
    }

    @Override
    public CheckFileResult checkFile(MetaDataRequest metaDataRequest, HttpServletRequest request) {
        return null;
    }

    @Override
    protected void check() {
        if (StringUtils.isNullOrEmpty(accessKey) || StringUtils.isNullOrEmpty(secretKey) || StringUtils.isNullOrEmpty(bucketName)) {
            throw new ServiceException("[" + this.storageType + "]尚未配置阿里云，文件上传功能暂时不可用！");
        }
        this.client = new OSSClient(endpoint, accessKey, secretKey);
        boolean bucketExist = client.doesBucketExist(bucketName);
        if (!bucketExist) {
            client.createBucket(bucketName);
        }
    }

    /**
     * 下载文件
     *
     * @param fileName OSS中保存的文件名
     */
    @Override
    public InputStream downloadFileStream(String fileName) {
        this.check();
        if (StringUtils.isEmpty(fileName)) {
            throw new ServiceException("[" + this.storageType + "]下载文件失败：文件key为空");
        }
        try {
            boolean exists = this.client.doesBucketExist(bucketName);
            if (!exists) {
                throw new ServiceException("[阿里云OSS] 文件删除失败！Bucket不存在：" + bucketName);
            }
            if (!this.client.doesObjectExist(bucketName, fileName)) {
                throw new ServiceException("[阿里云OSS] 文件下载失败！文件不存在：" + bucketName + "/" + fileName);
            }
            OSSObject object = this.client.getObject(bucketName, fileName);
            return object.getObjectContent();
        } finally {
            this.client.shutdown();
        }
    }
}
