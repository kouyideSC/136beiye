package com.caipiao.service.exception;

/**
 * 业务层异常通用定义
 * Created by kouyi on 2017/9/22.
 */
public class ServiceException extends Exception {

    private int errorCode;

    public ServiceException() {
        super();
    }

    public ServiceException(int errorCode) {
        this.errorCode = errorCode;
    }

    public ServiceException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
}
