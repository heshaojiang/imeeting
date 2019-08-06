package com.grgbanking.grgacd.dto;

import lombok.Data;

/**
 * 通话报表 按天为单位
 * @author tjshan
 * @since  2019/5/6 14:12
 */
@Data
public class CallDayDto {

    /**
     * 记录时间
     * YYYY-mm-dd格式
     */
    private String acdTime;

    /**
     * 平均间隔时间
     */
    private String durationTime;

    /**
     * 队列ID
     */
    private Integer queueId;

    /**
     * 通话状态
     */
    private Integer callStatus;

    /**
     * 某种状态的数量
     */
    private Integer statusCount;
}
