package com.grgbanking.grgacd.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author tjshan
 * @since 2019/5/27 16:13
 */
@Data
public class CallDto {

    /**
     * 主键 UUID
     */
    private String callId;
    /**
     * 客户ID
     */
    private Integer callerId;

    /**
     * 可能是游客，不存在数据库中
     */
    private String callerName;
    /**
     * 坐席所在队列
     */
    private String queueId;

    private String queueName;
    /**
     * 坐席用户名
     */
    private String agentName;
    /**
     * 服务坐席
     */
    private Integer agentId;
    /**
     * 通话状态：
     * LINE    呼叫中
     * RING    响铃中
     * CONNECT 已接通
     * HANGUP  正常挂断
     * TIMEOUT 呼叫超时
     */
    private String callStatus;
    /**
     * 客户发起呼叫时间
     */
    private Date makecallTime;
    /**
     * 找到可服务坐席时间
     */
    private Date selectTime;
    /**
     * 坐席接听时间
     */
    private Date answerTime;
    /**
     * 坐席或客户挂断通话时间
     */
    private Date hangupTime;
    /**
     * 录屏文件上传状态
     */
    private Integer recorderVideoStatus;
    /**
     * 录屏文件路径
     */
    private String recorderVideoFilepath;
    /**
     * 录音文件上传状态
     */
    private Integer recorderAudioStatus;
    /**
     * 录音文件路径
     */
    private String recorderAudioFilepath;
    /**
     * 截图文件上传路径
     */
    private String recorderPreviewFilepath;
    /**
     * 文字聊天记录
     */
    private String recorderChat;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 删除时间
     */
    private Date delTime;
    /**
     * 0-正常,1-删除
     */
    private String delFlag;

    /**
     * 服务时长
     */
    private Long duration;

    /**
     * 通话状态
     * success:成功
     * fail:失败
     *
     */
    private String status;
}
