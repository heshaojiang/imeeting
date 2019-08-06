package com.grgbanking.grgacd.queue;

import com.grgbanking.grgacd.common.QueueManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.QueryTimeoutException;

import java.util.List;

/**
 * @Description: 闲时优先策略处理类
 * @auther: hsjiang
 * @date: 2019/7/18/018
 * @version 1.0
 */
public class FreeTimePriorQueue extends QueueManager {
    public FreeTimePriorQueue(String queueId){
        super(queueId);
    }
    @Override
    public void agent_addQueue(String clientId) {
        if (StringUtils.isNotEmpty(clientId)) {
            boolean bExist = false;
            List<String> allClientId = getCacheAgentQueue().listGetAll();
            for (String oneClientId : allClientId) {
                if (oneClientId.equals(clientId)) {
                    bExist = true;
                    break;
                }
            }
            if (bExist) {
                getLog().warn("agent:{},is Already In The Queue:{}",clientId, getQueueId());
            } else {
                pushFromTail(clientId);
                getLog().info("agent_addQueue:{},Queue:{}", clientId, getQueueId());
            }
        }
    }

    @Override
    public void agent_removeQueue(String clientId) {
        if (StringUtils.isNotEmpty(clientId)) {
            Long removeCount = getCacheAgentQueue().listRemove(clientId);
            getLog().info("agent_removeQueue:{},removeCount:{}", clientId, removeCount);
        }
    }

    @Override
    public void agent_addQueueHead(String clientId){
        getCacheAgentQueue().pushFromHead(clientId);
    }

    public void  pushFromTail(String clientId){
        getCacheAgentQueue().pushFromTail(clientId);
    }

    @Override
    public String getOneAgent() {
        String clientId = null;
        do {
            try {
                clientId = getCacheAgentQueue().takeFromHead();
            } catch (Exception e) {
                if (!(e instanceof QueryTimeoutException)) {
                    e.printStackTrace();
                }
            }
        } while (isRunning() && StringUtils.isEmpty(clientId));

        getLog().info("getOneAgent:{},from Queue:{}",clientId, getQueueId());
        return clientId;
    }

}
