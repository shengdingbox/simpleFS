package com.zhouzifei.tool.fileClient;

import com.zhouzifei.tool.config.FileConfig;

import java.util.function.Function;

/**
 * @author 周子斐
 * @date 2022/3/14
 * @Description
 */
public class ApiClientBuild {

    private String source;
    private FileConfig fileConfig;

    public ApiClientBuild builder() {
        return new ApiClientBuild();
    }

    public ApiClientBuild source(String source) {
        this.source = source;
        return this;
    }

    public ApiClientBuild fileConfig(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
        return this;
    }
    public ApiClientBuild build() {

    }
}
