package com.grgbanking.grgacd.model;

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
 * 终端表，用于保存agent可以登录的授权的终端
 * </p>
 *
 * @author tjshan
 * @since 2019-05-24
 */
@Data
@TableName("acd_terminal")
public class AcdTerminal extends BaseEntity<AcdTerminal> {

    private static final long serialVersionUID = 1L;

    @TableField("id")
    private Integer id;
    /**
     * 设备编号
     */
    @TableId(value = "terminal_no",type = IdType.INPUT)
    private String terminalNo;
    /**
     * 终端名称
     */
    private String name;
    /**
     * 接入类型：
PC 
PAD
     */
    @TableField("access_type")
    private String accessType;
    /**
     * 接入时间
     */
    @TableField("access_time")
    private Date accessTime;
    /**
     * 设备说明
     */
	@TableField("description")
    private String description;
    /**
     * 设备型号
     */
    private String model;
    /**
     * 设备机器码
     */
    @TableField("machine_code")
    private String machineCode;
    /**
     * 是否有效
     */
    private Integer enable;
    /**
     * 设备状态 
    AVAILABLE (有效，已激活)
    NOTACTIVE (未激活)
     */
    private String status;
    /**
     * 终端的当前ip
     */
    @TableField("terminal_ip")
    private String terminalIp;
    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;
    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * 删除时间
     */
    @TableField("del_time")
    private Date delTime;

    @Override
    protected Serializable pkVal() {
        return this.terminalNo;
    }

    @Override
    public String toString() {
        return "AcdTerminal{" +
        ", id=" + id +
        ", terminalNo=" + terminalNo +
        ", name=" + name +
        ", accessType=" + accessType +
        ", accessTime=" + accessTime +
        ", description=" + description +
        ", model=" + model +
        ", machineCode=" + machineCode +
        ", enable=" + enable +
        ", status=" + status +
        ", terminalIp=" + terminalIp +
        ", createdTime=" + createdTime +
        ", updateTime=" + updateTime +
        ", delTime=" + delTime +
        "}";
    }
}
