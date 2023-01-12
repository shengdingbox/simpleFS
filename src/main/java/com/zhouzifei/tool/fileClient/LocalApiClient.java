package com.zhouzifei.tool.fileClient;

import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.config.FileProperties;
import com.zhouzifei.tool.config.LocalFileProperties;
import com.zhouzifei.tool.dto.VirtualFile;
import com.zhouzifei.tool.entity.FileListRequesr;
import com.zhouzifei.tool.entity.MetaDataRequest;
import com.zhouzifei.tool.media.file.util.StreamUtil;
import com.zhouzifei.tool.service.ApiClient;
import com.zhouzifei.tool.util.FileUtil;
import com.zhouzifei.tool.util.MediaFormat;
import com.zhouzifei.tool.util.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
 * @since 1.0
 */
public class LocalApiClient extends BaseApiClient {

    private String localUrl;
    private String localFilePath;

    public LocalApiClient() {
        super("Nginx文件服务器");
    }
    public LocalApiClient(FileProperties fileProperties) {
        super("Nginx文件服务器");
        init(fileProperties);
    }

    @Override
    public ApiClient init(FileProperties fileProperties) {
        final LocalFileProperties localFileProperties = (LocalFileProperties)fileProperties;
        String localUrl = localFileProperties.getLocalUrl();
        String localFilePath = localFileProperties.getLocalFilePath();
        if (StringUtils.isEmpty(localUrl) || StringUtils.isEmpty(localFilePath)) {
            throw new ServiceException("[" + this.storageType + "]尚未配置Nginx文件服务器，文件上传功能暂时不可用！");
        }
        checkDomainUrl(localUrl);
        this.localFilePath = localFilePath;
        return this;
    }

    @Override
    public String uploadInputStream(InputStream is, String userName) {
        String realFilePath = this.localFilePath + this.newFileName;
        try (FileOutputStream fos = new FileOutputStream(realFilePath)) {
            FileCopyUtils.copy(is, fos);
            return this.newFileName;
        } catch (Exception e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage() + userName);
        }
    }

        @Override
    public boolean removeFile(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new ServiceException("[" + this.storageType + "]删除文件失败：文件key为空");
        }
        File file = new File(this.localFilePath + key);
        if (!file.exists()) {
            throw new ServiceException("[" + this.storageType + "]删除文件失败：文件不存在[" + this.localFilePath + key + "]");
        }
        try {
            return file.delete();
        } catch (Exception e) {
            throw new ServiceException("[" + this.storageType + "]删除文件失败：" + e.getMessage());
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
        return false;
    }

    @Override
    protected void check() {
        String realFilePath = this.localFilePath + this.newFileName;
        FileUtil.newFiles(realFilePath);
    }

    @Override
    public InputStream downloadFileStream(String userName) {
        return FileUtil.getInputStreamByUrl(localUrl + userName, "");
    }
    @Override
    public ApiClient getAwsApiClient(){
        throw new ServiceException("[" + this.storageType + "]暂不支持AWS3协议！");
    }
}
