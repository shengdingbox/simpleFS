package com.zhouzifei.tool.util;

import java.lang.reflect.Method;
import java.util.List;

import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.html.encryption.RegexUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

/**
 * AOP相关的工具
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
 * @since 1.0
 */
public enum AspectUtil {

    INSTANCE;

    /**
     * 获取切面缓存的key
     *
     * @param point  当前切面执行的方法
     * @param extra  额外的参数 （非必选）
     * @param prefix key前缀 （非必选）
     * @throws NoSuchMethodException
     */
    public String getKey(JoinPoint point, String extra, String prefix) throws NoSuchMethodException {
        Method currentMethod = this.getMethod(point);
        if (null == currentMethod) {
            throw new ServiceException("Invalid operation! Method not found.");
        }
        String methodName = currentMethod.getName();
        return getKey(point, prefix) +
                "_" +
                methodName +
                CacheKeyUtil.getMethodParamsKey(point.getArgs()) +
                (null == extra ? "" : extra);
    }

    /**
     * 获取以类路径为前缀的键
     *
     * @param point 当前切面执行的方法
     */
    public String getKey(JoinPoint point, String prefix) {
        String keyPrefix = "";
        if (!StringUtils.isEmpty(prefix)) {
            keyPrefix += prefix;
        }
        keyPrefix += getClassName(point);
        return keyPrefix;
    }

    /**
     * 获取当前切面执行的方法所在的class
     *
     * @param point 当前切面执行的方法
     */
    public String getClassName(JoinPoint point) {
        return point.getTarget().getClass().getName().replaceAll("\\.", "_");
    }

    /**
     * 获取当前切面执行的方法的方法名
     *
     * @param point 当前切面执行的方法
     */
    public Method getMethod(JoinPoint point) throws NoSuchMethodException {
        Signature sig = point.getSignature();
        MethodSignature msig = (MethodSignature) sig;
        Object target = point.getTarget();
        return target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
    }

    public String parseParams(Object[] params, String bussinessName) {
        if (bussinessName.contains("{") && bussinessName.contains("}")) {
            List<String> result = RegexUtils.match(bussinessName, "(?<=\\{)(\\d+)");
            for (String s : result) {
                int index = Integer.parseInt(s);
                bussinessName = bussinessName.replaceAll("\\{" + index + "}", JSON.toJSONString(params[index - 1]));
            }
        }
        return bussinessName;
    }
}
