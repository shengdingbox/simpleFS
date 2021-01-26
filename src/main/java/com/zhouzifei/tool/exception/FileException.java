package com.zhouzifei.tool.exception;

/**
 * 
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @website https://www.zhouzifei.com
 * @date 2019年7月16日
 * @since 1.0
 */
public class FileException extends ServiceException {
    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public FileException() {
        super();
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message
     *         the detail message. The detail message is saved for
     *         later retrieval by the {@link #getMessage()} method.
     */
    public FileException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message
     *         the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param cause
     *         the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since 1.4
     */
    public FileException(String message, Throwable cause) {
        super(message, cause);
    }
}
