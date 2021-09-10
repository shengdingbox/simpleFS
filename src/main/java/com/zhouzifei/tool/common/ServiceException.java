package com.zhouzifei.tool.common;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String errorCode;
    private String message;

    public ServiceException(String errorCode, String errorMsg) {
        super(errorCode);
        this.errorCode = errorCode;
        this.message = errorMsg;
    }
    public ServiceException(String errorMsg) {
        super("999999");
        this.errorCode = "999999";
        this.message = errorMsg;
    }
    public ServiceException(String errorCode, String errorMsg, Throwable t) {
        super(errorCode, t);
        this.errorCode = errorCode;
        this.message = errorMsg;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getMessage() {
        return this.message;
    }
    public static String getTrace(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        StringBuffer buffer = stringWriter.getBuffer();
        return buffer.toString();
    }
}
