package com.zhouzifei.tool.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

@ControllerAdvice
@Slf4j
public class WebExceptionHandler {


    @ExceptionHandler({ServiceException.class})
    @ResponseStatus(
            code = HttpStatus.OK
    )
    @ResponseBody
    public Response<?> handleServiceException(HttpServletRequest request, ServiceException e) {
        log.error("error ", e);
        return Response.code(e.getErrorCode()).withMessage(e.getMessage());
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(
            code = HttpStatus.INTERNAL_SERVER_ERROR
    )
    @ResponseBody
    public Response<?> defaultExceptionHandler(HttpServletRequest request, Exception e) {
        log.error("error ", e);
        return Response.code("999999").withMessage("internal error");
    }

    @ResponseStatus(
            code = HttpStatus.OK
    )
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public Response<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("验证异常：", e);
        BindingResult bindingResult = e.getBindingResult();
        String firstErrorMsg = null;
        Iterator var4 = bindingResult.getFieldErrors().iterator();
        if (var4.hasNext()) {
            FieldError fieldError = (FieldError)var4.next();
            firstErrorMsg = fieldError.getDefaultMessage();
        }

        return Response.code("999998").withMessage(firstErrorMsg);
    }

    @ResponseStatus(code = HttpStatus.OK)
    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseBody
    public Response<?> illegalArgmentException(IllegalArgumentException e) {
        log.error("参数异常：", e);
        return Response.code("999998").withMessage(e.getMessage());
    }
}
