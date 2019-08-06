package com.grgbanking.grgacd.common;
/**
 * @author wjqiu
 * @date 2019-06-23
 * @description
 */
public class HangupReason {

    public static final String FROM_CALLER = "fromCaller";
    public static final String FROM_AGNET = "fromAgent";
    public static final String FROM_SERVER = "fromServer";
    public static final String CONNECT_CLOSED = "hangupForConnectionClosed";
    public static final String TRANSPORT_ERROR = "hangupForTransportError";

    public static final String TIME_OUT_makecall = "call-time-out";
    public static final String TIME_OUT_ringing = "ringing-time-out";

}
