package com.zhouzifei.tool.aspect;

import com.zhouzifei.tool.annotation.BusinessLog;
import com.zhouzifei.tool.util.AspectUtil;
import com.zhouzifei.tool.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


/**
 * AOP切面记录日志
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @note https://www.zhouzifei.com
 * @remark 2019年7月16日
 * @since 1.0
 */
@Slf4j
@Aspect
@Component
public class BusinessLogAspect {


    @Pointcut(value = "@annotation(com.zhouzifei.tool.annotation.BusinessLog)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object writeLog(ProceedingJoinPoint point) throws Throwable {

        //先执行业务
        Object result = point.proceed();

        try {
            handle(point);
        } catch (Exception e) {
            log.error("日志记录出错!", e);
        }

        return result;
    }

    private void handle(ProceedingJoinPoint point) throws Exception {
        Method currentMethod = AspectUtil.INSTANCE.getMethod(point);
        //获取操作名称
        BusinessLog annotation = currentMethod.getAnnotation(BusinessLog.class);
        String businessName = AspectUtil.INSTANCE.parseParams(point.getArgs(), annotation.value());
        String ua = RequestUtil.getUa();

        log.info("{} | {} - {} {} - {}", businessName, RequestUtil.getIp(), RequestUtil.getMethod(), RequestUtil.getRequestUrl(), ua);
    }


}
