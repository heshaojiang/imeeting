package com.github.pig.admin.model.entity;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.github.pig.common.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 会议预约、会议召开后会生成会议信息，会议信息记录会议的基本信息
 * </p>
 *
 * @author fmsheng
 * @since 2018-12-25
 */
@TableName("biz_meeting_hist")
@Data
public class BizMeetingHist extends BaseEntity<BizMeetingHist> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 会议ID用会议室ID或者用户ID，如：180201001（对应biz_room表中room_no）
     */
    @TableField("meeting_id")
    private String meetingId;
    /**
     * 唯一标识一次会议
     */
    @TableField("meeting_mid")
    private String meetingMid;
    /**
     * 客户ID
     */
    @TableField("customer_id")
    private String customerId;
    /**
     * 主持人
     */
    private String compere;
    /**
     * 主持人ID
     */
    @TableField("compere_id")
    private String compereId;
    /**
     * 管理员字段保留
     */
    private String manager;
    /**
     * 0 - 正规会议（在会议室召开）； 1 - 自由会议（用电脑随时发起) ;  2 - 培训模式；3 - 主持模式
     */
    @TableField("meeting_type")
    private String meetingType;
    /**
     * 会议名称
     */
    @TableField("meeting_name")
    private String meetingName;
    /**
     * 会议简介
     */
    @TableField("meeting_info")
    private String meetingInfo;

    /**
     * 最多通话方数量
     */
    @TableField("num_video")
    private Integer numVideo;
    /**
     * 最多接入方
     */
    @TableField("num_join")
    private Integer numJoin;
    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;
    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * 会议计划开始时间
     */
    @TableField("start_time")
    private Date startTime;
    /**
     * 会议实际开始时间
     */
    @TableField("real_start_time")
    private Date realStartTime;
    /**
     * 会议计划结束时间
     */
    @TableField("end_time")
    private Date endTime;
    /**
     * 会议实际结束时间
     */
    @TableField("real_end_time")
    private Date realEndTime;
    /**
     * 0 - 进行中； 1 - 预约； 2 - 完成
     */
    private String status;
    /**
     * 会议密码
     */
    @TableField("meeting_pwd")
    private String meetingPwd;
    /**
     * 主持人密码
     */
    @TableField("compere_pwd")
    private String comperePwd;
    /**
     * 视频轮询间隔
     */
    @TableField("video_loop_time")
    private Integer videoLoopTime;
    /**
     * 视频轮询类型：0-传输音视频；1-只传输视频
     */
    @TableField("video_loop_type")
    private Integer videoLoopType;
    /**
     * 默认分屏类型(为空时则不指定)；one - 一分屏 ; two - 二分屏；twoWithHover - 二分屏-画中画 ; three - 三分屏 ; threeWithHover - 三分屏-画中画 ; four - 四分屏 ; six - 六分屏
     */
    @TableField("split_type")
    private String splitType;
    @TableField("del_time")
    private Date delTime;

    /**
     * 会议结束类型：1-正常关闭；2-异常关闭；3-删除会议
     */
    @TableField("close_type")
    private String closeType;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
