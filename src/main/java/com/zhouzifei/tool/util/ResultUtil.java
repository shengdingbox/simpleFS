package com.zhouzifei.tool.util;

import com.zhouzifei.tool.consts.CommonConst;
import com.zhouzifei.tool.consts.ResponseStatus;
import com.zhouzifei.tool.dto.ResponseVO;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * 接口返回工具类，支持ModelAndView、ResponseVO、PageResult
 * @author Dabao (17600004572@163.com)
 * @version 1.0
 * @website https://www.zhouzifei.com
 * @date 2019年7月16日
 * @since 1.0
 */
public class ResultUtil {

    public static ModelAndView view(String view) {
        return new ModelAndView(view);
    }

    public static ModelAndView view(String view, Map<String, Object> model) {
        return new ModelAndView(view, model);
    }

    public static ModelAndView redirect(String view) {
        return new ModelAndView("redirect:" + view);
    }

    public static ModelAndView forward(String view) {
        return new ModelAndView("forward:" + view);
    }

    public static ResponseVO error(int code, String message) {
        return vo(code, message, null);
    }

    public static ResponseVO error(ResponseStatus status) {
        return vo(status.getCode(), status.getMessage(), null);
    }

    public static ResponseVO error(String message) {
        return vo(CommonConst.DEFAULT_ERROR_CODE, message, null);
    }

    public static ResponseVO success(String message, Object data) {
        return vo(CommonConst.DEFAULT_SUCCESS_CODE, message, data);
    }

    public static ResponseVO success(Object data) {
        return vo(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMessage(), data);
    }

    public static ResponseVO success(String message) {
        return success(message, null);
    }

    public static ResponseVO success(ResponseStatus status) {
        return vo(status.getCode(), status.getMessage(), null);
    }

    public static ResponseVO vo(int code, String message, Object data) {
        return new ResponseVO<>(code, message, data);
    }

}
