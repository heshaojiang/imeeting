package com.github.pig.admin.model.entity;

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
 * 客户注册时会关联注册会议室，会议室信息表记录其基本属性
 * </p>
 *
 * @author fmsheng
 * @since 2018-07-11
 */
@TableName("biz_room")
@Data
public class BizRoom extends BaseEntity<BizRoom> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "room_id", type = IdType.AUTO)
    private Integer roomId;
    /**
     * 会议室ID，如：1802010001
     */
    @TableField("room_no")
    private String roomNo;

    @TableField("room_name")
    private String roomName;
    /**
     * 0 - 交互平板； 1 - 一体机； 2 - 电脑
     */
    @TableField("terminal_type")
    private String terminalType;

    /**
     * 客户Id
     */
    @TableField("customer_id")
    private Integer customerId;
    /**
     * 0 - 空闲； 1 - 预约； 2 - 占用
     */
    private String status;
    @TableField("create_time")

    private Date createTime;
    @TableField("update_time")
    private Date updateTime;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Integer userId;

    @Override
    protected Serializable pkVal() {
        return this.roomId;
    }

}
