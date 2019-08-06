package com.github.pig.admin.common.meeting;

public class ProtocolElements {
    // ---------------------------- SSE EVENTS -----------------------
    public static final String SSE_EVENT_FROM_PARAM = "from";
    public static final String SSE_EVENT_TO_PARAM = "to";
    public static final String SSE_EVENT_STAT_PARAM = "stat";

    public static final String SSE_MUTE_REQUEST_METHOD = "requestMute";
    public static final String SSE_UNMUTE_REQUEST_METHOD = "requestUnmute";
    public static final String SSE_MUTE_REPLY_METHOD = "replyMute";
    public static final String SSE_VNC_REQUEST_METHOD = "requestVNC";
    public static final String SSE_VNC_REPLY_METHOD = "replyVNC";
    public static final String SSE_SPEAK_REQUEST_METHOD = "requestSpeak";
    public static final String SSE_SPEAK_APPROVE_METHOD = "replySpeak";
    public static final String SSE_HOST_APPLY_METHOD = "applyHost";
    public static final String SSE_HOST_REQUEST_PWD_METHOD = "requestHostPwd";
    public static final String SSE_HOST_APPROVE_METHOD = "approveHost";
    public static final String SSE_HOST_GIVEUP_METHOD = "giveUpHost";
    public static final String SSE_HOST_ASSIGN_METHOD = "assignHost";
    public static final String SSE_HOST_INVALIDPWD_METHOD = "InvalidHostPwd";

    //
    public static final String SSE_HOST_CHANGES_METHOD = "hostChanges";
    public static final String SSE_MEETING_REJOIN_METHOD = "rejoinMeeting";

    public static final String SSE_CLOSEVIDEO_REQUEST_METHOD = "closeVideo";
    public static final String SSE_OPENVIDEO_REQUEST_METHOD = "openVideo";
}
