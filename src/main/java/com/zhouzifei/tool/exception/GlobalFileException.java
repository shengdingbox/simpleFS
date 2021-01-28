package com.zhouzifei.tool.exception;

/**
 * 
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @note https://www.zhouzifei.com
 * @remark 2019年7月16日
 * @since 1.0
 */
public class GlobalFileException extends RuntimeException {
    public GlobalFileException() {
        super();
    }

    public GlobalFileException(String message) {
        super(message);
    }

    public GlobalFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public GlobalFileException(Throwable cause) {
        super(cause);
    }

    protected GlobalFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
