package com.grgbanking.grgacd.dto;

import lombok.Data;

/**
 * 通话报表 按30分钟为单位
 * @author tjshan
 * @date 2019/5/7 15:27
 */
@Data
public class CallMinuteDto {

    /**
     * 记录时间
     * YYYY-mm-dd格式
     */
    private String acdTime;

    /**
     * 当天的小时 从0-23
     */
    private Integer hour;

    /**
     * 分钟
     * 0 -表示前30分钟
     * 1 -表示后30分钟
     */
    private Integer minute;

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

    /**
     * 两种状态之间的间隔时间
     */
    private Integer durationTime;
}
