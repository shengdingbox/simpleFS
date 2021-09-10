package com.zhouzifei.tool.annotation;

import com.zhouzifei.tool.common.CommonWebConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({CommonWebConfig.class})
public @interface EnableCommonWeb {
}