package com.lt.advice;

import com.lt.exception.PochiException;
import com.lt.vo.Result;
import com.lt.enums.ResultEnums;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局异常处理
 *
 * @Author: Mr.Tian
 * @Date: 2020/11/8 13:33
 * @Version 1.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {

    /**
     * 全局处理自定义异常
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(PochiException.class)
    public Result<?> exceptionHandler(PochiException exception) {
        log.error("统一异常处理：", exception);
        return new Result<>(exception.getErrCode(), exception.getMessage());
    }

    /**
     * 处理权限不足异常
     * @param exception
     * @return
     */
    @ExceptionHandler(AuthorizationException.class)
    public Result<?> authorizationHandler(AuthorizationException exception) {
        log.error("权限不足异常处理，", exception);
        return new Result<>(ResultEnums.AUTH_NOT_FOUNT);
    }

    /**
     * 当系统出现MethodArgumentNotValidException这个异常时，会调用下面的方法，参数是json的时候
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result<?> jsonErrorHandler(MethodArgumentNotValidException e){
        return getAjaxResult(e.getBindingResult());
    }
    /**
     * 当系统出现BindException这个异常时，会调用下面的方法，参数是对象的时候
     * @param e
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    public Result<?> jsonErrorHandlerForParams(BindException e){
        return getAjaxResult(e.getBindingResult());
    }

    /**
     * 重新包装异常数据
     * @param bindingResult
     * @return
     */
    private Result<?> getAjaxResult(BindingResult bindingResult) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        for (ObjectError allError : allErrors) {
            Map<String, Object> map = new HashMap<>();
            map.put("defaultMessage", allError.getDefaultMessage());
            map.put("objectName", allError.getObjectName());
            //注意，这里面拿到具体的某一个属性
            FieldError fieldError = (FieldError) allError;
            map.put("field", fieldError.getField());
            list.add(map);
        }
        return new Result<>(ResultEnums.SERVER_ERROR.getCode(),"后端数据校验异常",list);
    }

}
