package com.grgbanking.grgacd.service;

import com.grgbanking.grgacd.common.Agent;
import com.grgbanking.grgacd.common.AgentStatus;
import com.grgbanking.grgacd.model.AcdStatusHistory;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 坐席状态变迁表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-05
 */
public interface AgentStatusHistoryService extends IService<AcdStatusHistory> {

    /**
     * 插入和改变agent 状态
     * 确保 status 和last_status 已更新
     * @param agent
     * @param queueId
     * @return
     */
    Boolean insertStatusHistory(Agent agent,String queueId);

    /**
     * 插入和改变agent 状态
     * 确保 agent 中的status 为原始状态
     * @param agent
     * @param statusNew
     * @param queueId
     * @return
     */
    Boolean insertStatusHistory(Agent agent,String statusNew,String queueId);

    Boolean insertStatusHistory(String agentId, String queueId, AgentStatus statusNew);


}
