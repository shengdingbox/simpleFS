package com.zhouzifei.tool.fileClient;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.ServiceException;
import com.aliyun.oss.model.*;
import com.zhouzifei.tool.config.FileProperties;
import com.zhouzifei.tool.consts.StorageTypeConst;
import com.zhouzifei.tool.dto.CheckFileResult;
import com.zhouzifei.tool.dto.VirtualFile;
import com.zhouzifei.tool.entity.FileListRequesr;
import com.zhouzifei.tool.entity.MetaDataRequest;
import com.zhouzifei.tool.media.file.util.StreamUtil;
import com.zhouzifei.tool.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    private OSS client;
    private String bucketName;
    private String endpoint;
    private String accessKey;
    private String secretKey;

    public AliyunOssApiClient() {
        super("阿里云OSS");
    }
    public AliyunOssApiClient(FileProperties fileProperties) {
        super("阿里云OSS");
        init(fileProperties);
    }

    @Override
    public AliyunOssApiClient init(FileProperties fileProperties) {
        String aliEndpoint = fileProperties.getOss().getEndpoint();
        String aliAccessKey = fileProperties.getOss().getAccessKey();
        String aliSecretKey = fileProperties.getOss().getSecretKey();
        this.bucketName = fileProperties.getOss().getBucketName();
        this.endpoint = aliEndpoint;
        this.accessKey = aliAccessKey;
        this.secretKey = aliSecretKey;
        checkDomainUrl(fileProperties.getOss().getDomainUrl());
        return this;
    }

    @Override
    public String uploadInputStream(InputStream is, String imageUrl) {
        this.check();
        try (InputStream uploadIs = StreamUtil.clone(is)) {
            this.client.putObject(bucketName, this.newFileName, uploadIs);
            return  newFileName;
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        } finally {
            this.client.shutdown();
        }
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
        final String name = metaDataRequest.getName();
        final Integer chunkSize = metaDataRequest.getChunkSize();
        final Integer chunk = metaDataRequest.getChunk();
        synchronized (LOCK) {
            final String storageType = StorageTypeConst.ALIYUN.getStorageType();
            final Object o = super.cacheEngine.get(storageType, md5);
            if (Objects.isNull(o)) {
                InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, name);
                InitiateMultipartUploadResult initiateMultipartUploadResult = client.initiateMultipartUpload(request);
                cacheEngine.add(storageType, md5, initiateMultipartUploadResult.getUploadId());
                cacheEngine.add(storageType, md5 + SLASH + name, name);
                cacheEngine.add(storageType, md5 + SLASH + TAG, new CopyOnWriteArrayList<>());
            }
        }
        UploadPartRequest uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setBucketName(bucketName);
        uploadPartRequest.setKey(String.valueOf(cacheEngine.get(storageType, md5 + SLASH + name)));
        uploadPartRequest.setUploadId(String.valueOf(cacheEngine.get(storageType, md5)));
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
        VirtualFile virtualFile =  VirtualFile.builder()
                .originalFileName(this.newFileName)
                .suffix(this.suffix)
                .uploadStartTime(startDate)
                .uploadEndTime(new Date())
                .filePath(this.newFileName)
                .fileHash(null)
                .fullFilePath(this.newFileUrl + this.newFileName).build();
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
    public List<VirtualFile> fileList(FileListRequesr fileListRequesr){
        this.check();
        // 设置最大个数。
        final int maxKeys = 200;
        String nextContinuationToken = null;
        ListObjectsV2Result result = null;
        // 指定前缀，例如exampledir/object。
        final String keyPrefix = fileListRequesr.getPrefix();
        List<VirtualFile> virtualFiles = new ArrayList<>();
        // 指定返回结果使用URL编码，则您需要对结果中的prefix、delemiter、startAfter、key和commonPrefix进行URL解码。
        do {
            ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request(bucketName).withMaxKeys(maxKeys);
            listObjectsV2Request.setPrefix(keyPrefix);
            listObjectsV2Request.setEncodingType("url");
            listObjectsV2Request.setContinuationToken(nextContinuationToken);
            result = client.listObjectsV2(listObjectsV2Request);
            // 文件名称解码。
            for (OSSObjectSummary s : result.getObjectSummaries()) {
                String decodedKey = null;
                try {
                    decodedKey = URLDecoder.decode(s.getKey(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                assert decodedKey != null;
                if(decodedKey.contains("/")){
                    final String[] split = decodedKey.split("");
                    this.newFileName = split[split.length-1];
                }else{
                    this.newFileName = decodedKey;
                }
                VirtualFile virtualFile =  VirtualFile.builder()
                        .originalFileName(this.newFileName)
                        .suffix(this.suffix)
                        .uploadStartTime(s.getLastModified())
                        .uploadEndTime(s.getLastModified())
                        .filePath(decodedKey)
                        .size(s.getSize())
                        .fileHash(s.getETag())
                        .fullFilePath(this.newFileUrl+decodedKey)
                        .build();
                virtualFiles.add(virtualFile);
            }
            nextContinuationToken = result.getNextContinuationToken();
        } while (result.isTruncated());
        // 关闭OSSClient。
        client.shutdown();
        return virtualFiles;
    }

    @Override
    protected void check() {
        final OSSClientBuilder ossClientBuilder = new OSSClientBuilder();
        if (StringUtils.isNullOrEmpty(accessKey) || StringUtils.isNullOrEmpty(secretKey) || StringUtils.isNullOrEmpty(bucketName)) {
            throw new ServiceException("[" + this.storageType + "]尚未配置阿里云，文件上传功能暂时不可用！");
        }
        this.client =ossClientBuilder.build(endpoint, accessKey, secretKey);
        boolean bucketExist = client.doesBucketExist(bucketName);
        if (!bucketExist) {
            client.createBucket(bucketName);
        }
    }

    @Override
    public boolean exists(String fileName) {
        return client.doesObjectExist(bucketName,fileName);
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
