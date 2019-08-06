package com.grgbanking.grgacd.dto;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.grgbanking.grgacd.model.AcdEvaluationScore;
import lombok.Data;

import java.util.Date;

/**
 * @author tjshan
 * @since 2019/6/26 17:00
 */
@Data
public class EvaluateDto {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 呼叫ID 评价所对应的通话记录ID
     */
    private String callId;

    private Integer resultType;
    /**
     * 问题解决情况
     */
    private String result;
    /**
     * 评分等级表ID
     */
    private Integer scoreId;
    /**
     * 评价说明
     */
    private String desc;
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

  private String callerName;

  private String agentName;

  private Date startTime;

  private Date endTime;

    /**
     * 业务场景
     */
  private String scene;

    /**
     * 评论文本
     */
  private String scoreText;

    /**
     * 评论得分
     */
  private Integer scoreValue;

}
