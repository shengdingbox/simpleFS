package com.zhouzifei.tool.media.file.fileClient;

import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.media.file.util.StreamUtil;
import com.zhouzifei.tool.media.file.util.FileUtil;
import com.zhouzifei.tool.util.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * 
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
 * @since 1.0
 */
public class LocalApiClient extends BaseApiClient {
    private static final String DEFAULT_PREFIX = "local/";
    private String url;
    private String rootPath;
    public LocalApiClient() {
        super("Nginx文件服务器");
    }

    public LocalApiClient init(String url, String rootPath, String uploadType) {
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(rootPath)) {
            throw new ServiceException("[" + this.storageType + "]尚未配置Nginx文件服务器，文件上传功能暂时不可用！");
        }
        this.url = url;
        this.rootPath = rootPath;
        super.folder = StringUtils.isEmpty(uploadType) ? "" : uploadType + "/";
        return this;
    }

    @Override
    public VirtualFile uploadFile(InputStream is, String imageUrl) {
        String key = FileUtil.generateTempFileName(imageUrl);
        this.createNewFileName(key);
        Date startTime = new Date();

        String realFilePath = this.rootPath + this.newFileName;
        FileUtil.checkFilePath(realFilePath);
        try (InputStream uploadIs = StreamUtil.clone(is);
             InputStream fileHashIs = StreamUtil.clone(is);
             FileOutputStream fos = new FileOutputStream(realFilePath)) {
            FileCopyUtils.copy(uploadIs, fos);
            return new VirtualFile()
                    .setOriginalFileName(FileUtil.getName(key))
                    .setSuffix(this.suffix)
                    .setUploadStartTime(startTime)
                    .setUploadEndTime(new Date())
                    .setFilePath(this.newFileName)
                    .setFileHash(DigestUtils.md5DigestAsHex(fileHashIs))
                    .setFullFilePath(this.url + this.newFileName);
        } catch (Exception e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage() + imageUrl);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean removeFile(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new ServiceException("[" + this.storageType + "]删除文件失败：文件key为空");
        }
        File file = new File(this.rootPath + key);
        if (!file.exists()) {
            throw new ServiceException("[" + this.storageType + "]删除文件失败：文件不存在[" + this.rootPath + key + "]");
        }
        try {
            return file.delete();
        } catch (Exception e) {
            throw new ServiceException("[" + this.storageType + "]删除文件失败：" + e.getMessage());
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
        return FileUtil.getInputStreamByUrl(url + key, "");
    }
}
