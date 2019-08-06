package com.grgbanking.grgacd.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.grgbanking.grgacd.model.SysUser;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author tjshan
 * @since 2019/5/28 10:36
 */
@Data
public class QueueDto {

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
    private Integer maxNum;
    /**
     * 队列状态
     */
    private String status;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

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

    private List<Integer> agentList;

    private List<SysUser> agentLists;

    /**
     * 总的客服人数
     */
    private Integer totalAgentCount;

    /**
     * 在线客服人数
     */
    private Integer onlineAgentCount;
}
