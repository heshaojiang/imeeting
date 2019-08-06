package com.grgbanking.grgacd.dto.converter.transform;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.grgbanking.grgacd.common.Agent;
import com.grgbanking.grgacd.common.AgentStatus;
import com.grgbanking.grgacd.common.SpringContextUtils;
import com.grgbanking.grgacd.mapper.AcdAgentQueueMapper;
import com.grgbanking.grgacd.mapper.SysUserMapper;
import com.grgbanking.grgacd.model.SysUser;
import com.grgbanking.grgacd.service.AgentService;

import java.util.List;

/**
 * @author tjshan
 * @since 2019/5/28 15:47
 */
public class AgentTransFrom {


    /**
     * 一个队列中所有的客服数量
     * @param queueId
     * @return
     */
    public Integer getAgentCountByQueueId(String queueId){
        AcdAgentQueueMapper bean = SpringContextUtils.getBean(AcdAgentQueueMapper.class);
        EntityWrapper wrapper=new EntityWrapper();
        wrapper.eq("queue_id",queueId);
        Integer count = bean.selectCount(wrapper);
        return count;
    }

    public Integer getQueueCountByAgentId(Integer agentId){
        AcdAgentQueueMapper bean = SpringContextUtils.getBean(AcdAgentQueueMapper.class);
        EntityWrapper wrapper=new EntityWrapper();
        wrapper.eq("agent_id",agentId);
        Integer count = bean.selectCount(wrapper);
        return count;
    }

    /**
     * 一个队列中所有在线的客服数量
     * @param queueId
     * @return
     */
    public Integer getAgentOnlineCountByQUeueId(String queueId){
        AgentService agentService = SpringContextUtils.getBean(AgentService.class);
        List<Agent> agentList = agentService.getAgentList();
        long count = agentList.stream()
                .filter(agent -> agent.getAcdQueues().contains(queueId))
                .filter(agent -> !agent.getStatus().equals(AgentStatus.OFFLINE.name()) || agent.getStatus().equals(AgentStatus.ONBREAK.name()))
                .count();
        return Math.toIntExact(count);
    }

    /**
     * 根据用户名获取agent状态
     * @param username
     * @return
     */
    public String getAgentStatus(String username){
        AgentService agentService = SpringContextUtils.getBean(AgentService.class);
        return agentService.getStatusWithName(username);
    }

    /**
     * 根据agent ID 获取用户名
     * @param id
     * @return
     */
    public String gentAgentName(Integer id){
        if (id==null){
            return null;
        }
        SysUserMapper sysUserMapper = SpringContextUtils.getBean(SysUserMapper.class);
        SysUser user = sysUserMapper.selectById(id);
        if (user==null){
            return null;
        }
        return user.getUsername();
    }
}
