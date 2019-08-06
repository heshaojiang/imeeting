package com.grgbanking.grgacd.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.util.exception.GrgException;
import com.grgbanking.grgacd.common.Agent;
import com.grgbanking.grgacd.common.QueueManager;
import com.grgbanking.grgacd.dto.QueueStrategyVo;
import com.grgbanking.grgacd.model.AcdQueue;
import com.baomidou.mybatisplus.service.IService;
import com.grgbanking.grgacd.model.SysUser;

import java.util.List;

/**
 * <p>
 * 坐席队列 服务类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-05
 */
public interface AcdQueueService extends IService<AcdQueue> {
    /**
     * 初始化队列
     */
    void initQueue();

    /**
     * 添加队列成员
     * @param queueId 队列id
     * @param agentIds 成员id集合
     * @return Boolean
     */
    Boolean addMembers(String queueId,Integer capacity,List<Integer> agentIds);

    /**
     * 删除队列成员
     * @param queueId 队列id
     * @param agentIds  成员id集合
     * @param all 是否全部删除
     * @return Boolean
     */
    Boolean deleteMembers(String queueId,List<String> agentIds,boolean all);

    /**
     * 添加队列
     * @param acdQueue 队列
     * @return  Boolean
     */
    Boolean addQueue(AcdQueue acdQueue);

    /**
     * 更新队列
     * @param acdQueue 队列
     * @return Boolean
     */
    Boolean updateQueue(AcdQueue acdQueue);

    /**
     * 获取一个队列详情
     * @param queueId 队列id
     * @return AcdQueue
     */
    AcdQueue getOneQueue(String queueId);

    /**
     * 获取队列列表
     * @param query 查询条件
     * @return Page
     */
    Page getQueuePage(Query<Object> query);

    /**
     * 获取队列成员列表
     * @param queueId 队列id
     * @param objectQuery 查询条件
     * @return Page
     */
    Page getQueueMembers(String queueId, Query<SysUser> objectQuery);

    /**
     * 获取队列所有的在线成员
     * @param queueId
     * @param query
     * @return
     */
    Page getQueueMembersOnline(String queueId, Query<Agent> query);


    /**
     * 删除队列
     * @param queueIds 队列id集合
     * @return RespCode
     */
    RespCode deleteQueue(String[] queueIds);

    /**
     * 检查对列中是有正在使用的agent
     * @param queueId 队列id
     * @return Boolean
     */
    Boolean checkQueueInUse(String queueId);

    /**
     * 添加一个对列
     * @param acdQueue  队列对象
     * @return QueueManager
     */
    QueueManager addQueueManager(AcdQueue acdQueue);

    /**
     * 获取一个对列
     * @param queueID  队列id
     * @return QueueManager
     */
    QueueManager getQueue(String queueID);

    /**
     * 获取agent所在的所有队列
     * @param agentId
     * @return
     */
    List<AcdQueue> getQueueByAgentId(Integer agentId);

    /**
     * 获取队列策略列表
     * @return
     */
    List<QueueStrategyVo> getQueueStrategyList();

    /**
     * 关闭一个通话
     * @param callId
     * @param reason
     */
    void hangupCall(String callId, String clientId, String clientType, String reason) throws GrgException;
    /**
     * 把Caller重新进行排队
     */
    void setCallerReCall(String callId) throws GrgException;
}
