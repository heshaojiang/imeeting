package com.grgbanking.grgacd.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.github.pig.common.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 通话记录表
 * </p>
 *
 * @author tjshan
 * @since 2019-05-05
 */

@Data
@TableName("acd_calls")
public class AcdCalls extends BaseEntity<AcdCalls> {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 主键 UUID
     */
    @TableId(value = "call_id", type = IdType.UUID)
    private String callId;
    /**
     * 客户ID
     */
    @TableField("caller_id")
    private Integer callerId;

    /**
     * 可能是游客，不存在数据库中
     */
    @TableField("caller_name")
    private String callerName;

    /**
     * 呼叫者 clientId
     */
    @TableField("caller_clientid")
    private String callerClientId;
    /**
     * 坐席 clientId
     */
    @TableField("agent_clientid")
    private String agentClientId;
    /**
     * 坐席所在队列
     */
    @TableField("queue_id")
    private String queueId;
    /**
     * 服务坐席
     */
    @TableField("agent_id")
    private Integer agentId;
    /**
     * 通话状态：
     * LINE    呼叫中
     * RING    响铃中
     * CONNECT 已接通
     * HANGUP  正常挂断
     * TIMEOUT 呼叫超时
     */
    @TableField("call_status")
    private String callStatus;
    /**
     * 客户发起呼叫时间
     */
    @TableField("makecall_time")
    private Date makecallTime;
    /**
     * 找到可服务坐席时间
     */
    @TableField("select_time")
    private Date selectTime;
    /**
     * 坐席接听时间
     */
    @TableField("answer_time")
    private Date answerTime;
    /**
     * 坐席或客户挂断通话时间
     */
    @TableField("hangup_time")
    private Date hangupTime;
    /**
     * 录屏文件上传状态
     */
    @TableField("recorder_video_status")
    private Integer recorderVideoStatus;
    /**
     * 录屏文件路径
     */
    @TableField("recorder_video_filepath")
    private String recorderVideoFilepath;
    /**
     * 录音文件上传状态
     */
    @TableField("recorder_audio_status")
    private Integer recorderAudioStatus;
    /**
     * 录音文件路径
     */
    @TableField("recorder_audio_filepath")
    private String recorderAudioFilepath;
    /**
     * 截图文件上传路径
     */
    @TableField("recorder_preview_filepath")
    private String recorderPreviewFilepath;
    /**
     * 文字聊天记录
     */
    @TableField("recorder_chat")
    private String recorderChat;
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
     * 删除时间
     */
    @TableField("del_time")
    private Date delTime;

    @TableField(exist = false)
    private SysUser agent;

    @TableField(exist = false)
    private AcdQueue queue;


    @Override
    protected Serializable pkVal() {
        return this.callId;
    }

    @Override
    public String toString() {
        return "AcdCalls{" +
                ", callId=" + callId +
                ", callerId=" + callerId +
                ", callerName=" + callerName +
                ", queueId=" + queueId +
                ", agentId=" + agentId +
                ", callStatus=" + callStatus +
                ", makecallTime=" + makecallTime +
                ", selectTime=" + selectTime +
                ", answerTime=" + answerTime +
                ", hangupTime=" + hangupTime +
                ", recorderVideoStatus=" + recorderVideoStatus +
                ", recorderVideoFilepath=" + recorderVideoFilepath +
                ", recorderAudioStatus=" + recorderAudioStatus +
                ", recorderAudioFilepath=" + recorderAudioFilepath +
                ", recorderPreviewFilepath=" + recorderPreviewFilepath +
                ", recorderChat=" + recorderChat +
                ", createdTime=" + createdTime +
                ", updateTime=" + updateTime +
                ", delTime=" + delTime +
                "}";
    }
}
