package com.grgbanking.grgacd.common;

/**
 * @author wjqiu
 * @date 2019-05-29
 * @description Caller状态
 */

public enum CallerStatus {

    /**
     * 在线
     */
    LOGIN,
    /**
     * 呼叫中 正在呼叫队列中，等待处理
     */
    MAKECALL,
    /**
     * 在Pending队列中，当前正在等待分配Agent
     */
    PENDING,
    /**
     * 振铃中 已经分配到Agent，正在在等待agent接听通话
     */
    RINGING,
    /**
     * 服务中
     */
    SERVICE

}
