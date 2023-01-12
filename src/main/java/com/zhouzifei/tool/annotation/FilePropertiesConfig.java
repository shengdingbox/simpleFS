//package com.zhouzifei.tool.annotation;
//
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.core.annotation.AliasFor;
//import org.springframework.stereotype.Component;
//
//import java.lang.annotation.*;
//
//@Target({ElementType.TYPE})
//@Retention(RetentionPolicy.RUNTIME)
//@Documented
//@Component
//@Data
//@EqualsAndHashCode(callSuper = true)
//@ConfigurationProperties(prefix = "simple-fs.local")
//public @interface FilePropertiesConfig {
//
//	/**
//	 * The value may indicate a suggestion for a logical component name,
//	 * to be turned into a Spring bean in case of an autodetected component.
//	 * @return the suggested component name, if any (or empty String otherwise)
//	 */
//	@AliasFor(annotation = Component.class)
//	String value() default "";
//
//}