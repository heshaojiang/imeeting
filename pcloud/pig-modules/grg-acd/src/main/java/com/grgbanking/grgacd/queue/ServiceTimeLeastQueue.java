package com.grgbanking.grgacd.queue;

import com.grgbanking.grgacd.common.Agent;
import com.grgbanking.grgacd.common.QueueManager;
import org.apache.commons.lang.StringUtils;

/**
 * @Description: 最少服务时间策略处理类
 * @auther: hsjiang
 * @date: 2019/7/18/018
 * @version 1.0
 */
public class ServiceTimeLeastQueue extends QueueManager {
    public ServiceTimeLeastQueue(String queueId){
        super(queueId);
    }

    @Override
    public void agent_addQueue(String clientId) {
        if (StringUtils.isNotEmpty(clientId)) {
            boolean bExist = getCacheAgentQueue().zSetContain(clientId);
            if (bExist) {
                getLog().warn("agent:{},is Already In The Queue:{}",clientId, getQueueId());
            } else {
                addQueue(clientId);
                getLog().info("agent_addQueue:{},Queue:{}", clientId, getQueueId());
            }
        }
    }

    @Override
    public void agent_removeQueue(String clientId) {
        if (StringUtils.isNotEmpty(clientId)) {
            Long removeCount = getCacheAgentQueue().zSetRemove(clientId);
            getLog().info("agent_removeQueue:{},removeCount:{}", clientId, removeCount);
        }
    }

    public void addQueue(String clientId){
        Agent agent = getAgentService().cacheGet(clientId);
        getCacheAgentQueue().zSetAddOne(clientId,agent.getServiceTime());
    }
    @Override
    public void agent_addQueueHead(String clientId){
        addQueue(clientId);
    }

    @Override
    public String getOneAgent() {
        String clientId = null;
        synchronized (this) {
            do {
                clientId = getCacheAgentQueue().zSetGetLowest();
                if (StringUtils.isEmpty(clientId)) {
                    try {
                        this.wait(60 * 1000);//等待60s，以防caller退出后，还一直处于等待状态
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    agent_removeQueue(clientId);//将其从缓存中清除
                }
            } while (isRunning() && StringUtils.isEmpty(clientId)) ;
        }
        getLog().info("getOneAgent:{},from Queue:{}",clientId, getQueueId());
        return clientId;
    }
}
