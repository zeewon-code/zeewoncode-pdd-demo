package com.zeewoncode.exception;

/**
 * 业务异常类，用于表示业务逻辑中的异常情况
 */
public class BaseException extends RuntimeException{
    public BaseException(String message) {
        super(message);
    }
    public BaseException() {

    }
}
