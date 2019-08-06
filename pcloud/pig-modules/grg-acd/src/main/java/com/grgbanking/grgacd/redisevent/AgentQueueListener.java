package com.grgbanking.grgacd.redisevent;

import com.grgbanking.grgacd.common.Constants;
import com.grgbanking.grgacd.common.QueueManager;
import com.grgbanking.grgacd.service.AcdQueueService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * @Description: 自定义redis监听--适用场景：客服入队时,使用单例设计模式
 * @auther: hsjiang
 * @date: 2019/7/26/026
 * @version 1.0
 */
public class AgentQueueListener implements MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(AgentQueueListener.class);
    private static AgentQueueListener instance = null;
    private AcdQueueService acdQueueService;
    private AgentQueueListener(AcdQueueService acdQueueService){
        this.acdQueueService = acdQueueService;
    }

    /**
     * 使用双重检查获取实例
     * @param
     * @return
     * @author hsjiang
     * @date 2019/7/26/026
     **/
    public static AgentQueueListener getInstance(AcdQueueService acdQueueService){
        if(instance == null){
            synchronized (AgentQueueListener.class){
                if(instance == null){
                    instance = new AgentQueueListener(acdQueueService);
                }
            }
        }
        return instance;
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        notify(message.toString());
    }

    /**
     * 通知等待的线程
     * @param key
     * @return
     * @author hsjiang
     * @date 2019/7/26/026
     **/
    private void notify(String key){
        if(StringUtils.isNotEmpty(key) && key.indexOf(Constants.KEY_QUEUE_AGENT) != -1){
            String queueId = key.replace(Constants.KEY_QUEUE_AGENT,"");
            QueueManager queueManager = acdQueueService.getQueue(queueId);
            if(null != queueManager){
                synchronized (queueManager){
                    queueManager.notifyAll();
                    logger.info("notify those waiting thread,from Queue:{}",queueId);
                }
            }
        }

    }
}
