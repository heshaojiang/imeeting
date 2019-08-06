package com.github.pig.common.util;

/**
 * @author fmsheng
 * @param
 * @description 管理类
 * @date 2019/1/14 9:42
 */
public enum RCode {

    /** @NOTE: 公共 RCode */
    //insert 1XXX
    SUCCESS(1000, "新建成功"),
    ADDFAIL(-1001, "新建失败"),

    //delete 2XXX

    //update 3XXX
    UPDATESUCCESS(3000, "更新成功"),
    UPDATEFAIL(-3001, "更新失败"),

    //selete 4XXX

    /**
     * @NOTE: 用户管理 RCode
     * @CODE: 5XXX
     */
    USERNAMEEMPTY(-5001, "用户名不能为空"),
    USERHADEXIST(-5002, "用户已存在"),

    /**
     * @NOTE: other RCode
     * @CODE: 4XXXX
     */
    UNAUTHORIZED(-40001, "未授权");


    private int code;
    private String msg;

    RCode(int code, String msg) {
        this.msg = msg;
        this.code = code;
    }

    public int getCode() {
        return code;
    }
    public String getMsg() {
//        try {
//            return URLDecoder.decode(msg, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        return msg;
    }
}
