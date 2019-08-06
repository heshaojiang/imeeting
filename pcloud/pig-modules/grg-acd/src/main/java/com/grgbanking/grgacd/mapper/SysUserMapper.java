package com.grgbanking.grgacd.mapper;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.github.pig.common.util.Query;
import com.grgbanking.grgacd.model.SysUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author lengleng
 * @since 2017-10-29
 */
@Repository
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 查询一个队列所有的客服成员
     * @param query 分页
     * @param queueId 队列id
     * @return List
     */
    List<SysUser> selectAgentByQueueId(Query query, @Param("queueId") String queueId);

    /**
     * 查询所有的客服
     * @param query 分页
     * @param params 查询条件
     * @return List
     */
    List<SysUser> selectAgentList(Query query, Map<String,Object> params);

    /**
     * 查询所有的客服 不分页
     * @param params 查询条件
     * @return List
     */
    List<SysUser> selectAgentNoPageList(Map<String,Object> params);

    /**
     * 通过username查找用户
     * @param username
     * @return
     */
    SysUser getByName(String username);

    /**
     * 通过userId查找用户
     * @param userId
     * @return
     */
    SysUser getById(Integer userId);
}