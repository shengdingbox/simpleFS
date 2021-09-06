package com.zhouzifei.tool.controller;

import com.google.common.io.ByteStreams;
import com.zhouzifei.tool.config.properties.FileProperties;
import com.zhouzifei.tool.consts.StorageTypeConst;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.media.file.service.ApiClient;
import com.zhouzifei.tool.media.file.service.FastdfsClientUtil;
import com.zhouzifei.tool.media.file.service.FileUploader;
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
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public static FileProperties fileStaticProperties;

    private static String oldUrl;
    private static String oldName;

    public FileUploadController() {
    }

    @GetMapping({"/upload"})
    public ModelAndView upload(ModelAndView modelAndView) {
        modelAndView.setViewName("upload");
        return modelAndView;
    }
    @GetMapping({"/config"})
    public ModelAndView config(ModelAndView modelAndView) {
        final HttpSession session = request.getSession();
        //FileProperties config = (FileProperties)session.getAttribute("config");
        if(null == fileStaticProperties){
            fileStaticProperties = new FileProperties();
            fileStaticProperties.setStorageTypeConst(StorageTypeConst.ALIYUN);
            fileStaticProperties.setAccessId("");
            fileStaticProperties.setSecretKey("");
            fileStaticProperties.setAccessId("");
            fileStaticProperties.setAccessId("");
            fileStaticProperties.setAccessId("");
            fileStaticProperties.setAccessId("");
        }
        final Map<Object, Object> list = StorageTypeConst.getMap();
        modelAndView.addObject("config",fileStaticProperties);
        modelAndView.addObject("list",list);
        modelAndView.setViewName("config");
        return modelAndView;
    }
    @PostMapping({"/config"})
    public String config(FileProperties fileProperties) {
        final HttpSession session = request.getSession();
        session.setAttribute("config",fileProperties);
        fileStaticProperties = fileProperties;
        return "ok";
    }

    @GetMapping({"/index"})
    public ModelAndView index(ModelAndView modelAndView) {
        modelAndView.setViewName("index");
        return modelAndView;
    }
    @GetMapping({"/aliyun"})
    public ModelAndView aliyun(ModelAndView modelAndView) {
        modelAndView.setViewName("config");
        return modelAndView;
    }
    @PostMapping({"/upload"})
    public VirtualFile upload(MultipartFile file, HttpServletRequest request) {
        FileUploader uploader = new FileUploader();
        if(null == fileStaticProperties.getStorageTypeConst()){
            final StorageTypeConst enumType = StorageTypeConst.getEnumType(fileStaticProperties.getStorageType());
            fileStaticProperties.setStorageTypeConst(enumType);
        }
        ApiClient apiClient = uploader.getApiClient(fileStaticProperties);
        final VirtualFile virtualFile = apiClient.uploadFile(file);
        return virtualFile;
    }
    @PostMapping({"/multipartUpload"})
    public VirtualFile multipartUpload(MultipartFile file, HttpServletRequest request) throws IOException {
        FileUploader uploader = new FileUploader();
        if(null == fileStaticProperties.getStorageTypeConst()){
            final StorageTypeConst enumType = StorageTypeConst.getEnumType(fileStaticProperties.getStorageType());
            fileStaticProperties.setStorageTypeConst(enumType);
        }
        ApiClient apiClient = uploader.getApiClient(fileStaticProperties);
        final InputStream inputStream = file.getInputStream();
        final String fileName = file.getOriginalFilename();
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
        this.fastdfsClientUtil.deleteFile(url);
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
    @PostMapping({"/open"})
    public ModelAndView open(String idNbr,ModelAndView modelAndView) {
        if("1".equals(idNbr)){
            isUpload = true;
        }else if("0".equals(idNbr)){
            isUpload = false;
        }
        modelAndView.setViewName("index");
        modelAndView.addObject("idNbr",idNbr);
        return modelAndView;
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
    public static void main(String[] args) {
        String s = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        System.out.println(s);
    }
}
