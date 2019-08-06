package com.grgbanking.grgacd.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 聊天记录封装类
 * @auther: hsjiang
 * @date: 2019/6/27/027
 * @version 1.0
 */
@Data
public class ChatRecord implements Serializable {
    private String id;
    /** 服务记录id */
    private String callId;
    /** 聊天人 */
    private String name;
    /** 时间 */
    private String time;
    /** 聊天内容 */
    private String content;
    /** 聊天类型 */
    private Integer type;
}
