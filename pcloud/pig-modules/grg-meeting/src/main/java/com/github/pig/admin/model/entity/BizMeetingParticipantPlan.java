package com.github.pig.admin.model.entity;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 创建会议时设定的参会人员列表
 * </p>
 *
 * @author fmsheng
 * @since 2018-12-25
 */
@TableName("biz_meeting_participant_plan")
@Data
public class BizMeetingParticipantPlan extends Model<BizMeetingParticipantPlan> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 会议唯一标识
     */
    @TableField("meeting_mid")
    private String meeting_mid;
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Integer user_id;
    /**
     * 入会模式 1:通话方(可发布视频);2:接入方(只能订阅视频)
     */
    @TableField("join_type")
    private String join_type;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
