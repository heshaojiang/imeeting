package com.github.pig.admin.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.github.pig.admin.model.dto.UserDto;
import com.github.pig.admin.model.dto.UserInfo;
import com.github.pig.admin.model.entity.SysUser;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.R;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.vo.UserVo;

import java.util.List;

/**
 * @author lengleng
 * @date 2017/10/31
 */
public interface SysUserService extends IService<SysUser> {
    /**
     * 根据用户名查询用户角色信息
     *
     * @param username 用户名
     * @return userVo
     */
    UserVo findUserByUsername(String username);

    /**
     * 分页查询用户信息（含有角色信息）
     *
     * @param query 查询条件
     * @return
     */
    Page selectWithRolePage(Query query);

    /**
     * 分页查询用户信息（admin）
     *
     * @param query 查询条件
     * @return
     */
    Page selectUserVoPageForAdmin(Query query);

    /**
     * 查询用户信息
     *
     * @param userVo 角色名
     * @return userInfo
     */
    UserInfo findUserInfo(UserVo userVo);

    /**
     * 保存验证码
     *  @param randomStr 随机串
     * @param imageCode 验证码*/
    void saveImageCode(String randomStr, String imageCode);

    /**
     * 删除用户
     * @param sysUser 用户
     * @return boolean
     */
    Boolean deleteUserById(SysUser sysUser);

    /**
     * 更新当前用户基本信息
     * @param userDto 用户信息
     * @param username 用户名
     * @return Boolean
     */
    R<Boolean> updateUserInfo(UserDto userDto, String username);

    /**
     * 个人密码重置
     *
     * @return Boolean
     */
    Boolean passwordReset(String username);

    /**
     * 更新指定用户信息
     * @param userDto 用户信息
     * @param username 用户信息
     * @return
     */
    Boolean updateUser(UserDto userDto, String username);

    /**
     * 通过手机号查询用户信息
     * @param mobile 手机号
     * @return 用户信息
     */
    UserVo findUserByMobile(String mobile);

    /**
     * 发送验证码
     * @param mobile 手机号
     * @return R
     */
    R<Boolean> sendSmsCode(String mobile);

    /**
     * 通过openId查询用户
     * @param openId openId
     * @return 用户信息
     */
    UserVo findUserByOpenId(String openId);

    /**
     * 通过ID查询用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    UserVo selectUserVoById(Integer id);

    /**
     * @author fmsheng
     * @param userIds
     * @description  批量删除用户
     * @date 2018/10/23 11:04
     */
    Boolean batchDeleteUser(String[] userIds);

    List<UserVo> selectUserVoByCustomerId(Integer customerId);

    UserVo selectUserVoAndRoomResult(String username);

    SysUser getCurSysUser();

    /**
     * @author fmsheng
     * @param
     * @description  通过用户查询到客户ID
     * @date 2018/12/27 14:07
     */
    Integer  getCustomerIdByUserId() ;

    /**
     * @author wjqiu
     * @date 2019-03-13
     * 获取当前用户的UserId
     */
    Integer getUserId();

    /**
     * @author wjqiu
     * @date 2019-01-16
     * description: 检查是否存在用户，同时检查用户名和手机号。都不能有重复
     */
    RespCode checkUser(UserDto userDtoNew, SysUser sysUserOri);
    RespCode addUser(UserDto userDto);

    Boolean checkNameExist(String name);
    Boolean checkNickaameExist(String name);
    Boolean checkPhonenumExist(String phonenum);
}
