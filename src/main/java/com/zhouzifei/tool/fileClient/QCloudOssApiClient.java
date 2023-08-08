package com.zhouzifei.tool.fileClient;


import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import com.qcloud.cos.transfer.Upload;
import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.config.FileProperties;
import com.zhouzifei.tool.config.QcloudFileProperties;
import com.zhouzifei.tool.dto.VirtualFile;
import com.zhouzifei.tool.entity.FileListRequesr;
import com.zhouzifei.tool.entity.MetaDataRequest;
import com.zhouzifei.tool.util.StringUtils;
import org.junit.Test;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 周子斐
 * @date 2021/1/30
 * @Description
 */
public class QCloudOssApiClient extends BaseApiClient {

    private COSClient cosClient;
    private TransferManager transferManager;

    public QCloudOssApiClient() {
        super("腾讯云");
    }
    public QCloudOssApiClient(FileProperties fileProperties) {
        super("腾讯云");
        init(fileProperties);
    }

    @Override
    public QCloudOssApiClient init(FileProperties fileProperties) {
        final QcloudFileProperties qcloudFileProperties = (QcloudFileProperties) fileProperties;
        this.accessKey = qcloudFileProperties.getAccessKey();
        this.secretKey = qcloudFileProperties.getSecretKey();
        this.endpoint = qcloudFileProperties.getEndpoint();
        this.domainUrl = qcloudFileProperties.getDomainUrl();
        this.bucketName = qcloudFileProperties.getBucketName();
        checkDomainUrl(this.domainUrl);
        if (StringUtils.isNullOrEmpty(accessKey) || StringUtils.isNullOrEmpty(secretKey) || StringUtils.isNullOrEmpty(bucketName)) {
            throw new ServiceException("[" + this.storageType + "]尚未配置腾讯云，文件上传功能暂时不可用！");
        }
        COSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);
        Region region = new Region(endpoint);
        ClientConfig clientConfig = new ClientConfig(region);
        cosClient = new COSClient(cred, clientConfig);
        return this;
    }

    @Override
    public String uploadInputStream(InputStream is, String fileName) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, this.newFileName, is, objectMetadata);
        Upload upload = transferManager.upload(putObjectRequest);
        try {
            UploadResult uploadResult = upload.waitForUploadResult();
            return uploadResult.getKey();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    @Override
    public boolean removeFile(String key) {
        if (StringUtils.isNullOrEmpty(key)) {
            throw new ServiceException("[" + this.storageType + "]删除文件失败：文件key为空");
        }
        // 删除文件
        cosClient.deleteObject(bucketName, key);
        return true;
    }

    @Override
    public VirtualFile multipartUpload(InputStream inputStream, MetaDataRequest metaDataRequest) {
        return null;
    }

    @Override
    public List<VirtualFile> fileList(FileListRequesr fileListRequesr) {
        List<VirtualFile> virtualFiles = new ArrayList<>();
        // 指定返回结果使用URL编码，则您需要对结果中的prefix、delemiter、startAfter、key和commonPrefix进行URL解码。
        ObjectListing objectListing = null;
        do {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
            // 设置 bucket 名称
            listObjectsRequest.setBucketName(bucketName);
            // 设置列出的对象名以 prefix 为前缀
            listObjectsRequest.setPrefix(fileListRequesr.getPrefix());
            // 设置最大列出多少个对象, 一次 listobject 最大支持1000
            listObjectsRequest.setMaxKeys(fileListRequesr.getSize());
            try {
                objectListing = cosClient.listObjects(listObjectsRequest);
            } catch (CosClientException e) {
                e.printStackTrace();
            }
            // 文件名称解码。
            for (COSObjectSummary s : objectListing.getObjectSummaries()) {
                String decodedKey = null;
                try {
                    decodedKey = URLDecoder.decode(s.getKey(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                VirtualFile virtualFile = VirtualFile.builder().originalFileName(decodedKey).suffix(this.suffix).uploadStartTime(s.getLastModified()).uploadEndTime(s.getLastModified()).filePath(this.newFileName).size(s.getSize()).fileHash(s.getETag()).fullFilePath(decodedKey).build();
                virtualFiles.add(virtualFile);
            }
            String nextContinuationToken = objectListing.getNextMarker();
        } while (objectListing.isTruncated());
        // 确认本进程不再使用 cosClient 实例之后，关闭之
        cosClient.shutdown();
        return virtualFiles;
    }

    @Override
    public boolean exists(String fileName) {
        return cosClient.doesObjectExist(bucketName,fileName);
    }

    @Override
    protected void check() {
        // 自定义线程池大小，建议在客户端与 COS 网络充足（例如使用腾讯云的 CVM，同地域上传 COS）的情况下，设置成16或32即可，可较充分的利用网络资源
        // 对于使用公网传输且网络带宽质量不高的情况，建议减小该值，避免因网速过慢，造成请求超时。
        ExecutorService threadPool = Executors.newFixedThreadPool(32);
        // 传入一个 threadpool, 若不传入线程池，默认 TransferManager 中会生成一个单线程的线程池。
        TransferManager transferManager = new TransferManager(cosClient, threadPool);
        // 设置高级接口的配置项
        // 分块上传阈值和分块大小分别为 5MB 和 1MB
        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(5 * 1024 * 1024);
        transferManagerConfiguration.setMinimumUploadPartSize(1 * 1024 * 1024);
        transferManager.setConfiguration(transferManagerConfiguration);
        this.transferManager = transferManager;
    }

    @Override
    public InputStream downloadFileStream(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        COSObject object = cosClient.getObject(getObjectRequest);
        return object.getObjectContent();
    }
}
