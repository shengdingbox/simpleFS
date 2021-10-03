package com.zhouzifei.tool.media.file.fileClient;


import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.util.StringUtils;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.media.file.util.FileUtil;
import com.zhouzifei.tool.media.file.common.upaiyun.UpaiManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author 周子斐
 * @date 2021/1/30
 * @Description
 */
public class UpaiyunOssApiClient extends BaseApiClient {


    private UpaiManager upaiManager;
    private String baseUrl;

    public UpaiyunOssApiClient() {
        super("又拍云");
    }

    public UpaiyunOssApiClient init(String operatorName, String operatorPwd, String bucketName, String baseUrl, String uploadType) {
        if (StringUtils.isNullOrEmpty(operatorName) || StringUtils.isNullOrEmpty(operatorPwd) || StringUtils.isNullOrEmpty(bucketName)) {
            throw new ServiceException("[" + this.storageType + "]尚未配置七牛云，文件上传功能暂时不可用！");
        }
        upaiManager = new UpaiManager(bucketName, operatorName, operatorPwd);
        this.baseUrl = baseUrl;
        super.folder = StringUtils.isEmpty(uploadType) ? "" : uploadType + "/";
        return this;
    }
    @Override
    public VirtualFile uploadFile(InputStream is, String imageUrl) {
        // 切换 API 接口的域名接入点，默认为自动识别接入点
        upaiManager.setApiDomain(UpaiManager.ED_AUTO);
        // 设置连接超时时间，默认为30秒
        upaiManager.setTimeout(60);
        Date startTime = new Date();
        String key = FileUtil.generateTempFileName(imageUrl);
        this.createNewFileName(key);
        try {
            Map<String, String> param = new HashMap<>();
            upaiManager.writeFile(this.newFileName,is,param);
            return new VirtualFile()
                    .setOriginalFileName(key)
                    .setSuffix(this.suffix)
                    .setUploadStartTime(startTime)
                    .setUploadEndTime(new Date())
                    .setFilePath(this.newFileName)
                    .setFullFilePath(this.baseUrl + this.newFileName);
        } catch (IOException ex) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + ex.getMessage());
        }
    }

    @Override
    public boolean removeFile(String key) {
        if (StringUtils.isNullOrEmpty(key)) {
            throw new ServiceException("[" + this.storageType + "]删除文件失败：文件key为空");
        }
        // 删除文件
        try {
            upaiManager.deleteFile(key, null);
            return true;
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件删除失败：" + e.getMessage());
        }
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
        if (StringUtils.isNullOrEmpty(key)) {
            throw new ServiceException("[" + this.storageType + "]删除文件失败：文件key为空");
        }
        try {
            return Objects.requireNonNull(upaiManager.readFile(baseUrl + key).body()).byteStream();
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件下载失败：" + e.getMessage());
        }
    }
}
