package com.github.pig.admin.model.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
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
 * 用户表
 * </p>
 *
 * @author lengleng
 * @since 2017-10-29
 */
@TableName("sys_user")
@Data
public class SysUser extends BaseEntity<SysUser> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;
    /**
     * @NOTE 账号
     */
    @TableField("username")
    private String username;
    /**
     * @NOTE 姓名
     */
    @TableField("nickname")
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
     * 最近登录
     */
    @TableField("login_time")
    private Date loginTime;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * 删除时间
     */
    @TableField("del_time")
    private String delTime;

    /**
     * 简介
     */
    private String introduction;
    /**
     * 头像
     */
    private String avatar;

    /**
     * 部门ID
     */
    @TableField("dept_id")
    private Integer deptId;

    /**
     * 父ID
     */
    @TableField("parent_id")
    private Integer parentId;
    /**
     * 商户ID
     */
    @TableField("customer_id")
    private Integer customerId;

    @Override
    protected Serializable pkVal() {
        return this.userId;
    }
}
