package com.zhouzifei.tool.fileClient;


import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.common.upaiyun.UpaiManager;
import com.zhouzifei.tool.config.FileProperties;
import com.zhouzifei.tool.config.UpaiFileProperties;
import com.zhouzifei.tool.dto.VirtualFile;
import com.zhouzifei.tool.entity.FileListRequesr;
import com.zhouzifei.tool.entity.MetaDataRequest;
import com.zhouzifei.tool.service.ApiClient;
import com.zhouzifei.tool.util.StringUtils;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 周子斐
 * @date 2021/1/30
 * @Description
 */
public class UpaiyunOssApiClient extends BaseApiClient {

    private UpaiManager upaiManager;

    public UpaiyunOssApiClient() {
        super("又拍云");
    }
    public UpaiyunOssApiClient(FileProperties fileProperties) {
        super("又拍云");
        init(fileProperties);
    }

    @Override
    public UpaiyunOssApiClient init(FileProperties fileProperties) {
        final UpaiFileProperties upaiFileProperties = fileProperties.getUpai();
        String userName = upaiFileProperties.getUserName();
        String passWord = upaiFileProperties.getPassWord();
        String url = upaiFileProperties.getUrl();
        String bucketName = upaiFileProperties.getBucketName();
        checkDomainUrl(url);
        if (StringUtils.isNullOrEmpty(userName)
                || StringUtils.isNullOrEmpty(passWord)
                || StringUtils.isNullOrEmpty(bucketName)) {
            throw new ServiceException("[" + this.storageType + "]尚未配置又拍云，文件上传功能暂时不可用！");
        }
        upaiManager = new UpaiManager(bucketName, userName, passWord);
        return this;
    }
    @Override
    public String uploadInputStream(InputStream is, String imageUrl) {
        // 切换 API 接口的域名接入点，默认为自动识别接入点
        upaiManager.setApiDomain(UpaiManager.ED_AUTO);
        // 设置连接超时时间，默认为30秒
        upaiManager.setTimeout(60);
        try {
            Map<String, String> param = new HashMap<>();
            final Response response = upaiManager.writeFile(this.newFileName, is, param);
            if(!response.isSuccessful()){
                throw new ServiceException("[" + this.storageType + "]文件上传失败.");
            }
            return this.newFileName;
        } catch (IOException ex) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + ex.getMessage());
        }
    }

    @Override
    public boolean removeFile(String key) {
        if (StringUtils.isNullOrEmpty(key)) {
            throw new ServiceException("[" + this.storageType + "]删除文件失败：文件key为空");
        }
        try {
            upaiManager.deleteFile(key, null);
            return true;
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件删除失败：" + e.getMessage());
        }
    }
    @Override
    public VirtualFile multipartUpload(InputStream inputStream, MetaDataRequest metaDataRequest) {
        return null;
    }

    @Override
    public List<VirtualFile> fileList(FileListRequesr fileListRequesr){
        return null;
    }

    @Override
    public boolean exists(String fileName) {
        try (Response response = upaiManager.getFileInfo(this.domainUrl + fileName)) {
            return StringUtils.isNotBlank(response.header("x-upyun-file-size"));
        } catch (IOException e) {
            throw new ServiceException("判断文件是否存在失败！fileInfo：" + fileName);
        }
    }

    @Override
    protected void check() {

    }

    @Override
    public InputStream downloadFileStream(String key) {
        if (StringUtils.isNullOrEmpty(key)) {
            throw new ServiceException("[" + this.storageType + "]下载文件失败：文件key为空");
        }
        try {
            return Objects.requireNonNull(upaiManager.readFile(this.domainUrl + key).body()).byteStream();
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件下载失败：" + e.getMessage());
        }
    }
    @Override
    public ApiClient getAwsApiClient(){
        throw new ServiceException("[" + this.storageType + "]暂不支持AWS3协议！");
    }
}
