package com.zhouzifei.tool.controller;

import com.zhouzifei.tool.cache.CacheEngine;
import com.zhouzifei.tool.cache.FileCacheEngine;
import com.zhouzifei.tool.config.properties.FileProperties;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {


    @ApiOperation(value = "序列号", notes = "序列号")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(required = true, name = "storageTypeConst", value = "云存储类型", dataType = "StorageTypeConst", paramType = "query"),
            @ApiImplicitParam(required = false, name = "pathPrefix", value = "文件前缀", dataType = "String", paramType = "path"),
            @ApiImplicitParam(required = true, name = "domainUrl", value = "服务器域名", dataType = "String", paramType = "path"),
            @ApiImplicitParam(required = false, name = "bucketName", value = "Bucket 名称", dataType = "String", paramType = "path"),
            @ApiImplicitParam(required = false, name = "accessId", value = "AccessKey", dataType = "String", paramType = "path"),
            @ApiImplicitParam(required = false, name = "secretKey", value = "Secret Key", dataType = "String", paramType = "path"),
            @ApiImplicitParam(required = false, name = "endpoint", value = "地域节点（EndPoint）", dataType = "String", paramType = "path"),
            @ApiImplicitParam(required = false, name = "serverUrl", value = "服务器地址", dataType = "String", paramType = "path"),
            @ApiImplicitParam(required = false, name = "localFilePath", value = "本地地址", dataType = "String", paramType = "path")
    })
    @PostMapping({"/config"})
    public String config(FileProperties fileProperties) {
        CacheEngine cacheEngine = new FileCacheEngine();
        final String storageType = fileProperties.getStorageTypeConst().getStorageType();
        cacheEngine.add(storageType,"fileProperties",fileProperties);
        return "ok";
    }


    public static void main(String[] args) {
        String s = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        System.out.println(s);
    }
}
