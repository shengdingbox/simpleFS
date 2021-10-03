package com.zhouzifei.tool.media.file.fileClient;


import com.alibaba.fastjson.JSON;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Region;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.html.util.Randoms;
import com.zhouzifei.tool.util.StringUtils;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.media.file.util.FileUtil;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Qiniu云操作文件的api：v1
 *
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
 * @since 1.0
 */
public class QiniuApiClient extends BaseApiClient {

    private BucketManager bucketManager;
    private Auth auth;
    private String bucket;
    private String baseUrl;

    public QiniuApiClient() {
        super("七牛云");
    }

    public QiniuApiClient init(String accessKey, String secretKey, String bucketName, String baseUrl, String uploadType) {
        if (StringUtils.isNullOrEmpty(accessKey) || StringUtils.isNullOrEmpty(secretKey) || StringUtils.isNullOrEmpty(bucketName)) {
            throw new ServiceException("[" + this.storageType + "]尚未配置七牛云，文件上传功能暂时不可用！");
        }
        auth = Auth.create(accessKey, secretKey);
        this.bucket = bucketName;
        this.baseUrl = baseUrl;
        super.folder = StringUtils.isEmpty(uploadType) ? "" : uploadType + "/";
        return this;
    }

    /**
     * 上传图片
     *
     * @param is       图片流
     * @param fileName 图片路径
     * @return 上传后的路径
     */
    @Override
    public VirtualFile uploadFile(InputStream is, String fileName) {
        Date startTime = new Date();
        //Zone.zone0:华东
        //Zone.zone1:华北
        //Zone.zone2:华南
        //Zone.zoneNa0:北美
        Configuration cfg = new Configuration(Region.autoRegion());
        UploadManager uploadManager = new UploadManager(cfg);
        this.suffix = FileUtil.getSuffix(fileName);
        fileName = Randoms.alpha(16) + this.suffix;
        this.createNewFileName(fileName);
        try {
            String upToken = auth.uploadToken(this.bucket);
            Response response = uploadManager.put(is, this.newFileName, upToken, null, null);

            //解析上传成功的结果
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            return new VirtualFile()
                    .setOriginalFileName(fileName)
                    .setSuffix(this.suffix)
                    .setUploadStartTime(startTime)
                    .setUploadEndTime(new Date())
                    .setFilePath(putRet.key)
                    .setFileHash(putRet.hash)
                    .setFullFilePath(this.baseUrl + putRet.key);
        } catch (QiniuException ex) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + ex.error());
        }
    }

    /**
     * 删除七牛空间图片方法
     *
     * @param key 七牛空间中文件名称
     */
    @Override
    public boolean removeFile(String key) {
        if (StringUtils.isNullOrEmpty(key)) {
            throw new ServiceException("[" + this.storageType + "]删除文件失败：文件key为空");
        }
        try {
            Response re = bucketManager.delete(this.bucket, key);
            return re.isOK();
        } catch (QiniuException e) {
            Response r = e.response;
            throw new ServiceException("[" + this.storageType + "]删除文件发生异常：" + r.toString());
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

    public String getPath() {
        return this.baseUrl;
    }

    @Override
    protected void check() {

    }

    @Override
    public InputStream downloadFileStream(String key) {
        try {
            String encodedFileName = URLEncoder.encode(key, "utf-8").replace("+", "%20");
            String publicUrl = String.format("%s/%s", getPath(), encodedFileName);
            long expireInSeconds = 3600;
            String finalUrl = auth.privateDownloadUrl(publicUrl, expireInSeconds);
            return FileUtil.getInputStreamByUrl(finalUrl, "");
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException("[" + this.storageType + "]下载文件发生异常：" + e.toString());
        }
    }
}
