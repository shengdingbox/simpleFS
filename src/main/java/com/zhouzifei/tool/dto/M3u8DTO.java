package com.zhouzifei.tool.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author 周子斐
 * @date 2021/1/28
 * @Description
 */
@Data
@Builder
public class M3u8DTO {
    /**
     * 文件保存的路径
     */
    private String filePath;
    /**
     * 要下载的index.m3u8的url
     */
    private String m3u8Url;
    /**
     * //保存的视频文件名，不带后缀名
     */
    private String fileName;
    /**
     * //线程数
     */
    private String threadCount;
    /**
     * //重试次数
     */
    private String retryCount;
    /**
     * //连接超时时间（单位：毫秒）
     */
    private String timeout;
}
