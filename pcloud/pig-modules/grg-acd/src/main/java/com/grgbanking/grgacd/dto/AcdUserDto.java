package com.grgbanking.grgacd.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author tjshan
 * @since 2019/5/10 8:08
 */
@Data
public class AcdUserDto implements Serializable {


    /**
     * 主键ID
     */
    private Integer userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 别名
     */
    private String nickname;

    private String password;
    /**
     * 随机盐
     */
    private String salt;

    /**
     * 手机
     */
    @TableField("telephone")
    private String call;
    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 0-正常，1-删除
     */
    private String delFlag;

    /**
     * 最近登录
     */
    private Date loginTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 状态
     */
    private String status;

    private Set<String> acdQueues = new HashSet<String>();

    /**
     * 队列数量
     */
    private int queueCount;
}
