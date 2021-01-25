package com.zhouzifei.tool.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author 周子斐
 * @date 2021/1/22
 * @Description
 */
public class ServiceException {
    public static String getTrace(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        StringBuffer buffer = stringWriter.getBuffer();
        return buffer.toString();
    }
}
