package com.grgbanking.grgacd.service;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.util.exception.GrgException;
import com.grgbanking.grgacd.common.*;
import com.grgbanking.grgacd.config.AcdConfig;
import com.grgbanking.grgacd.model.AcdCalls;
import com.grgbanking.grgacd.model.AcdQueue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author wjqiu
 * @date 2019-05-08
 */

@Slf4j
@Service
public class CallerService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private AcdNotificationService acdNotificationService;
    @Autowired
    private AcdConfig acdConfig;
    @Autowired
    private AcdCallsService acdCallsService;
    @Autowired
    private AcdQueueService acdQueueService;
    //所有caller详细信息的保存列表
    private RedisUtil<Caller> cacheAllCallerList;
//    @Value("${acd.caller.timeout.checkInterval}")
//    public static long checkInterval = 30000;
    @PostConstruct
    private void init(){
        cacheAllCallerList = new RedisUtil<>(redisTemplate,Constants.KEY_CALLER);
        //启动时，先清空所有Caller列表
        cacheAllCallerList.removeAll();
    }

    public Caller login(String callerName, String clientId, String platformType) throws GrgException {
        log.info("Caller login clientId:{},callerName:{},platformType:{}",clientId,callerName,platformType);
        if (StringUtils.isEmpty(clientId)){
            log.error("parameter error:clientId is required.");
            throw new GrgException(RespCode.IME_INVALIDPARAMETER);
        }
        if (StringUtils.isEmpty(platformType)){
            platformType = Constants.PLATFORM_TYPE_IMEETING;
        }
//        if (StringUtils.isEmpty(callerName)){
//            callerName = clientId;
//        }
        Caller caller = cacheGet(clientId);
        if (caller == null){
            caller = new Caller();
//            caller.setCallerId(clientId);
            caller.setCallerName(callerName);
            caller.setPlatformType(platformType);
            caller.setStatus(CallerStatus.LOGIN.name());
        }
        caller.setClientId(clientId);
        cacheSave(caller);

        return caller;
    }
    public String getCallIdFromClinetId(String clientId){
        Caller caller = cacheGet(clientId);
        if (caller == null) {
            log.warn("caller:{} is not exist", clientId);
            throw new GrgException(RespCode.ACD_CALLER_NOT_LOGIN);
        }
        log.info("getCallIdFromClinetId clientId:{} CallId:{}", clientId,caller.getCallId());
        return caller.getCallId();
    }
    public boolean logout(String clientId) throws GrgException {
        return logout(clientId,HangupReason.FROM_CALLER);
    }
    public boolean logout(String clientId,String reason) throws GrgException {
        log.info("Caller logout clientId:{},reason:{}", clientId,reason);
        String callId = getCallIdFromClinetId(clientId);
        hangupCall(callId,clientId,reason);
        return cacheRemove(clientId);
    }
    public boolean hangupCall(String clientId,String reason) throws GrgException {
        log.info("Caller hangupCall clientId:{} reason:{}",clientId,reason);
        String callId = getCallIdFromClinetId(clientId);
        return hangupCall(callId,clientId,reason);
    }
    public boolean hangupCall(String callId,String clientId,String reason) throws GrgException {
        log.info("Caller hangupCall callId:{} clientId:{} reason:{}",callId,clientId,reason);
        acdQueueService.hangupCall(callId,clientId,Constants.CLIENT_TYPE_CALLER,reason);
        return true;
    }
    public void removeQueue(String clientId,String queueId,String removeType){
        log.info("removeQueue clientId:{} queueId:{} removeType:{}", clientId,queueId,removeType);
        if (StringUtils.isEmpty(queueId)) {
            Caller caller = cacheGet(clientId);
            if (caller != null) {
                queueId = caller.getQueueId();
            }
        }
        if (!StringUtils.isEmpty(queueId)) {
            QueueManager queueManager = acdQueueService.getQueue(queueId);
            if (queueManager == null){
                log.warn("queueId:{} is not exist", queueId);
                throw new GrgException(RespCode.ACD_QUEUE_NOT_EXIST);
            }
            // 将caller移除排队
            queueManager.caller_removeQueue(clientId,removeType);

        }

    }
    public static String getUUID(){
        return UUID.randomUUID().toString().trim().replaceAll("-", "");
    }

