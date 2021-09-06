package com.zhouzifei.tool.media.file.listener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 上传文件
 * @author zhouzezhong & Happy Fish YuQing
 * @version 1.11版
 */
public class UploadStream implements UploadCallback {
    private final InputStream inputStream;
    private long fileSize = 0;

    /**
     * 构造函数
     * @param inputStream 用于上传的输入流
     * @param fileSize 上传文件的大小
     */
    public UploadStream(InputStream inputStream, long fileSize) {
        super();
        this.inputStream = inputStream;
        this.fileSize = fileSize;
    }

    /**
     * 结束文件内容回调函数，仅在文件上传时调用一次
     * @param out 输出流写入文件内容
     * @return 0 成功，失败则返回none 0(errno)
     */
    @Override
    public int send(OutputStream out) throws IOException {
        long remainBytes = fileSize;
        byte[] buff = new byte[256 * 1024];
        int bytes;
        while (remainBytes > 0) {
            try {
                if ((bytes = inputStream.read(buff, 0, remainBytes > buff.length ? buff.length : (int) remainBytes)) < 0) {
                    return -1;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return -1;
            }

            out.write(buff, 0, bytes);
            remainBytes -= bytes;
        }

        return 0;
    }
}
