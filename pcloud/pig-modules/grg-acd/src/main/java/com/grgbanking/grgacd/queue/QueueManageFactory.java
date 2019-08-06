package com.grgbanking.grgacd.queue;

import com.grgbanking.grgacd.common.QueueManager;
import com.grgbanking.grgacd.model.AcdQueue;

/**
 * @Description: 队列管理器生成工厂类（简单工厂）
 * @auther: hsjiang
 * @date: 2019/7/18/018
 * @version 1.0
 */
public class QueueManageFactory {
    public static QueueManager getQueueManager(AcdQueue acdQueue){
        String strategy = acdQueue.getStrategy();
        if(strategy.equals(QueueStrategy.IDLE_FIRST.getType())){
            return new FreeTimePriorQueue(acdQueue.getQueueId());
        }
        else if(strategy.equals(QueueStrategy.SERVICE_LAST_FIRST.getType())){
            return new ServiceTimeLeastQueue(acdQueue.getQueueId());
        }
        return null;
    }
}
