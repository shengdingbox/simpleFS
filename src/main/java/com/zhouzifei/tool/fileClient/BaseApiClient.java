package com.zhouzifei.tool.fileClient;

import com.zhouzifei.cache.FileCacheEngine;
import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.config.FileProperties;
import com.zhouzifei.tool.config.SimpleFsProperties;
import com.zhouzifei.tool.dto.CheckFileResult;
import com.zhouzifei.tool.dto.VirtualFile;
import com.zhouzifei.tool.entity.MetaDataRequest;
import com.zhouzifei.tool.listener.ProgressListener;
import com.zhouzifei.tool.media.file.util.StreamUtil;
import com.zhouzifei.tool.service.ApiClient;
import com.zhouzifei.tool.util.FileUtil;
import com.zhouzifei.tool.util.RandomsUtil;
import com.zhouzifei.tool.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Date;

/**
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
 * @since 1.0
 */
@Component
public abstract class BaseApiClient implements ApiClient {

    protected String storageType;
    protected String folder = "";
    public ProgressListener progressListener = newListener();
    protected String suffix;
    protected String newFileName;
    protected String domainUrl;
    protected String accessKey;
    protected String secretKey;
    protected String region;
    protected String endpoint;
    protected String bucketName;
    protected String username;
    protected String password;
    protected String token;
    protected final Object LOCK = new Object();
    protected final FileCacheEngine cacheEngine = new FileCacheEngine();
    protected static final String SLASH = "/";
    protected static final String TAG = "PartETag";
    protected static final Integer ONE_INT = 1;
    SimpleFsProperties simpleFsProperties;

    protected ProgressListener newListener() {
        return new ProgressListener() {
            @Override
            public void start(String msg) {
            }

            @Override
            public void process(int finished, int sum) {
            }

            @Override
            public void end(VirtualFile virtualFile) {
            }
        };
    }

    public BaseApiClient(String storageType) {
        this.storageType = storageType;
    }

    public abstract ApiClient init(FileProperties fileProperties);
    public ApiClient setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    @Override
    public VirtualFile uploadFile(MultipartFile file) {
        if (file == null) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：文件不可为空");
        }
        try {
            this.newFileName = file.getOriginalFilename();
            return this.uploadFile(file.getInputStream(),this.newFileName);
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public VirtualFile uploadFile(File file) {
        if (file == null) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：文件不可为空");
        }
        this.newFileName = file.getName();
        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            return this.uploadFile(is, file.getName());
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public VirtualFile uploadFile(InputStream is, String fileName) {
        Date startTime = new Date();
        this.newFileName = fileName;
        this.checkName();
        getMeta();
        try (InputStream uploadIs = StreamUtil.clone(is);
             InputStream fileHashIs = StreamUtil.clone(is)) {
            final String filePath = this.uploadInputStream(uploadIs, newFileName);
            return VirtualFile.builder().originalFileName(this.newFileName).suffix(this.suffix).uploadStartTime(startTime).uploadEndTime(new Date()).filePath(filePath).fileHash(DigestUtils.md5DigestAsHex(fileHashIs)).fullFilePath(this.domainUrl+filePath).build();
        } catch (IOException ex) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + ex.getMessage());
        }
    }

    private void getMeta() {

    }

    /**
     * 将网络图片转存到云存储中
     *
     * @param userName 网络图片地址
     * @param referer  为了预防某些网站做了权限验证，不加referer可能会403
     */
    @Override
    public VirtualFile saveToCloudStorage(String userName, String referer, String fileName) {
        try (InputStream is = FileUtil.getInputStreamByUrl(userName, referer)) {
            if (StringUtils.isEmpty(fileName)) {
                fileName = userName;
            }
            return this.uploadFile(is, fileName);
        } catch (Exception e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        }
    }

    protected abstract void check();

    protected  void checkName() {
        this.check();
        if(StringUtils.isEmpty(this.newFileName)||exists(this.newFileName)){
            createNewFileName();
        }
    }

    protected abstract String uploadInputStream(InputStream is, String fileName);

    @Override
    public void downloadFileToLocal(String key, String localFile) {
        this.check();
        InputStream content = this.downloadFileStream(key);
        String saveFile = localFile + key;
        FileUtil.mkdirs(saveFile);
        FileUtil.down(content, saveFile);
    }

    void createNewFileName() {
        this.suffix = FileUtil.getSuffix(this.newFileName);
        this.newFileName = folder + RandomsUtil.alpha(16) + suffix;
    }

    void checkDomainUrl(String domainUrl) {
        if(StringUtils.isEmpty(domainUrl)){
            throw new ServiceException("业务域名不能为空");
        }
        this.domainUrl =  domainUrl.endsWith("/") ? domainUrl : domainUrl + "/";
    }

    @Override
    public VirtualFile multipartUpload(File file, MetaDataRequest metaDataRequest) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            return this.multipartUpload(fileInputStream, metaDataRequest);
        } catch (FileNotFoundException e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public VirtualFile multipartUpload(MultipartFile file, MetaDataRequest metaDataRequest) {
        if (file == null) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：文件不可为空");
        }
        try {
            return this.multipartUpload(file.getInputStream(), metaDataRequest);
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public CheckFileResult checkFile(MetaDataRequest metaDataRequest, HttpServletRequest request) {
        return null;
    }

    @Override
    public VirtualFile resumeUpload(InputStream inputStream, String fileName) {
        return null;
    }
    public  ApiClient getAwsApiClient() {
        return new AwsS3ApiClient(this.accessKey
                ,secretKey
                ,region
                ,endpoint
                ,bucketName
                ,this.domainUrl);
    }
}
