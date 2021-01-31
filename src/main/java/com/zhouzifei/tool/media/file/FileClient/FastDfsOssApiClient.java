package com.zhouzifei.tool.media.file.FileClient;


import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.exception.OssApiException;
import com.zhouzifei.tool.exception.QiniuApiException;
import com.zhouzifei.tool.exception.ServiceException;
import com.zhouzifei.tool.media.file.FileUtil;
import com.zhouzifei.tool.media.file.service.UpaiManager;
import com.zhouzifei.tool.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 周子斐
 * @date 2021/1/30
 * @Description
 */
public class FastDfsOssApiClient extends BaseApiClient {

    private static final String DEFAULT_PREFIX = "upaiyun/";
    private String operatorName;
    private String operatorPwd;
    private String bucket;
    private String path;
    private String pathPrefix;

    public FastDfsOssApiClient() {
        super("又拍云");
    }

    public FastDfsOssApiClient init(String operatorName, String operatorPwd, String bucketName, String baseUrl, String uploadType) {
        this.operatorName = operatorName;
        this.operatorPwd = operatorPwd;
        this.bucket = bucketName;
        this.path = baseUrl;
        this.pathPrefix = StringUtils.isNullOrEmpty(uploadType) ? DEFAULT_PREFIX : uploadType.endsWith("/") ? uploadType : uploadType + "/";
        return this;
    }

    @Override
    protected void check() {
        if (StringUtils.isNullOrEmpty(this.operatorName) || StringUtils.isNullOrEmpty(this.operatorPwd) || StringUtils.isNullOrEmpty(this.bucket)) {
            throw new QiniuApiException("[" + this.storageType + "]尚未配置七牛云，文件上传功能暂时不可用！");
        }
    }

    @Override
    public VirtualFile uploadImg(InputStream is, String imageUrl) {
        this.check();
        UpaiManager upaiManager = new UpaiManager(bucket, operatorName, operatorPwd);
        // 切换 API 接口的域名接入点，默认为自动识别接入点
        upaiManager.setApiDomain(UpaiManager.ED_AUTO);
        // 设置连接超时时间，默认为30秒
        upaiManager.setTimeout(60);
        Date startTime = new Date();
        String key = FileUtil.generateTempFileName(imageUrl);
        this.createNewFileName(key, this.pathPrefix);
        try {
            Map<String, String> param = new HashMap<>();
            upaiManager.writeFile(this.newFileName,is,param);
            return new VirtualFile()
                    .setOriginalFileName(key)
                    .setSuffix(this.suffix)
                    .setUploadStartTime(startTime)
                    .setUploadEndTime(new Date())
                    .setFilePath(this.newFileName)
                    .setFullFilePath(this.path + this.newFileName);
        } catch (IOException ex) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + ex.getMessage());
        }
    }

    @Override
    public boolean removeFile(String key) {
        this.check();
        if (StringUtils.isNullOrEmpty(key)) {
            throw new OssApiException("[" + this.storageType + "]删除文件失败：文件key为空");
        }
        UpaiManager upaiManager = new UpaiManager(bucket, operatorName, operatorPwd);
        // 删除文件
        try {
            upaiManager.deleteFile(key, null);
            return true;
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件删除失败：" + e.getMessage());
        }
    }
}
