package com.zhouzifei.tool.fileClient;


import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.config.AwsFileProperties;
import com.zhouzifei.tool.config.FileProperties;
import com.zhouzifei.tool.consts.StorageTypeConst;
import com.zhouzifei.tool.dto.CheckFileResult;
import com.zhouzifei.tool.dto.VirtualFile;
import com.zhouzifei.tool.entity.FileListRequesr;
import com.zhouzifei.tool.entity.MetaDataRequest;
import com.zhouzifei.tool.media.file.util.StreamUtil;
import com.zhouzifei.tool.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author 周子斐
 * @date 2022/1/14
 * @Description
 */
public class AwsS3ApiClient extends BaseApiClient {
    private String accessKey;
    private String secretKey;
    private String region;
    private String endpoint;
    private String bucketName;
    private String domainUrl;
    private AmazonS3 amazonS3;

    public AwsS3ApiClient() {
        super("AWS-S3");
    }

    public AwsS3ApiClient(FileProperties fileProperties) {
        super("AWS-S3");
        init(fileProperties);
    }

    @Override
    public AwsS3ApiClient init(FileProperties fileProperties) {
        final AwsFileProperties awsFileProperties = fileProperties.getAws();
        this.accessKey = awsFileProperties.getSecretKey();
        this.secretKey = awsFileProperties.getSecretKey();
        this.domainUrl = awsFileProperties.getDomainUrl();
        this.endpoint = awsFileProperties.getEndpoint();
        this.region = awsFileProperties.getRegion();
        this.bucketName = awsFileProperties.getBucketName();
        checkDomainUrl(domainUrl);
        return this;
    }

    @Override
    public String uploadInputStream(InputStream is, String imageUrl) {
        try (InputStream uploadIs = StreamUtil.clone(is)) {
            final PutObjectResult putObjectResult = amazonS3.putObject(bucketName, imageUrl, uploadIs, null);
            System.out.println(putObjectResult);
            return this.newFileName;
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        } finally {
            this.amazonS3.shutdown();
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
            boolean exists = this.amazonS3.doesBucketExist(bucketName);
            if (!exists) {
                throw new ServiceException("[阿里云OSS] 文件删除失败！Bucket不存在：" + bucketName);
            }
            if (!this.amazonS3.doesObjectExist(bucketName, fileName)) {
                throw new ServiceException("[阿里云OSS] 文件删除失败！文件不存在：" + bucketName + "/" + fileName);
            }
            this.amazonS3.deleteObject(bucketName, fileName);
            return true;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        } finally {
            this.amazonS3.shutdown();
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
                InitiateMultipartUploadResult initiateMultipartUploadResult = amazonS3.initiateMultipartUpload(request);
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
        UploadPartResult uploadPartResult = amazonS3.uploadPart(uploadPartRequest);
        // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
        ((CopyOnWriteArrayList) cacheEngine.get(storageType, md5 + SLASH + TAG)).add(uploadPartResult.getPartETag());
        // 关闭OSSClient。
        amazonS3.shutdown();
        final VirtualFile virtualFile = VirtualFile.builder().originalFileName(this.newFileName).suffix(this.suffix).uploadStartTime(startDate).uploadEndTime(new Date()).filePath(this.newFileName).fileHash(null).fullFilePath(this.newFileUrl + this.newFileName).build();
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
    public List<VirtualFile> fileList(FileListRequesr fileListRequesr) {
        this.check();
        // 设置最大个数。
        final int maxKeys = 200;
        String nextContinuationToken = null;
        // 指定前缀，例如exampledir/object。
        final String keyPrefix = fileListRequesr.getPrefix();
        ListObjectsV2Result result = null;
        List<VirtualFile> virtualFiles = new ArrayList<>();
        // 指定返回结果使用URL编码，则您需要对结果中的prefix、delemiter、startAfter、key和commonPrefix进行URL解码。
        do {
            result = amazonS3.listObjectsV2(bucketName);
            // 文件名称解码。
            for (S3ObjectSummary s3ObjectSummary : result.getObjectSummaries()) {
                String decodedKey = null;
                try {
                    decodedKey = URLDecoder.decode(s3ObjectSummary.getKey(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                VirtualFile virtualFile = VirtualFile.builder().originalFileName(decodedKey).suffix(this.suffix).uploadStartTime(s3ObjectSummary.getLastModified()).uploadEndTime(s3ObjectSummary.getLastModified()).filePath(this.newFileName).size(s3ObjectSummary.getSize()).fileHash(s3ObjectSummary.getETag()).fullFilePath(this.newFileUrl + decodedKey).build();
                virtualFiles.add(virtualFile);
            }
            nextContinuationToken = result.getNextContinuationToken();
        } while (result.isTruncated());
        // 关闭OSSClient。
        amazonS3.shutdown();
        return virtualFiles;
    }

    @Override
    public boolean exists(String fileName) {
        return amazonS3.doesObjectExist(bucketName, this.newFileUrl + folder + fileName);
    }

    @Override
    protected void check() {
        if (StringUtils.isNullOrEmpty(accessKey) || StringUtils.isNullOrEmpty(secretKey) || StringUtils.isNullOrEmpty(bucketName)) {
            throw new ServiceException("[" + this.storageType + "]尚未配置阿里云，文件上传功能暂时不可用！");
        }
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)));
        if (StringUtils.isNotBlank(endpoint)) {
            builder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region));
        } else if (StringUtils.isNotBlank(region)) {
            builder.withRegion(region);
        }
        this.amazonS3 = builder.build();
        try {
            final boolean bucketExist = amazonS3.doesBucketExistV2(bucketName);
            if (!bucketExist) {
                amazonS3.createBucket(bucketName);
            }
        } catch (SdkClientException e) {
            e.printStackTrace();
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
            boolean exists = this.amazonS3.doesBucketExist(bucketName);
            if (!exists) {
                throw new ServiceException("[阿里云OSS] 文件删除失败！Bucket不存在：" + bucketName);
            }
            if (!this.amazonS3.doesObjectExist(bucketName, fileName)) {
                throw new ServiceException("[阿里云OSS] 文件下载失败！文件不存在：" + bucketName + "/" + fileName);
            }
            final S3Object object = this.amazonS3.getObject(bucketName, fileName);
            return object.getObjectContent();
        } finally {
            this.amazonS3.shutdown();
        }
    }

    public static void main(String[] args) {
        final FileProperties fileProperties = new FileProperties();
        final AwsFileProperties awsFileProperties = fileProperties.getAws();
        awsFileProperties.setAccessKey("LTAI5tFpTDE26XYiPmH9dxDz");
        awsFileProperties.setSecretKey("9gC7gs5kEJJmZec6a6QupoefIL82Kr");
        awsFileProperties.setEndpoint("oss-cn-beijing.aliyuncs.com");
        awsFileProperties.setBucketName("simple-fs");
        awsFileProperties.setDomainUrl("https://simple-fs.oss-cn-beijing.aliyuncs.com/");
        final AwsS3ApiClient awsS3ApiClient = new AwsS3ApiClient(fileProperties);
        final File file = new File("/Users/Dabao/Downloads/videoplayback.mp4");
        final VirtualFile virtualFile = awsS3ApiClient.uploadFile(file);
        System.out.println(virtualFile);
        System.out.println(1);
    }

}
