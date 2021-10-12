package com.zhouzifei.tool.media.file.util;


import com.zhouzifei.tool.common.ServiceException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
 * @since 1.0
 */
public class StreamUtil {

    /**
     * 将InputStream转换为字符串
     *
     * @param is InputStream
     * @return
     */
    public static String toString(InputStream is) {
        return toString(is, "UTF-8");
    }

    /**
     * 将InputStream转换为字符串
     *
     * @param is InputStream
     * @return
     */
    public static String toString(InputStream is, String encoding) {
        if (null == is) {
            return null;
        }
        encoding = encoding == null ? "UTF-8" : encoding;
        StringBuilder fileContent = new StringBuilder();
        try (
                InputStream inputStream = is;
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, encoding))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line);
                fileContent.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileContent.toString();
    }

    /**
     * 复制InputStream
     *
     * @param is InputStream
     * @return
     */
    public static InputStream clone(InputStream is) {
        if(null == is){
            throw new ServiceException("无法获取文件流，文件不可用！");
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (IOException e) {
            throw new ServiceException("无法复制当前文件流！{}", e.getMessage());
        }
    }
}
