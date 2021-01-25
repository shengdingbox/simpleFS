package com.zhouzifei.tool.aspect;

import com.zhouzifei.tool.annotation.BussinessLog;
import com.zhouzifei.tool.consts.PlatformEnum;
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
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
@Slf4j
@Aspect
@Component
public class BussinessLogAspect {


    @Pointcut(value = "@annotation(com.zhouzifei.tool.annotation.BussinessLog)")
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
        BussinessLog annotation = currentMethod.getAnnotation(BussinessLog.class);
        boolean save = annotation.save();
        PlatformEnum platform = annotation.platform();
        String bussinessName = AspectUtil.INSTANCE.parseParams(point.getArgs(), annotation.value());
        String ua = RequestUtil.getUa();

        log.info("{} | {} - {} {} - {}", bussinessName, RequestUtil.getIp(), RequestUtil.getMethod(), RequestUtil.getRequestUrl(), ua);
        if (!save) {
            return;
        }
    }


}
