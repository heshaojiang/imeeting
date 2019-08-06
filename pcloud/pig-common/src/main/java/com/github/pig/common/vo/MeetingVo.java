package com.github.pig.common.vo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 会议预约、会议召开后会生成会议信息，会议信息记录会议的基本信息
 * </p>
 *
 * @author bywei
 * @since 2018-05-10
 */
@Data
public class MeetingVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    /**
     * 会议ID用会议室ID或者用户ID，如：180201001
     */
    private String meetingId;

    /**
     * 会议唯一标识
     */
    private String meetingMid;
    private String customerId;
    /**
     * 0 - 正规会议（再会议室召开）； 1 - 自由会议（用电脑随时发起）
     */
    private String meetingType;
    private String meetingName;
    /**
     * 主持人
     */
    private String compere;
    /**
     * 主持人ID
     */
    private String compereId;

    private String participants;

    /**
     * 最多通话方数量
     */
    private Integer numVideo;
    /**
     * 最多接入方
     */
    private Integer numJoin;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 会议计划开始时间
     */
    private Date startTime;
    /**
     * 会议实际开始时间
     */
    private Date realStartTime;
    /**
     * 会议计划结束时间
     */
    private Date endTime;
    /**
     * 会议实际结束时间
     */
    private Date realEndTime;

    /**
     * 0 - 进行中； 1 - 预约； 2 - 完成
     */
    private String status;
    /**
     * 0-正常,1-删除
     */
    private String delFlag;
    /**
     * 会议密码
     */
    private String meetingPwd;
    /**
     * 主持人密码
     */
    private String comperePwd;

    /**
     * 视频轮询间隔
     */
    private Integer videoLoopTime;

    /**
     * 视频轮询类型：0-传输音视频；1-只传输视频
     */
    private Integer videoLoopType;

    /**
     * 默认分屏类型(为空时则不指定)；one - 一分屏 ; two - 二分屏；twoWithHover - 二分屏-画中画 ; three - 三分屏 ; threeWithHover - 三分屏-画中画 ; four - 四分屏 ; six - 六分屏
     */
    private String splitType;

    /**
     * 参会人列表
     */
    private List<MeetingParticipant> ParticipantList = new ArrayList<>();
    /**
     * 计划参会人列表
     */
    private List<MeetingParticipantPlan> ParticipantPlanList = new ArrayList<>();
    /**
     * 会议扩展字段
     */
    private String externConfigs;
    /**
     * 主持人分辨率
     */
    private String compereDpi;
    /**
     * 主席是否开启视频
     */
    private boolean compereVideoEnable;
    /**
     * 主席是否开启音频
     */
    private boolean compereAudioEnable;
    /**
     * 参会人是否开启视频
     */
    private boolean participantVideoEnable;
    /**
     * 参会人是否开启音频
     */
    private boolean participantAudioEnable;
    /**
     * 参会人分辨率
     */
    private String participantDpi;
    /**
     * 显示模式
     */
    private String showMode;
}
