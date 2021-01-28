package com.zhouzifei.tool.media.file;

import com.zhouzifei.tool.media.file.service.DownloadListener;
import com.zhouzifei.tool.media.file.service.M3u8DownloadFactory;
import com.zhouzifei.tool.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 周子斐
 * @date 2021/1/28
 * @Description
 */
@Slf4j
public class M3u8DownloadUtil {
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
    /**
     * 下载视频
     */
    public void downloadM3u8Video() {

        //校验字段
        if(!checkFields()) {
            return;
        }
        //保存目录设置为以“/”结尾
        if(!filePath.endsWith("/")) {
            filePath = filePath+"/";
        }
        M3u8DownloadFactory.M3u8Download m3u8Download = M3u8DownloadFactory.getInstance(m3u8Url);
        //设置生成目录
        m3u8Download.setDir(filePath);
        //设置视频名称
        m3u8Download.setFileName(fileName);
        //设置线程数
        m3u8Download.setThreadCount(Integer.parseInt(threadCount));
        //设置重试次数
        m3u8Download.setRetryCount(Integer.parseInt(retryCount));
        //设置连接超时时间（单位：毫秒）
        m3u8Download.setTimeoutMillisecond(Long.parseLong(timeout)*1000);
        //设置监听器间隔（单位：毫秒）
        m3u8Download.setInterval(1000L);
        //添加监听器
        m3u8Download.addListener(new DownloadListener() {
            @Override
            public void prepare(int step, String msg) {
                log.info("正在解析视频地址，请稍后...");
            }

            @Override
            public void start(int sum) {
                log.info("检测到"+sum+"个视频片段，开始下载！");
                log.info("0% (0/"+sum+")");
            }

            @Override
            public void process(String downloadUrl, int finished, int sum, float percent) {
                log.info("已下载" + finished + "个\t一共" + sum + "个\t已完成" + percent + "%");
                log.info(percent+"% ("+finished+"/"+sum+")");
            }

            @Override
            public void speed(String speedPerSecond) {
                log.info("下载速度：" + speedPerSecond);
            }

            @Override
            public void end() {
                log.info("下载完毕");
            }
        });

        //开始下载
        m3u8Download.runTask();
    }
    /**
     * 校验字段
     * @return
     */
    public boolean checkFields() {
        if(StringUtils.isBlank(m3u8Url)) {
            log.info("请填写视频地址");
            return false;
        }
        if ("m3u8".compareTo(MediaFormat.getMediaFormat(m3u8Url)) != 0) {
            log.info(m3u8Url+"不是一个完整m3u8链接!");
            return false;
        }
        if(StringUtils.isBlank(filePath)) {
            log.info("请选择或输入保存目录！");
            return false;
        }
        //保存目录设置为以“/”结尾
        if(!filePath.endsWith("/")) {
            filePath = filePath+"/";
        }
        if(StringUtils.isBlank(fileName)) {
            log.info("保存文件名不得为空！");
            return false;
        }
        if(StringUtils.isBlank(threadCount)) {
            log.info("线程数不得为空！");
            return false;
        }
        if(!StringUtils.isMatch("^[1-9]\\d*$", threadCount)) {
            log.info("线程数必须为正整数！");
            return false;
        }
        if(StringUtils.isBlank(threadCount)) {
            log.info("重试次数不得为空！");
            return false;
        }
        if(!StringUtils.isMatch("^\\d+$", threadCount)) {
            log.info("重试次数必须为大于或等于0的整数！");
            return false;
        }
        if(StringUtils.isBlank(timeout)) {
            log.info("连接超时时间不得为空！");
            return false;
        }
        if(!StringUtils.isMatch("^[1-9]\\d*$", timeout)) {
            log.info("连接超时时间必须为正整数！");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        M3u8DownloadUtil m3u8Download = new M3u8DownloadUtil();
        m3u8Download.m3u8Url="https://api.dabaotv.cn/cache/iqiyi/2021012811/a12d29965b6c42b59d46416bb38ee77a.m3u8";
        m3u8Download.fileName="测试";
        m3u8Download.filePath="/Users/Dabao/";
        m3u8Download.retryCount="5";
        m3u8Download.threadCount="3";
        m3u8Download.timeout="3000";
        m3u8Download.downloadM3u8Video();
    }
}
