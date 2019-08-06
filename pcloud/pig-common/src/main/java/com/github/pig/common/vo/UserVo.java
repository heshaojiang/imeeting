package com.github.pig.common.vo;

import lombok.Data;

import java.beans.Customizer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lengleng
 * @date 2017/10/29
 */
@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = 1L;

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
    /**
     * 密码
     */
    private String password;
    /**
     * 随机盐
     */
    private String salt;
    /**
     * 手机
     */
    private String call;
    /**
     * 邮箱
     */
    private String email;
    /**
     *  最近登录
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
     * 0-正常，1-删除
     */
    private String delFlag;
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
    private Integer deptId;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 角色列表
     */
    private List<SysRole> roleList = new ArrayList<>();
    /**
     * 客户列表
     */
    private List<BizCustomer> customerList = new ArrayList<>();
    /**
     * 会议室列表
     */
    private List<BizRoom> roomList = new ArrayList<>();
    /**
     * 父ID
     */
    private Integer parentId;
    /**
     * 商户ID
     */
    private Integer customerId;

    /**
     * First Role
     */
    private String role;
    /**
     * 屏蔽密码
     */
    public UserVo maskPassword(){
        password = "";
        return this;
    }
}
