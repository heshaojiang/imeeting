package com.grgbanking.grgacd.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author wjqiu
 * @date 2019-04-21
 */
@Data
public class Agent implements Serializable {
    private String clientId;
    private String agentId;
    private String agentName;
    private String status;
    private Date statusTime;
    private String statusLast;
    private Date statusTimeLast;
    private Date createTime;
    private String callId;
    /** 服务时长 */
    private Long serviceTime = 0L;
    private Set<String> acdQueues = new HashSet<String>();

    public Agent(){
        this.createTime = new Date();
    }
}
