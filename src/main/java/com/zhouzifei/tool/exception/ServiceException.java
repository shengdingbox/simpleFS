package com.zhouzifei.tool.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author 周子斐 (17600004572@163.com)
 * @remark 2021/1/22

 * @Description
 */
public class ServiceException extends RuntimeException {
    /**
     * 使用 {@code null} 作为其详细消息构造一个新的运行时异常。原因未初始化，随后可能会通过调用 {@link initCause} 进行初始化。
     */
    public ServiceException() {
        super();
    }

    /**
     * 使用指定的详细消息构造一个新的运行时异常。原因未初始化，随后可能会通过调用 {@link initCause} 进行初始化。
     *
     * @param message
     *         详细信息。详细消息被保存以供稍后通过 {@link getMessage()} 方法检索。
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * 使用指定的详细消息和原因构造一个新的运行时异常。 <p>请注意，与 {@code cause} 关联的详细消息<i>不会<i>自动合并到此运行时异常的详细消息中。
     *
     * @param message
     *         详细消息（保存以供稍后通过 {@link getMessage()} 方法检索）。
     * @param cause
     *         原因（通过 {@link getCause()} 方法保存以供以后检索）。 （允许使用 <tt>null<tt> 值，表示原因不存在或未知。）
     * @since 1.4
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public static String getTrace(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        StringBuffer buffer = stringWriter.getBuffer();
        return buffer.toString();
    }
}
