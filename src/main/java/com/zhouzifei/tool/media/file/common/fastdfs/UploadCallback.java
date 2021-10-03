package com.zhouzifei.tool.media.file.common.fastdfs;

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
   * send file content callback function, be called only once when the file uploaded
   *
   * @param out output stream for writing file content
   * @return 0 success, return none zero(errno) if fail
   */
  public int send(OutputStream out) throws IOException;
}
