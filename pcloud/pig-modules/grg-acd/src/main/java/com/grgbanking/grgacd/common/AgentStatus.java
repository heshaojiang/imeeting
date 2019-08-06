package com.grgbanking.grgacd.common;

/**
 * 客服状态变迁
 * @author tjshan
 * @since 2019/5/9 10:49
 */
public enum AgentStatus {

    /**
     * 离线
     */
    OFFLINE,
    /**
     *在线
     */
    ONLINE,
    /**
     * 就绪
     */
    AVAILABLE,
    /**
     * 休息
     */
    AUX,
    /**
     * 临时走开
     */
    ONBREAK,
    /**
     * 服务中
     */
    SERVICE,
    /**
     * 文档处理
     */
    ACW,
    /**
     * 选中
     */
    SELECTED

}
