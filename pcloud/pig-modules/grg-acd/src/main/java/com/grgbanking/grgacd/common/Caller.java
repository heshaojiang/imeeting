package com.grgbanking.grgacd.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wjqiu
 * @date 2019-04-21
 */
@Data
public class Caller implements Serializable {
    private String callerId;
    private String callerName;
    private String clientId;
    private String callId;
    private String queueId;
    private String platformType;
    private String status;
    private Date callTime;
    private Date ringingTime;
    private Date loginTime;
    private Object metaData;
    public Caller(){
        this.loginTime = new Date();
    }
    public void reinit(){
        callId = null;
        ringingTime = null;
        callTime = null;
        metaData = null;
        status = CallerStatus.LOGIN.name();
    }

}
