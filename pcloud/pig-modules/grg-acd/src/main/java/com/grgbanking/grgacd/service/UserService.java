package com.grgbanking.grgacd.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.RespCode;
import com.grgbanking.grgacd.model.SysUser;

import java.util.List;
import java.util.Map;

/**
 * @author tjshan
 * @since 2019/5/17 14:12
 */
public interface UserService extends IService<SysUser> {
    /**
     * 获取agent列表
     *
     * @param page 分页
     * @return Page
     */
    Page getAgentPage(Query<SysUser> page);

    List<SysUser> getAgentList(Map<String,Object> params);

    SysUser getByName(String username);

    /**
     * 根据id获取agent
     * @param userId
     * @return
     */
    SysUser getById(Integer userId);

    /**
     * 添加客服
     *
     * @param userVo
     * @return
     */
    RespCode addAgent(SysUser userVo);

    /**
     * 更新客服
     * @param userVo
     * @param username
     * @return
     */
    Boolean updateAgent(SysUser userVo, String username);

    Boolean deleteAgent(Integer agentId);

    Boolean batchDeleteUser(String[] agentIds);

    List<String> checkAgentInUse(String[] agentIds);

    /**
     * 检查客服数据是否 正确
     *
     * @param userVo
     * @param sysUserOri
     * @return
     */
    RespCode checkAgent(SysUser userVo, SysUser sysUserOri);

}
