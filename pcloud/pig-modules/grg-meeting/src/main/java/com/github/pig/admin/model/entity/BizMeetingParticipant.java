package com.github.pig.admin.model.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.github.pig.common.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author bywei
 * @since 2018-06-15
 */
@TableName("biz_meeting_participant")
@Data
public class BizMeetingParticipant extends BaseEntity<BizMeetingParticipant> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（token）
     */
    private String id;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 0:静音； 1非静音
     */
    @TableField("mute_mic")
    private Integer muteMic;

    /**
     * 0:打开视频； 1:禁用视频
     */
    @TableField("mute_video")
    private Integer muteVideo;

    /**
     * 0:未共享桌面； 1:正在共享桌面
     */
    @TableField("share_desktop")
    private Integer shareDesktop;
    /**
     * 会议ID
     */
    @TableField("meeting_mid")
    private String meetingMid;
    /**
     * 会议号
     */
    @TableField("meeting_id")
    private String meetingId;
    /**
     * 心跳更新时间
     */
    @TableField("latest_hbtime")
    private Date latestHbTime;
    /**
     * 加入时间
     */
    @TableField("joined_time")
    private Date joinedTime;
    /**
     * 状态
     */
    @TableField("status")
    private String status;
    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * 主席人ID
     */
    @TableField("compere_id")
    private String compereId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
