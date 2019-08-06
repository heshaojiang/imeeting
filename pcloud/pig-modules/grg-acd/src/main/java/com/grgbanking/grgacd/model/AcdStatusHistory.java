package com.grgbanking.grgacd.model;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 坐席状态变迁表
 * </p>
 *
 * @author tjshan
 * @since 2019-05-05
 */
@Data
@TableName("acd_status_history")
public class AcdStatusHistory extends Model<AcdStatusHistory> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 坐席ID
     */
    @TableField("agent_id")
    private Integer agentId;

    /**
     * 队列id
     */
    @TableField("queue_id")
    private String queueId;
    /**
     * 坐席状态
     */
    private String status;
    /**
     * 坐席状态变化时的时间
     */
    @TableField("status_time")
    private Date statusTime;
    /**
     * 坐席原来状态
     */
    @TableField("last_status")
    private String lastStatus;
    /**
     * 坐席原来状态变化时的时间
     */
    @TableField("last_status_time")
    private Date lastStatusTime;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    public AcdStatusHistory() {
        this.statusTime = new Date();
    }

    @Override
    public String toString() {
        return "AcdStatusHistory{" +
                ", id=" + id +
                ", agentId=" + agentId +
                ", queueId=" + queueId +
                ", status=" + status +
                ", statusTime=" + statusTime +
                ", lastStatus=" + lastStatus +
                ", lastStatusTime=" + lastStatusTime +
                "}";
    }
}
