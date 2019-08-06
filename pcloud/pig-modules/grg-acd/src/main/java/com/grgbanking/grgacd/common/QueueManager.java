package com.grgbanking.grgacd.common;

import com.github.pig.common.util.RespCode;
import com.github.pig.common.util.exception.GrgException;
import com.grgbanking.grgacd.config.AcdConfig;
import com.grgbanking.grgacd.service.*;
import com.grgbanking.grgacd.service.impl.AcdCallsServiceImpl;
import com.grgbanking.grgacd.service.impl.AgentStatusHistoryServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author wjqiu
 * @date 2019-04-21
 *
 * 用于管理一个队列的排队处理逻辑。每个队列中包含一个处理线程。
 * 在线程中，每次从CallerQueue取出一个Caller，放入PendingQueue中，则表示当前Caller的呼叫正在处理中。
 * 再以阻塞的方式从AgentQueue中获取一个Agent.并发送CallingEvent事件给Agent。并等待Agent接受呼叫。
 * 收到Agent Accept的请求时，则先检查Caller是否在PendingQueue中。确保当前呼叫仍在进行中。
 * Accept后则把Caller从PendingQueue中移出。则开始通话。
 */

public abstract class QueueManager {
    private AcdConfig acdConfig;
    private RedisTemplate redisTemplate;
    private String queueId;
    private AcdNotificationService acdNotificationService;
    private boolean running = true;
    private Logger log = LoggerFactory.getLogger(this.getClass());
    //caller排队队列
    private RedisUtil<String> cacheCallerQueue;
    //agent排队队列
    private RedisUtil<String> cacheAgentQueue;
    //caller等待处理队列，从CallerQueue中取出caller进行处理时，则把此caller放入PendingQueue中
    private RedisUtil<String> cachePendingQueue;

    private AgentService agentService;
    private CallerService callerService;
    private AcdCallsService acdCallsService;
    private AgentStatusHistoryService agentStatusHistoryService;

//    private LicenseManage licenseManage;

    public QueueManager(String queueId) {
        this.agentService = SpringContextUtils.getBean(AgentService.class);
        this.callerService = SpringContextUtils.getBean(CallerService.class);
        this.acdCallsService = SpringContextUtils.getBean(AcdCallsServiceImpl.class);
        this.agentStatusHistoryService = SpringContextUtils.getBean(AgentStatusHistoryServiceImpl.class);
        this.acdConfig = SpringContextUtils.getBean(AcdConfig.class);
        this.redisTemplate = SpringContextUtils.getBean("redisTemplate", RedisTemplate.class);
        this.acdNotificationService = SpringContextUtils.getBean(AcdNotificationService.class);
        this.queueId = queueId;
        cacheCallerQueue = new RedisUtil<>(redisTemplate, Constants.KEY_QUEUE_CALLER + queueId);
        cacheAgentQueue = new RedisUtil<>(redisTemplate, Constants.KEY_QUEUE_AGENT + queueId);
        cachePendingQueue = new RedisUtil<>(redisTemplate, Constants.KEY_QUEUE_CALLER_PENDING + queueId);

        //启动处理线程
        startProcessThread();

    }

    @Override
    protected void finalize() {
        running = false;
    }

