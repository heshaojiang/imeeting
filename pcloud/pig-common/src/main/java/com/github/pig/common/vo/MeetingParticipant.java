package com.github.pig.common.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author bywei
 * @since 2018-05-12
 */
@Data
public class MeetingParticipant implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 0:静音； 1非静音
     */
    private Integer muteMic;

    /**
     * 0:打开视频； 1:禁用视频
     */
    private Integer muteVideo;

    /**
     * 0:未共享桌面； 1:正在共享桌面
     */
    private Integer shareDesktop;

    private String meetingId;

    private String nickname;

    private String meetingMid;

    private Date latestHbTime;

    private Date joinedTime;

    private String status;

    private Date updateTime;

    private String delFlag;

}
