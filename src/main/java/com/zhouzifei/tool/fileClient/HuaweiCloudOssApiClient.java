package com.zhouzifei.tool.fileClient;


import com.obs.services.ObsClient;
import com.obs.services.model.DeleteObjectResult;
import com.obs.services.model.ObsObject;
import com.obs.services.model.PutObjectResult;
import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.config.FileProperties;
import com.zhouzifei.tool.config.HuaweiFileProperties;
import com.zhouzifei.tool.dto.CheckFileResult;
import com.zhouzifei.tool.dto.VirtualFile;
import com.zhouzifei.tool.entity.FileListRequesr;
import com.zhouzifei.tool.entity.MetaDataRequest;
import com.zhouzifei.tool.util.FileUtil;
import com.zhouzifei.tool.util.RandomsUtil;
import com.zhouzifei.tool.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 * @author 周子斐
 * @date 2021/1/30
 * @Description
 */
public class HuaweiCloudOssApiClient extends BaseApiClient {

    private ObsClient obsClient;

    public HuaweiCloudOssApiClient() {
        super("华为云");
    }

    public HuaweiCloudOssApiClient(FileProperties fileProperties) {
        super("华为云");
        init(fileProperties);
    }

    @Override
    public HuaweiCloudOssApiClient init(FileProperties fileProperties) {
        HuaweiFileProperties huaweiFileProperties = (HuaweiFileProperties) fileProperties;
        this.accessKey = huaweiFileProperties.getAccessKey();
        this.secretKey = huaweiFileProperties.getSecretKey();
        this.endpoint = huaweiFileProperties.getEndpoint();
        this.domainUrl = huaweiFileProperties.getDomainUrl();
        this.bucketName = huaweiFileProperties.getBucketName();
        if (StringUtils.isNullOrEmpty(accessKey) || StringUtils.isNullOrEmpty(secretKey) || StringUtils.isNullOrEmpty(endpoint)) {
            throw new ServiceException("[" + this.storageType + "]尚未配置华为云，文件上传功能暂时不可用！");
        }
        // 创建ObsClient实例
        obsClient = new ObsClient(accessKey, secretKey, endpoint);
        checkDomainUrl(domainUrl);
        return this;
    }

    @Override
    public String uploadInputStream(InputStream is, String fileName) {
        PutObjectResult putObjectResult = obsClient.putObject(bucketName, this.newFileName, is);
        return putObjectResult.getObjectKey();
    }

    @Override
    public boolean removeFile(String fileName) {
        if (StringUtils.isNullOrEmpty(fileName)) {
            throw new ServiceException("[" + this.storageType + "]删除文件失败：文件key为空");
        }
        if (!exists(fileName)) {
            throw new ServiceException("[阿里云OSS] 文件删除失败！文件不存在：" + bucketName + "/" + fileName);
        }
        // 删除文件
        DeleteObjectResult deleteObjectResult = obsClient.deleteObject(bucketName, fileName);
        return deleteObjectResult.isDeleteMarker();
    }

    @Override
    public VirtualFile multipartUpload(InputStream inputStream, MetaDataRequest metaDataRequest) {
        return null;
    }

    @Override
    public CheckFileResult checkFile(MetaDataRequest metaDataRequest, HttpServletRequest request) {
        return null;
    }

    @Override
    public List<VirtualFile> fileList(FileListRequesr fileListRequesr) {
        return null;
    }

    @Override
    public boolean exists(String fileName) {
        return obsClient.doesObjectExist(bucketName, fileName);
    }

    @Override
    protected void check() {

    }

    @Override
    public InputStream downloadFileStream(String key) {
        ObsObject obsObject = obsClient.getObject(bucketName, key);
        System.out.println("Object content:");
        return obsObject.getObjectContent();

    }
}
