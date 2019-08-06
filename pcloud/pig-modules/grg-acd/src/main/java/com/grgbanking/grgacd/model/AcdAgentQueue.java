package com.grgbanking.grgacd.model;


import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 *
 *  * <p>
 *  * 坐席队列分类
 *  一个客服可在多个对列
 *  一个队列存在多个客服
 *  * </p>
 *
 * @author  tjshan
 * @since 2019-05-06
 */
@Data
public class AcdAgentQueue extends Model<AcdAgentQueue> {

    /**
     * 客服ID
     */
    @TableField("agent_id")
    private Integer agentId;

    /**
     * 队列ID
     */
    @TableField("queue_id")
    private String queueId;

    public AcdAgentQueue() {
    }

    public AcdAgentQueue(Integer agentId, String queueId) {
        this.agentId = agentId;
        this.queueId = queueId;
    }

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
