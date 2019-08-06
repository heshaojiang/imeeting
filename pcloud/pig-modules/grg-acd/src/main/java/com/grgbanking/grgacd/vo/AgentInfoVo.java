package com.grgbanking.grgacd.vo;

import lombok.Data;

@Data
public class AgentInfoVo {
    private Integer agentId;

//    private String statusTime;
//
//    private String lastStatusTime;

    private String userName;

    private Integer serviceCount;

    private Integer totalServiceTime;

    private Integer avgResponseTime;

    private Integer avgServiceTime;
}
