package com.zhouzifei.tool.controller;

import com.zhouzifei.tool.cache.CacheEngine;
import com.zhouzifei.tool.cache.FileCacheEngine;
import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.config.properties.FileProperties;
import com.zhouzifei.tool.consts.StorageTypeConst;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.holder.RequestHolder;
import com.zhouzifei.tool.media.file.service.ApiClient;
import com.zhouzifei.tool.media.file.service.FileUploader;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
public class FileUploadController {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("/yyyy/MM/hh/");


    @PostMapping({"/upload"})
    public VirtualFile upload(MultipartFile file, StorageTypeConst storageTypeConst) throws IOException {
        FileUploader uploader = new FileUploader();
        final String storageType = storageTypeConst.getStorageType();
        CacheEngine cacheEngine = new FileCacheEngine();
        final Object fileProperties = cacheEngine.get(storageType, "fileProperties");
        if(null == fileProperties){
            throw new ServiceException("该空间未配置,请POST请求/config添加配置");
        }
        ApiClient apiClient = uploader.getApiClient((FileProperties)fileProperties);
        return apiClient.uploadFile(file.getInputStream(),file.getOriginalFilename());
    }
    @PostMapping({"/ceshi"})
    public VirtualFile ceshi(MultipartFile file, HttpServletRequest request, StorageTypeConst storageTypeConst) {
        FileUploader uploader = new FileUploader();
        final String storageType = storageTypeConst.getStorageType();
        CacheEngine cacheEngine = new FileCacheEngine();
        final Object fileProperties = cacheEngine.get(storageType, "fileProperties");
        if(null == fileProperties){
            throw new ServiceException("该空间未配置,请POST请求/config添加配置");
        }
        ApiClient apiClient = uploader.getApiClient((FileProperties)fileProperties);
        return apiClient.uploadFile(file);
    }
    @PostMapping({"/multipartUpload"})
    public VirtualFile multipartUpload(MultipartFile file, HttpServletRequest request,StorageTypeConst storageTypeConst) throws IOException {
        FileUploader uploader = new FileUploader();
        final String storageType = storageTypeConst.getStorageType();
        CacheEngine cacheEngine = new FileCacheEngine();
        final Object fileProperties = cacheEngine.get(storageType, "fileProperties");
        if(null == fileProperties){
            throw new ServiceException("该空间未配置,请POST请求/config添加配置");
        }
        final InputStream inputStream = file.getInputStream();
        final String fileName = file.getOriginalFilename();
        ApiClient apiClient = uploader.getApiClient((FileProperties)fileProperties);
        return apiClient.multipartUpload(inputStream,fileName);
    }
    @PostMapping({"/uploads"})
    public String[] uploads(MultipartFile[] files, HttpServletRequest request) {
        List list = new ArrayList();
        String format = this.simpleDateFormat.format(new Date());
        String realPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        System.out.println(realPath);
        File folder = new File(realPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        MultipartFile[] var7 = files;
        int var8 = files.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            MultipartFile file = var7[var9];
            String oldName = file.getOriginalFilename();
            String newName = UUID.randomUUID().toString() + oldName.substring(oldName.lastIndexOf("."));

            try {
                file.transferTo(new File(folder, newName));
                String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/img" + format + newName;
                list.add(url);
            } catch (IOException var14) {
                var14.printStackTrace();
            }
        }

        return (String[])((String[])((String[])((String[])list.toArray(new String[0]))));
    }
    @PostMapping({"/delete"})
    public String delete(String url) {
        final String[] sessionKeys = RequestHolder.getSessionKeys();
        System.out.println(sessionKeys);

//        this.fastdfsClientUtil.deleteFile(url);
        return "ok";
    }
    public static void main(String[] args) throws IOException {
        final FileInputStream fileInputStream = new FileInputStream("/Users/Dabao/git.mp4");
        final String md5Hex = DigestUtils.md5Hex(fileInputStream);
        final FileCacheEngine fileCacheEngine = new FileCacheEngine();
//        FileUtil.down("https://vd4.bdstatic.com/mda-kj0ry8h7pjtsn4fu/sc/mda-kj0ry8h7pjtsn4fu.mp4?v_from_s=hkapp-haokan-tucheng&auth_key=1631587610-0-0-220e26be5a0e7a96d6cc1a210931e0a5&bcevod_channel=searchbox_feed&pd=1&pt=3&abtest=3000185_2","/Users/Dabao/","git");
        fileCacheEngine.add(md5Hex,"file",fileInputStream);
        System.out.println(md5Hex);
    }
}
