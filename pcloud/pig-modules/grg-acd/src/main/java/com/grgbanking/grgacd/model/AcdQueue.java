package com.grgbanking.grgacd.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.pig.common.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 坐席队列
 * </p>
 *
 * @author tjshan
 * @since 2019-05-05
 */
@Data
@TableName("acd_queue")
public class AcdQueue extends BaseEntity<AcdQueue> {

    private static final long serialVersionUID = 1L;
    private Integer id;
    /**
     * 队列id
     */
    @TableId(value = "queue_id",type = IdType.INPUT)
    private String queueId;
    /**
     * 队列名称
     */
    private String queueName;
    /**
     * 队列描述
     */
    private String description;

    /**
     * 排队策略
     longest-idle-agent (闲时优先)
     agent-with-least-talk-time (最少服务时间)
     */
    private String strategy;

    /**
     * 最大坐席接入数
     */
    @TableField("max_num")
    private Integer maxNum;
    /**
     * 队列状态
     */
    private String status;
    /**
     * 创建时间
     */
    @TableField("created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

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
    private List<Integer> agentList;

    @TableField(exist = false)
    private List<SysUser> agentLists;

    @Override
    protected Serializable pkVal() {
        return this.queueId;
    }

    @Override
    public String toString() {
        return "AcdQueue{" +
                ", id=" + id +
                ", queueId=" + queueId +
                ", queueName=" + queueName +
                ", strategy=" + strategy +
                ", maxNum=" + maxNum +
                ", status=" + status +
                ", description=" + description +
                ", createdTime=" + createTime +
                ", updateTime=" + updateTime +
                ", delTime=" + delTime +
                "}";
    }
}
