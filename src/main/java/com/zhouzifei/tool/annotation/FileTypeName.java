package com.zhouzifei.tool.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author Dabao
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface FileTypeName{
	String value() default "";

}
