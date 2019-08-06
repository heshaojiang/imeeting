package com.grgbanking.grgacd.common;

/**
 * 通话状态：
 * @author tjshan
 * @since 2019/5/8 15:54
 */
public enum CallStatus {

    /**
     * 呼叫中
     */
    LINE,
    /**
     * 响铃中
     */
    RING,
    /**
     * 已接通
     */
    CONNECT,
    /**
     * 正常挂断
     */
    HANGUP,
    /**
     * 呼叫超时
     */
    TIMEOUT,
    /**
     * 异常挂断
     */
    ERROR,
    /**
     * 坐席拒接
     */
    CALL_REJECT,
    /**
     * Caller取消呼叫
     */
    CALL_CANCEL

}
