package com.zhouzifei.tool.media.file.service;

/**
 * m3u8下载监听器
 * @author 周子斐 (17600004572@163.com)
 * @date 2020/3/8
 */
public interface DownloadListener {

    void prepare(int step, String msg);

    void start(int sum);

    void process(String downloadUrl, int finished, int sum, float percent);

    void speed(String speedPerSecond);

    void end();

}
