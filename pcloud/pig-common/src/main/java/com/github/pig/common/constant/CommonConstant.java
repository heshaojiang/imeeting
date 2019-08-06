package com.github.pig.common.constant;

/**
 * @author fmsheng
 * @param
 * @description  default
 * @date 2019/1/12 14:31
 */
public interface CommonConstant {

    /** @NOTE: 公共Constant */
    /**
     * 删除
     */
    String STATUS_DEL = "1";
    /**
     * 正常
     */
    String STATUS_NORMAL = "0";
    /**
     * 删除标记
     */
    String DEL_FLAG = "del_flag";

    /** @NOTE: 认证Constant */
    /**
     * token请求头名称
     */
    String REQ_HEADER = "Authorization";

    /**
     * token分割符
     */
    String TOKEN_SPLIT = "Bearer ";

    /**
     * client分割符
     */
    String CLIENT_SPLIT = "Basic ";

    /**
     * jwt签名
     */
    String SIGN_KEY = "GRG";
    /**
     * 锁定
     */
    String STATUS_LOCK = "9";
    /**
     * 编码
     */
    String UTF8 = "UTF-8";

    /**
     * JSON 资源
     */
    String CONTENT_TYPE = "application/json; charset=utf-8";

    /**
     * 阿里大鱼
     */
    String ALIYUN_SMS = "aliyun_sms";

    /**
     * 成功
     */
    String SUCCESS = "登录校验成功";

    /** @NOTE: 其它Constant */
    /**
     * 菜单
     */
    String MENU = "0";

    /**
     * 按钮
     */
    String BUTTON = "1";

    String ENUS="en-US";

}
