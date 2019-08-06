package com.github.pig.admin.common.meeting;

import io.openvidu.java.client.OpenViduRole;

import java.util.List;

public class Participant {
    private String userId;      // token from Openvidu(participantId)
    //是否静音
    private boolean muteMic=true;


    public Participant(String user) {
        this.userId = user;     // token
    }

    public boolean getMuteMic() {
        return muteMic;
    }

    public void setMuteMic(boolean muteMic) {
        this.muteMic = muteMic;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "userId='" + userId + '\'' +
                ", muteMic=" + muteMic +
                '}';
    }
}
