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
 * 通话评价表，记录用户对当前通话的评价等级与内容
 * </p>
 *
 * @author tjshan
 * @since 2019-05-24
 */
@Data
@TableName("acd_evaluation")
public class AcdEvaluation extends BaseEntity<AcdEvaluation> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 呼叫ID 评价所对应的通话记录ID
     */
    @TableField("call_id")
    private String callId;

    @TableField("result_type")
    private Integer resultType;
    /**
     * 问题解决情况
     */
    private String result;
    /**
     * 评分等级表ID
     */
    @TableField("score_id")
    private Integer scoreId;
    /**
     * 评价说明
     */
	@TableField("description")
    private String desc;
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
    private AcdCalls calls;

    @TableField(exist = false)
    private AcdEvaluationScore score;



    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "AcdEvaluation{" +
        ", id=" + id +
        ", callId=" + callId +
        ", result=" + result +
        ", scoreId=" + scoreId +
        ", desc=" + desc +
        ", createdTime=" + createdTime +
        ", updateTime=" + updateTime +
        ", delTime=" + delTime +
        "}";
    }
}
