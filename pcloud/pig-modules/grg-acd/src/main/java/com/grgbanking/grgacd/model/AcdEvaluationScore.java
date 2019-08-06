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
 * 评价等级表。用于可供用户选择的评价等级
 * </p>
 *
 * @author tjshan
 * @since 2019-05-24
 */
@Data
@TableName("acd_evaluation_score")
public class AcdEvaluationScore extends BaseEntity<AcdEvaluationScore> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 评价等级名称
     */
    private String name;
    /**
     * 评价等级描述
     */
	@TableField("description")
    private String desc;
    /**
     * 评分类型
     */
    private String type;
    /**
     * 评分分值
     */
    @TableField("score_value")
    private Integer scoreValue;
    /**
     * 评分等级排序序号
     */
    @TableField("score_index")
    private Integer scoreIndex;
    /**
     * 是否有效
     */
    private Integer enable;
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

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "AcdEvaluationScore{" +
        ", id=" + id +
        ", name=" + name +
        ", desc=" + desc +
        ", type=" + type +
        ", scoreValue=" + scoreValue +
        ", scoreIndex=" + scoreIndex +
        ", enable=" + enable +
        ", createdTime=" + createdTime +
        ", updateTime=" + updateTime +
        ", delTime=" + delTime +
        "}";
    }
}
