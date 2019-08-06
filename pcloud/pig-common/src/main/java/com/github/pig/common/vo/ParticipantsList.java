package com.github.pig.common.vo;

import java.util.ArrayList;
import java.util.List;

public class ParticipantsList {
    /**
     * 参会人列表
     */
    private List<MeetingParticipant> ParticipantList = new ArrayList<>();

    public List<MeetingParticipant> getParticipantList() {
        return ParticipantList;
    }

    public void setParticipantList(List<MeetingParticipant> participantList) {
        ParticipantList = participantList;
    }

    @Override
    public String toString() {
        return "ParticipantsList{" +
                "ParticipantList=" + ParticipantList +
                '}';
    }
}
