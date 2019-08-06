package com.grgbanking.grgacd.dto;

import lombok.Data;

import java.util.List;

/**
 * @author yejinwen
 */
@Data
public class CountSessionVo{
    /**
     * 统计接听和未接听会话数
     */
    private Integer answerTimeSum;

    /**
     * 计算报表横坐标
     */
    private String intervalTime;

//    /**
//     * 存储数据库返回记录
//     */
//    private List records;


}
