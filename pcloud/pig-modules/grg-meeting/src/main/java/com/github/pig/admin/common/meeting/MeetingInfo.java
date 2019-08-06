package com.github.pig.admin.common.meeting;

import com.github.pig.admin.model.entity.BizMeeting;
import com.github.pig.common.vo.MeetingParticipant;
import com.github.pig.common.vo.MeetingVo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

@Data
public class MeetingInfo {
    //会议session
    private MeetingSession meetingSession;
    //用户名
    private String userName;
    //nickname
    private String nickname;
    //当前用户是否为主持（从数据库通过用户名判断得出）
    private boolean isHost;
    //是否为匿名入会
    private boolean isAnonymous;

    //会议信息
    MeetingVo meetingVo;
    public MeetingInfo(MeetingVo meeting){
        meetingVo = meeting;
    }

    public MeetingInfo(BizMeeting meeting){
        if(meeting!=null){
            BeanUtils.copyProperties(meeting,meetingVo);
        }
    }

    public MeetingInfo(MeetingVo meeting, MeetingSession session){
        this.meetingVo = meeting;
        this.meetingSession=session;

    }

}
