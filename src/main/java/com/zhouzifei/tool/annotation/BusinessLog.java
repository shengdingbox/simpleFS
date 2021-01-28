package com.zhouzifei.tool.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 日志记录、自定义注解
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0


 * @remark 2019年7月16日
 * @since 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessLog {
    /**
     * 业务的名称
     */
    String value() default "";
    /**
     * 是否将当前日志记录到数据库中
     */
    boolean save() default true;

}