    private void startProcessThread() {
        new Thread(() -> {
            log.info("Queue:{},Start One Queue", queueId);
            while (running) {

                String callerClientId = null;
                try {
                    callerClientId = cacheCallerQueue.takeFromHead();
                } catch (Exception e) {
                    if (!(e instanceof QueryTimeoutException)) {
                        e.printStackTrace();
                    }
                }
                //取出一个排队中的caller
                if (!StringUtils.isEmpty(callerClientId)) {
                    //获取caller信息
                    log.info("Queue:{},Get a Caller callerClientId:{}", queueId,callerClientId);
                    try {
                        Caller caller = callerService.cacheGet(callerClientId);
                        if (caller != null) {
                            //放入到等待处理的队列中
                            cachePendingQueue.addSet(callerClientId);

                            callerService.setStatus(caller, CallerStatus.PENDING);

                            String callId = caller.getCallId();
//                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            log.info("Queue:{} get Caller:{},callid:{}", queueId, callerClientId, callId);
                            //获取一个agent进行服务，如果没有空闲的agent则一直等待到有空闲agent为止
                            String agentClientId = getOneAgent();
                            if (caller_InPendingQueue(callerClientId)) {
                                //已经选择到了一个Agent
                                if (!StringUtils.isEmpty(agentClientId)) {
                                    Agent agent = agentService.cacheGet(agentClientId);
                                    if (agent != null) {
                                        //更新、记录坐席状态
                                        agent.setCallId(callId);
                                        agentService.cacheSave(agent);
                                        agentService.setStatus(agent, AgentStatus.SELECTED);

                                        //更新通话记录
                                        acdCallsService.updateCalls(callId, queueId, agent, CallStatus.RING);
                                        log.info("Queue:{}, get Agent:{}", queueId, agentClientId);
                                        acdNotificationService.sendRingingEvent(callId);
                                        //等待Agent接听通话
                                        callerService.setStatus(caller, CallerStatus.RINGING);
                                    }

                                }
                            } else {
                                // Caller已不在队列中，需要把取出的agent重新放回队列的首位
                                log.info("Queue:{} Caller:{},not in pending.repush agent", queueId, callerClientId, agentClientId);
                                //cacheAgentQueue.agent_addQueueHead(agentClientId);
                                agent_addQueueHead(agentClientId);
                            }

                        } else {
                            //在redis中找不到，则已经超时。
                            log.info("Queue:{},caller not find in cache , get next caller", queueId);
                        }
                    }catch (Exception e) {
                        log.info("Queue:{},get Exception callerClientId:{}", queueId,callerClientId);
                        e.printStackTrace();

                    }
                } else {
                    log.info("Queue:{},callerQueue is empty, wait for caller makecall", queueId);
                    //队列为空，则等待
                    synchronized(this) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
            log.info("Queue:{},thread end!", queueId);
        }).start();
    }

    public void caller_addQueue_head(String callerId) {
        if (!StringUtils.isEmpty(callerId) && !caller_InQueue(callerId)) {
            log.info("caller:{},agent_addQueueHead to Queue:{}",callerId, queueId);
            cacheCallerQueue.pushFromHead(callerId);
        }
    }
    public RespCode licenseVerify() {
        RespCode respCode = RespCode.SUCCESS;
//        //查询当前所有会议的总入会成员数
//        Integer countCaller = bizMeetingParticipantService.countParticipantOnLine(null);
//        //查询当前正在开会的会议数
//        int countAgent = agentService.getAgentCountWithStatus(null);
//        int countAgentInCall = agentService.getAgentCountWithStatus(AgentStatus.SERVICE);
//
////        respCode = licenseManage.licenseVerfify(nParticipantCount,countAgentInCall);

        return respCode;
    }
    public synchronized  boolean  caller_CheckMakeCall(String clientId) {
        log.info("caller_CheckMakeCall clientId:{}",clientId);
        //检查 License
        RespCode respCode = licenseVerify();
        if (respCode != RespCode.SUCCESS){
            log.warn("------------------  CHECK LICENSE FAILED ----------");
            throw new GrgException(respCode);
        }

        if (StringUtils.isEmpty(clientId)){
            log.info("Queue:{} caller_addQueue_tail clientId is empty", queueId);
            throw new GrgException(RespCode.ACD_CLIENT_NAME_EMPTY);
        }
        if (caller_InQueue(clientId))
        {
            log.info("Queue:{} caller_addQueue_tail caller is In Queue.", queueId);
            throw new GrgException(RespCode.ACD_IN_QUEUE);
        }
        if (caller_InPendingQueue(clientId))
        {
            log.info("Queue:{} caller_addQueue_tail caller is In Pending Queue.", queueId);
            throw new GrgException(RespCode.ACD_IN_PENDING_QUEUE);
        }

        log.info("------------------  CHECK MAKE CALL SUCCESS ----------");
        return true;
    }
    public synchronized  boolean  caller_addQueue_tail(String clientId) throws GrgException {

        log.info("Queue:{},caller:{},pushFromTail to ", queueId,clientId);
        cacheCallerQueue.pushFromTail(clientId);

        //唤醒处理线程
        this.notifyAll();
        return true;
    }

    public void caller_removeQueue(String clientId,String removeType) {
        log.info("caller_removeQueue clientId:{} removeType:{}",clientId,removeType);
        if (!StringUtils.isEmpty(clientId)) {
            //删除caller_pending中的caller
            long removeCount_Pending = cachePendingQueue.removeSetValue(clientId);
            //删除callerQueue中的caller
            Long removeCount_CallerQueue = cacheCallerQueue.listRemove(clientId);

            switch (removeType) {
                case Constants.REMOVE_QUEUE_TYPE_ACCEPT:
                    callerService.setStatus(clientId,CallerStatus.SERVICE);
                    break;
                case Constants.REMOVE_QUEUE_TYPE_HANGUP:
                    //重置caller状态
                    callerService.reInitCaller(clientId);
                    break;
                case Constants.REMOVE_QUEUE_TYPE_RECALL:{
                    Caller caller = callerService.cacheGet(clientId);
                    if (caller != null) {
                        caller.setCallTime(null);
                        caller.setStatus(CallerStatus.MAKECALL.name());
                        callerService.cacheSave(caller);
                    }
                    break;
                }


            }

            log.info("caller_removeQueue:{},removeCount_Pending:{},removeCount_CallerQueue:{}", clientId, removeCount_Pending, removeCount_CallerQueue);
        }
    }
    public boolean caller_InQueue(String clientId) {
        boolean bExist = false;
        if (!StringUtils.isEmpty(clientId)) {
            List<String> callerIds = cacheCallerQueue.listGetAll();
            for (String id : callerIds) {
                if (id.equals(clientId)) {
                    bExist = true;
                    log.warn("caller:{},is Already In The Queue:{}",clientId, queueId);
                    break;
                }
            }
        }
        return bExist;
    }
    public boolean caller_InPendingQueue(String clientId) {
        if (!StringUtils.isEmpty(clientId)) {
            return cachePendingQueue.hasSetValue(clientId);
        } else {
            return false;
        }
    }

    /**
     * 将客服加入队列中的抽象方法，由子类实现
     * @param clientId 客服clientId
     * @return
     * @author hsjiang
     * @date 2019/7/22/022
     **/
    public abstract void agent_addQueue(String clientId);

    /**
     * 将客服从队列中移除的抽象方法，由子类实现
     * @param clientId 客服clientId
     * @return
     * @author hsjiang
     * @date 2019/7/22/022
     **/
    public abstract void agent_removeQueue(String clientId);

    /**
     * 从队列中头部添加的抽象方法，由子类实现
     * @param clientId 客服clientId
     * @return
     * @author hsjiang
     * @date 2019/7/22/022
     **/
    public abstract void agent_addQueueHead(String clientId);

    /**
     * 定义获取客服的抽象方法，由子类实现
     * @param
     * @return
     * @author hsjiang
     * @date 2019/7/19/019
     **/
    public abstract String getOneAgent();

    public String getQueueId(){
        return queueId;
    }
    protected AcdConfig getAcdConfig() {
        return acdConfig;
    }

    protected RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    protected AcdNotificationService getAcdNotificationService() {
        return acdNotificationService;
    }

    protected boolean isRunning() {
        return running;
    }

    protected Logger getLog() {
        return log;
    }

    protected RedisUtil<String> getCacheCallerQueue() {
        return cacheCallerQueue;
    }

    protected RedisUtil<String> getCacheAgentQueue() {
        return cacheAgentQueue;
    }

    protected RedisUtil<String> getCachePendingQueue() {
        return cachePendingQueue;
    }

    protected AgentService getAgentService() {
        return agentService;
    }

    protected CallerService getCallerService() {
        return callerService;
    }

    protected AcdCallsService getAcdCallsService() {
        return acdCallsService;
    }

    protected AgentStatusHistoryService getAgentStatusHistoryService() {
        return agentStatusHistoryService;
    }
}
