package com.zhouzifei.tool.config;


import com.zhouzifei.tool.media.file.service.FastdfsClientUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({FastdfsClientUtil.class})
public class FdfsConfig {
    public FdfsConfig() {
    }
}