//    private String getCallerName(String clientId) {
//        String userName = UserUtils.getUser();
//        String callerName;
//        if (StringUtils.isEmpty(userName)) {
//            callerName = clientId;
//        } else {
//            callerName = userName;
//        }
//        return callerName;
//    }
    public Caller makeCall(String clientId,String queueId,String callerName,Object metaData) throws GrgException {
        log.info("makeCall queueId:{} clientId:{},callerName:{},metaData:{}", queueId, clientId, callerName,metaData);
//        if (StringUtils.isEmpty(callerName)) {
//            callerName = getCallerName(clientId);
//        }
//        if (StringUtils.isEmpty(callerName)) {
////            log.warn("call error:callerName is empty. queueId:{} clientId:{}",queueId,clientId);
////            return new R(Boolean.FALSE, RespCode.IME_NEED_LOGIN);
//            callerName = clientId;
//        }
        Caller caller = cacheGet(clientId);
        if (caller == null) {
            //Caller未登录。
            log.warn("call error:caller is not login. queueId:{} clientId:{}", queueId, clientId);
            throw new GrgException(RespCode.ACD_CALLER_NOT_LOGIN);
        }
        log.info("makeCall queueId:{} finnalCallerName:{},caller:{}", queueId, callerName, caller);
        if (caller != null) {
            String callId = getUUID();
            log.info("makeCall queueId:{} new callId:{}", queueId, callId);
            QueueManager queueManager = acdQueueService.getQueue(queueId);
            if (queueManager != null) {
                // add queue时会检查license
                if (queueManager.caller_CheckMakeCall(clientId)) {
                    caller.setQueueId(queueId);
                    caller.setCallId(callId);
                    caller.setStatus(CallerStatus.MAKECALL.name());
                    caller.setMetaData(metaData);
                    caller.setCallTime(new Date());
                    //保存caller状态
                    this.cacheSave(caller);

                    queueManager.caller_addQueue_tail(clientId);

                    //新建通话状态
                    AcdCalls acdCalls = new AcdCalls();
                    acdCalls.setCallId(callId);
                    acdCalls.setCallerName(caller.getCallerName());
                    acdCalls.setCallerClientId(clientId);
//                acdCalls.setCallerId(Integer.valueOf(caller.getCallerId()));
                    acdCalls.setMakecallTime(new Date());
                    acdCalls.setQueueId(queueId);
                    acdCalls.setCallStatus(CallStatus.LINE.name());
                    acdCallsService.addCall(acdCalls);
                }
            } else {
                //队列不存在。
                log.info("queueId:{} is not exist", queueId);
                throw new GrgException(RespCode.ACD_QUEUE_NOT_EXIST);
            }
        }
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("callId", caller.getCallId());

        return caller;
    }

    public boolean setStatus(String callerClientId,CallerStatus callerStatus){
        Caller caller = cacheGet(callerClientId);
        if (caller != null) {
            caller.setStatus(callerStatus.name());
            return cacheSave(caller);
        }
        return false;
    }
    public boolean setStatus(Caller caller,CallerStatus callerStatus){
        switch (callerStatus) {
            case RINGING:
                caller.setRingingTime(new Date());
                break;
        }

        caller.setStatus(callerStatus.name());
        return cacheSave(caller);
    }
    public boolean cacheSave(Caller caller){
        log.debug("cacheSave caller:{}",caller);
        if (caller != null) {
            cacheAllCallerList.addMap(caller.getClientId(),caller);
            return true;
        } else {
            return false;
        }
    }
    public boolean cacheRemove(String callerId){
        if (!StringUtils.isEmpty(callerId)) {
            cacheAllCallerList.removeMapField(callerId);
            return true;
        } else {
            return false;
        }
    }
    public Caller get(String clientId){
        Caller caller = cacheGet(clientId);
        if (caller == null) {
            throw new GrgException(RespCode.ACD_CALLER_NOT_LOGIN);
        }
        return caller;
    }
    public Caller cacheGet(String callerId){
        if (StringUtils.isEmpty(callerId)){
            return null;
        } else {
            return cacheAllCallerList.getMapField(callerId);
        }
    }
    public void reInitCaller(String callerId){
        log.info("reInitCaller callerId:{}",callerId);
        Caller caller = cacheGet(callerId);
        if (caller != null) {
            caller.reinit();
            cacheSave(caller);
        }
    }
    /**
     * 获取等待队列的数量
     * @param queueId
     * @return
     */
    public int cachePendingCount(String queueId){
        int sum = 0;
        if (StringUtils.isEmpty(queueId)){
            EntityWrapper wrapper=new EntityWrapper();
            wrapper.setSqlSelect("queue_id");
            List<Object> list = acdQueueService.selectObjs(wrapper);
            for (Object queue:list) {
                RedisUtil<String> cachePendingQueue= new RedisUtil<>(redisTemplate,Constants.KEY_QUEUE_CALLER_PENDING+queue);
                RedisUtil<Object> objectRedisUtil = new RedisUtil<>(redisTemplate, Constants.KEY_QUEUE_CALLER+queue);
                long setSize = cachePendingQueue.getSetSize();
                long listSize = objectRedisUtil.getListSize();
                sum+= (int) (setSize+listSize);
            }
            return sum;
        }
        RedisUtil<String> cachePendingQueue= new RedisUtil<>(redisTemplate,Constants.KEY_QUEUE_CALLER_PENDING+queueId);
        RedisUtil<Object> objectRedisUtil = new RedisUtil<>(redisTemplate, Constants.KEY_QUEUE_CALLER+queueId);
        long setSize = cachePendingQueue.getSetSize();
        long listSize = objectRedisUtil.getListSize();
        sum= (int) (setSize+listSize);
        return sum;
    }



}
