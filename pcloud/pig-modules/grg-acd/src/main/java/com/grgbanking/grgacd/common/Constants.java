package com.grgbanking.grgacd.common;

/**
 * @author wjqiu
 * @date 2019-04-21
 */
public class Constants {
    public static final String KEY_QUEUE = "acd:queue:";
    public static final String KEY_QUEUE_CALLER = KEY_QUEUE+"caller:";
    public static final String KEY_QUEUE_AGENT = KEY_QUEUE+"agent:";
    public static final String KEY_QUEUE_CALLER_PENDING = KEY_QUEUE+"caller_pending:";

    public static final String KEY_CALLER = "acd:caller:";
    public static final String KEY_AGENT = "acd:agent:";

    public static final String DEF_QUEUE_ID = "def_queue";

    public static final String PLATFORM_TYPE_IMEETING = "imeeting";
    public static final String PLATFORM_TYPE_WEIXIN = "weixin";

    public static final String CLIENT_TYPE_AGENT = "agent";
    public static final String CLIENT_TYPE_CALLER = "caller";

    public static final String KEY_CHAT_RECORD = "acd:chat_record";

    public static final String REMOVE_QUEUE_TYPE_ACCEPT = "acceptcall";
    public static final String REMOVE_QUEUE_TYPE_HANGUP = "hangupcall";
    public static final String REMOVE_QUEUE_TYPE_RECALL = "recall";

    /** 客服服务时长*/
    public static final String KEY_AGENT_SERVICE_TIME = "acd:agent:service:time";

    /**
     * 正在通话中的通话记录
     */
    public static final String KEY_CALL_LINE="acd:call:line";

//    /** 是否删除 0-正常 1-删除*/
//    public static final String FLAG_ENABLE = "0";
//    public static final String FLAG_UNENABLE = "1";
}
