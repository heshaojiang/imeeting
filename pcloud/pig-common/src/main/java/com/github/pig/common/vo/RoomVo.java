package com.github.pig.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author fmsheng
 * @description
 * @date 2018/11/10 11:51
 * @modified by
 */
@Data
public class RoomVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer roomId;
    /**
     * 会议室ID，如：1802010001
     */
    private String roomNo;

    private String roomName;
    /**
     * 0 - 交互平板； 1 - 一体机； 2 - 电脑
     */
    private String terminalType;
    /**
     * 通话方数量
     */
    private Integer numVideo;
    /**
     * 接入方数量
     */
    private Integer numJoin;
    /**
     * 0 - 空闲； 1 - 预约； 2 - 占用
     */
    private String status;

    private Date createTime;

    private Date updateTime;
    /**
     * 0-正常,1-删除
     */
    private String delFlag;
    /**
     * 客户
     */
    private List<BizCustomer> customerList = new ArrayList<>();
    /**
     * 用户
     */
    private List<UserVo> userList = new ArrayList<>();
}
