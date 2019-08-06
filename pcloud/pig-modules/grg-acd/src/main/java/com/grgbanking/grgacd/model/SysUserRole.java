package com.grgbanking.grgacd.model;



import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 用户角色表
 * </p>
 *
 * @author tjshan
 * @since 2019-5-17
 */
@Data
@TableName("sys_user_role")
public class SysUserRole extends Model<SysUserRole> {

    private static final long serialVersionUID = 1L;
    private static final Integer ROLE_ID=6;

    /**
     * 用户ID
     */

    @TableId(type = IdType.INPUT,value = "user_id")
	private Integer userId;
    /**
     * 角色ID
     */
	@TableId(type = IdType.INPUT,value = "role_id")
	private Integer roleId;

	@Override
	protected Serializable pkVal() {
		return this.userId;
	}

    public SysUserRole() {
	    this.roleId=ROLE_ID;
    }

    @Override
	public String toString() {
		return "SysUserRole{" +
			", userId=" + userId +
			", roleId=" + roleId +
			"}";
	}
}
