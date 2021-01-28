package com.zhouzifei.tool.exception;

/**
 * 
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @note https://www.zhouzifei.com
 * @remark 2019年7月16日
 * @since 1.0
 */
public class CommentException extends ServiceException {
    /**
     * 使用{@code null}作为其*详细消息构造一个新的运行时异常。原因尚未初始化，随后可以通过调用{@link #initCause}进行初始化。
     */
    public CommentException() {
        super();
    }

    /**
     * 使用指定的详细消息构造一个新的运行时异常。 *原因尚未初始化，并且可能随后通过调用{@link #initCause}进行初始化。
     *
     * @param message
     *         详细信息。保存详细消息以供以后通过{@link #getMessage（）}方法检索。
     */
    public CommentException(String message) {
        super(message);
    }
}
