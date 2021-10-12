package com.zhouzifei.tool.media.file.listener;

import java.io.IOException;
import java.io.OutputStream;

/**
 * upload file callback interface
 *
 * @author Happy Fish / YuQing
 * @version Version 1.0
 */
public interface UploadCallback {
  /**
   * 发送文件内容回调函数，文件上传时只调用一次
   * @param out 输出流写入文件内容
   * @return 0成功，失败则返回none 0(errno)
   */
  public int send(OutputStream out) throws IOException;
}
