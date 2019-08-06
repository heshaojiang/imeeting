package com.grgbanking.grgacd.vo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.github.pig.common.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
/**
 * @author wjqiu
 * @date 2019-07-12
 * @description 用于界面显示的评价等级类
 */
@Data

public class AcdEvaluationScoreVo {

    private Integer id;
    /**
     * 评价等级名称
     */
    private String name;
    /**
     * 评价等级描述
     */
    private String desc;
    /**
     * 评分类型
     */
    private String type;
    /**
     * 评分分值
     */
    private Integer scoreValue;
    /**
     * 评分等级排序序号
     */
    private Integer scoreIndex;

}
