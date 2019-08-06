package com.grgbanking.grgacd.service.impl;

import com.grgbanking.grgacd.common.Agent;
import com.grgbanking.grgacd.common.AgentStatus;
import com.grgbanking.grgacd.model.AcdStatusHistory;
import com.grgbanking.grgacd.mapper.AgentStatusHistoryMapper;
import com.grgbanking.grgacd.service.AgentService;
import com.grgbanking.grgacd.service.AgentStatusHistoryService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 坐席状态变迁表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-05
 */
@Service
public class AgentStatusHistoryServiceImpl extends ServiceImpl<AgentStatusHistoryMapper, AcdStatusHistory> implements AgentStatusHistoryService {

    @Autowired
    AgentStatusHistoryMapper statusHistoryMapper;

    @Autowired
    AgentService agentService;


    @Override
    public Boolean insertStatusHistory(Agent agent,String callId) {
        AcdStatusHistory statusHistory=new AcdStatusHistory();
        statusHistory.setAgentId(Integer.parseInt(agent.getAgentId()));
        //FIXME 需要修改为保存callId 不再保存queueId
        statusHistory.setQueueId(callId);
        statusHistory.setStatus(agent.getStatus());
        statusHistory.setLastStatus(agent.getStatusLast());
        statusHistory.setStatusTime(agent.getStatusTime());
        statusHistory.setLastStatusTime(agent.getStatusTimeLast());
        Integer hisCount = statusHistoryMapper.insert(statusHistory);
        return hisCount==1;
    }

    @Override
    public Boolean insertStatusHistory(Agent agent, String statusNew, String queueId) {
        AcdStatusHistory acdStatusHistory=new AcdStatusHistory();
        acdStatusHistory.setAgentId(Integer.parseInt(agent.getAgentId()));
        acdStatusHistory.setQueueId(queueId);
        acdStatusHistory.setStatus(statusNew);
        acdStatusHistory.setStatusTime(new Date());

        acdStatusHistory.setLastStatus(agent.getStatus());
        acdStatusHistory.setLastStatusTime(agent.getStatusTime());

        Integer count = statusHistoryMapper.insert(acdStatusHistory);
        return count==1;
    }

    @Override
    public Boolean insertStatusHistory(String agentId, String queueId, AgentStatus statusNew) {
        Agent agent=agentService.cacheGet(agentId);
        return insertStatusHistory(agent,statusNew.name(),queueId);
    }
}
