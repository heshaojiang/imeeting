package com.grgbanking.grgacd.common.schedule;

import com.grgbanking.grgacd.common.*;
import com.grgbanking.grgacd.config.AcdConfig;
import com.grgbanking.grgacd.service.AcdCallsService;
import com.grgbanking.grgacd.service.AcdNotificationService;
import com.grgbanking.grgacd.service.AcdQueueService;
import com.grgbanking.grgacd.service.CallerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;

/**
 * @author wjqiu
 * @date 2019-07-12
 * @description 定时检查Caller状态。关闭已经超时的呼叫
 */

@Slf4j
@Component
public class CheckCallerTimeoutTask {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private AcdNotificationService acdNotificationService;
    @Autowired
    private AcdConfig acdConfig;
    @Autowired
    private AcdQueueService acdQueueService;
    @Autowired
    private CallerService callerService;
    //所有caller详细信息的保存列表
    private RedisUtil<Caller> cacheAllCallerList;
    @PostConstruct
    private void init(){
        cacheAllCallerList = new RedisUtil<>(redisTemplate,Constants.KEY_CALLER);
    }

    /**
     * 定时检查Caller状态。关闭超时的呼叫
     */
    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void checkCallerTimeoutSchedule() {
        try {
            Map<String, Caller> mapCallers = cacheAllCallerList.getMap();
            for (String key : mapCallers.keySet()) {
                Caller caller = mapCallers.get(key);
                if (CallerStatus.MAKECALL.name().equals(caller.getStatus()) ||
                        CallerStatus.PENDING.name().equals(caller.getStatus()) ||
                        CallerStatus.RINGING.name().equals(caller.getStatus())) {
                    Date now = new Date();
                    Date dateCall = caller.getCallTime();
                    Date dateRinging = caller.getRingingTime();
                    boolean bMakecallTimeout = false;
                    boolean bPedingTimeout = false;
                    String clientId = caller.getClientId();
                    String reason = null;
                    if (dateCall != null) {
                        long nMakeCall = now.getTime() - caller.getCallTime().getTime();
                        if (nMakeCall > acdConfig.getCaller_makecall_timeout() * 1000) {
                            log.warn("Caller:{}[{}] has makecall timeout", clientId, caller.getCallerName());
                            bMakecallTimeout = true;
                            reason = HangupReason.TIME_OUT_makecall;
                        }
                    }
                    if (dateRinging != null) {
                        long nRinging = now.getTime() - caller.getRingingTime().getTime();
                        if (nRinging > acdConfig.getCaller_ringing_timeout() * 1000) {
                            log.warn("Caller:{}[{}] has ringing timeout", clientId, caller.getCallerName());
                            bPedingTimeout = true;
                            reason = HangupReason.TIME_OUT_ringing;
                        }
                    }
                    if (bMakecallTimeout || bPedingTimeout) {

                        try {
                            acdQueueService.hangupCall(caller.getCallId(),clientId,Constants.CLIENT_TYPE_CALLER, reason);
                            acdNotificationService.sendTimeoutEvent(caller.getCallId(), reason);
                        } catch (Exception e) {
                            log.warn("checkCallerTimeoutSchedule hangupCall Get Exception", e);
                        }
                        callerService.reInitCaller(clientId);

//                    cacheRemove(key);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("checkCallerTimeoutSchedule Get Exception", e);
        }
    }


}
