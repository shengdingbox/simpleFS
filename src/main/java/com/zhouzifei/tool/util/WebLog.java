package com.zhouzifei.tool.util;

import java.lang.annotation.*;

/**
 * @author 周子斐 (17600004572@163.com)
 * @remark 2020/6/26
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface WebLog {
    /**
     * 日志描述信息
     * @return String
     */
    String description() default "";

    /**
     * 是否打印返回值
     * @return boolean
     */
    boolean isPutResponse() default true;

}
