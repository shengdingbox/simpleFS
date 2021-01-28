package com.zhouzifei.tool.util;

import java.lang.annotation.*;

/**
 * @author 周子斐 (zhouzf@asp.citic.com)
 * @remark 2020/6/26
 * @Description
 * @Copyright 中信网络科技有限公司 Copyright (c)
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
