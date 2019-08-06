package com.github.pig.common.util.exception;


import com.github.pig.common.util.RespCode;

/**
 * @author wjqiu
 * @date 2019-06-04
 *
 * 用于统一的异常返回。如在返回类型为R 的Controller方式中直接抛出此异常，可被AOP截获，实现
 * 统一格式的异常信息返回。
 */

public class GrgException extends RuntimeException {
    private RespCode statusCode;
    //无参构造
    public GrgException() {}
    public GrgException(String msg) {
        super(msg);
    }
    public GrgException(RespCode statusCode) {
        super(statusCode.getMsg());
        setStatusCode(statusCode);
    }
    public RespCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(RespCode statusCode) {
        this.statusCode = statusCode;
    }
}
