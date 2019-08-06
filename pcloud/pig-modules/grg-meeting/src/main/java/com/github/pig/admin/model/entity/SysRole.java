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
 * <p>
 * </p>
 *
 * @author lengleng
 * @since 2017-10-29
 */
@TableName("sys_role")
@Data
public class SysRole extends BaseEntity<SysRole> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "role_id", type = IdType.AUTO)
    private Integer roleId;

    @TableField("role_name")
    private String roleName;

    @TableField("role_code")
    private String roleCode;

    @TableField("role_desc")
    private String roleDesc;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
    /**
     * 分级
     */
    @TableField("role_level")
    private String roleLevel;
    /**
     * 删除时间
     */
    @TableField("del_time")
    private String delTime;

    @Override
    protected Serializable pkVal() {
        return this.roleId;
    }
}
