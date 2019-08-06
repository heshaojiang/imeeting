package com.github.pig.admin.common.meeting;


import lombok.Data;

import java.io.Serializable;

@Data
public class MeetingSessionCache implements Serializable {
    private String token;
    private String sessionId;
    private String websocketUrl;
    private String openviduUrl;
    private String compereId;
}