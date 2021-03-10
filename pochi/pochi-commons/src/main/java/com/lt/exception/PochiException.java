package com.lt.exception;

import com.lt.enums.ResultEnums;

/**
 * 自定义异常
 *
 * @Author: Mr.Tian
 * @Date: 2020/11/8 13:29
 * @Version 1.0
 */
public class PochiException extends RuntimeException {

    private Integer errCode = ResultEnums.ERROR.getCode();

    public PochiException(ResultEnums resultEnums) {
        super(resultEnums.getMsg());
        this.errCode = resultEnums.getCode();
    }

    public PochiException(ResultEnums resultEnums, Throwable throwable) {
        super(resultEnums.getMsg(), throwable);
        this.errCode = resultEnums.getCode();
    }

    public PochiException(Integer code, String msg) {
        super(msg);
        this.errCode = code;
    }

    public PochiException(String msg) {
        super(msg);
    }

    public PochiException(Throwable throwable) {
        super(throwable);
    }

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }
}
