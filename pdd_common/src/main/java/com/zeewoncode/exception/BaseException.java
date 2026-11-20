package com.zeewoncode.exception;

import com.zeewoncode.constant.ErrorCode;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final int code;

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BaseException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}