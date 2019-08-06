package com.github.pig.common.util;

import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @param <T>
 * @author grg
 */
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int NO_LOGIN = -1;

    public static final int SUCCESS = 0;

    public static final int FAIL = 1;

    public static final int NO_PERMISSION = 2;

    private String msg = "";

    private int code = SUCCESS;

    private Object data;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    private boolean success = true;

    public R() {
        super();
    }

    public R(Boolean success) {
        super();
        initSuccess(success);
    }
    /**
     * @deprecated 需要删除此方法
     */
    public R(T data, RCode rCode) {    //FIXME 需要删除此方法
        super();
        this.data = data;
        this.code = rCode.getCode();
        this.msg = rCode.getMsg();
    }
    public R(Boolean success, Object data) {
        super();
        initSuccess(success);
        this.data = data;
    }
    public R(Boolean success, RespCode rCode) {
        super();
        this.code = rCode.getCode();
        this.msg = rCode.getMsg();
        this.success = success;
    }
    public R(RespCode rCode) {
        super();
        setRespCode(rCode);
    }
    public R(RespCode rCode,Object data) {
        super();
        setRespCode(rCode);
        this.data = data;
    }
    public R(Throwable e) {
        super();
        this.msg = e.getMessage();
        this.code = FAIL;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setRespCode(RespCode rCode) {
        this.code = rCode.getCode();
        this.msg = rCode.getMsg();
        if (rCode == RespCode.SUCCESS){
            this.success = true;
        } else {
            this.success = false;
        }
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private void initSuccess(Boolean isSuccess){
        if (isSuccess) {
            this.code = RespCode.SUCCESS.getCode();
            this.msg = RespCode.SUCCESS.getMsg();
        } else {
            this.code = RespCode.FAIL.getCode();
            this.msg = RespCode.FAIL.getMsg();
        }
        this.success = isSuccess;
    }
}
