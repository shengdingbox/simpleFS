package com.zhouzifei.tool.controller;

import com.google.common.io.ByteStreams;
import com.zhouzifei.tool.cache.CacheEngine;
import com.zhouzifei.tool.cache.FileCacheEngine;
import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.config.properties.FileProperties;
import com.zhouzifei.tool.consts.StorageTypeConst;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.holder.RequestHolder;
import com.zhouzifei.tool.media.file.FileUtil;
import com.zhouzifei.tool.media.file.service.ApiClient;
import com.zhouzifei.tool.media.file.service.FastdfsClientUtil;
import com.zhouzifei.tool.media.file.service.FileUploader;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
public class FileUploadController {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("/yyyy/MM/hh/");

    static  boolean isUpload;
    @Value("${fdst.url}")
    String fdstUrl;
    @Autowired
    FastdfsClientUtil fastdfsClientUtil;
    @Autowired
    HttpServletRequest request;

    private static String oldUrl;
    private static String oldName;

    @PostMapping({"/upload"})
    public VirtualFile upload(MultipartFile file, HttpServletRequest request, StorageTypeConst storageTypeConst) {
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

    @PostMapping({"/upload2"})
    public ModelAndView upload2(MultipartFile file, ModelAndView modelAndView) {
//        if(!isUpload){
//            return null;
//        }
        String format = this.simpleDateFormat.format(new Date());
        ApplicationHome applicationHome = new ApplicationHome(this.getClass());
        File source = applicationHome.getSource();
        String realPath = source.getParentFile().toString() + "/upload/";
        System.out.println(realPath);
        File folder = new File(realPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String name = file.getOriginalFilename();
        System.out.println(name);
        String s = this.fastdfsClientUtil.fileUpload(file);
        modelAndView.setViewName("url");
        modelAndView.addObject("download",fdstUrl+oldUrl+"?attname="+oldName);
        modelAndView.addObject("url",fdstUrl+s);
        oldUrl = s;
        oldName = name;
        return modelAndView;
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
    @GetMapping({"/oldUrl"})
    public ModelAndView delete(String url,ModelAndView modelAndView,HttpServletResponse response) {
        modelAndView.setViewName("url");
        modelAndView.addObject("download",fdstUrl+oldUrl+"?attname="+oldName);
        modelAndView.addObject("name",fdstUrl+oldUrl);
        return modelAndView;
        //download(fdstUrl+oldUrl,response);
    }

    @PostMapping({"/download"})
    public void download(String url,  HttpServletResponse response) {
        ServletOutputStream out = null;
        try {
            byte[] bytes = this.fastdfsClientUtil.downFile(url);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(oldName, "UTF-8"));
            response.setContentType("application/force-download");
            //String ext = Files.getFileExtension(fileName);
            //response.setContentType(getContentType(ext));
            response.setCharacterEncoding("UTF-8");
            out = response.getOutputStream();
            ByteStreams.copy(byteArrayInputStream, out);
            out.flush();
            //fastdfsClientUtil.deleteFile(url);
        } catch (IOException e) {
           throw new RuntimeException(e.getMessage());
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
