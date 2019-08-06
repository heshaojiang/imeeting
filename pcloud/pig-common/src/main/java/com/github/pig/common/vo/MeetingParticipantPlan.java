package com.github.pig.common.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: wjqiu
 * @date: 2019-01-14
 * @description: 计划参会人员
 */
@Data
public class MeetingParticipantPlan implements Serializable {

    private Integer id;

    private Integer user_id;

    private String nickname;
    /* 会议唯一标识 */
    private String meeting_mid;
    /* 入会模式 1:通话方(可发布视频);2:接入方(只能订阅视频) */
    private String join_type;

}
