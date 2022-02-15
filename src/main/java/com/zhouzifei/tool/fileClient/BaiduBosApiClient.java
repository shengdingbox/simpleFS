package com.zhouzifei.tool.fileClient;

import com.baidubce.Protocol;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.BosObject;
import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.dto.VirtualFile;
import com.zhouzifei.tool.entity.FileListRequesr;
import com.zhouzifei.tool.entity.MetaDataRequest;
import com.zhouzifei.tool.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author 周子斐
 * @date 2022/1/18
 * @Description
 */
public class BaiduBosApiClient extends BaseApiClient {

    private BosClient bos;
    private String accessKey;
    private String secretKey;
    private String endPoint;
    private String bucketName;

    public BaiduBosApiClient init(String endPoint, String accessKey, String secretKey, String domainUrl, String bucketName) {
        this.newFileUrl = checkDomainUrl(domainUrl);
        this.bucketName = bucketName;
        this.endPoint = endPoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        return this;
    }

    public BaiduBosApiClient(String storageType) {
        super(storageType);
    }

    @Override
    protected void check() {
        if (StringUtils.isNullOrEmpty(accessKey) || StringUtils.isNullOrEmpty(secretKey) || StringUtils.isNullOrEmpty(bucketName)) {
            throw new ServiceException("[" + this.storageType + "]尚未配置，文件上传功能暂时不可用！");
        }
        BosClientConfiguration config = new BosClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(accessKey, secretKey));
        config.setEndpoint(endPoint);
        config.setProtocol(Protocol.HTTPS);
        this.bos = new BosClient(config);
        boolean bucketExist = bos.doesBucketExist(bucketName);
        if (!bucketExist) {
            bos.createBucket(bucketName);
        }
    }

    @Override
    protected String uploadInputStream(InputStream is, String fileName) {
        bos.putObject(bucketName, fileName, is);
        return fileName;
    }

    @Override
    public boolean removeFile(String fileName) {
        bos.deleteObject(bucketName, fileName);
        return true;
    }

    @Override
    public InputStream downloadFileStream(String fileName) {
        BosObject object = bos.getObject(bucketName, fileName);
        try (InputStream in = object.getObjectContent()) {
            return in;
        } catch (IOException e) {
            throw new ServiceException("文件下载失败！platform：" + e);
        }
    }

    @Override
    public VirtualFile multipartUpload(InputStream inputStream, MetaDataRequest metaDataRequest) {
        return null;
    }

    @Override
    public List<VirtualFile> fileList(FileListRequesr fileListRequesr) {
        return null;
    }

    @Override
    public boolean exists(String fileName) {
        this.check();
        return bos.doesObjectExist(bucketName, fileName);
    }
}
