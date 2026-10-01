package com.zeewoncode.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果类
 * @param <T>
 */
@Data
public class Result<T> implements Serializable {

    //所有请求的响应结果都封装成Result
    private Integer code; //编码：1.成功，0.失败
    private String msg; //错误信息
    private T data; //业务数据

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<>();
        result.code = 1;
        result.msg = "ok";
        result.data = object;
        return result;
    }

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 1;
        result.msg = "ok";
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.code = 0;
        result.msg = msg;
        return result;
    }

}
