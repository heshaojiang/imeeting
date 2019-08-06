package com.grgbanking.grgacd.service;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.grgbanking.grgacd.common.Agent;
import com.grgbanking.grgacd.common.Caller;
import com.grgbanking.grgacd.model.AcdCalls;
import com.grgbanking.grgacd.rpc.Service.RpcNotificationService;
import com.grgbanking.grgacd.rpc.common.AcdMsgType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wjqiu
 * @date 2019-04-21
 * @editor jinwen
 */
@Service
public class AcdNotificationService {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AgentService agentService;
    @Autowired
    private CallerService callerService;

    @Autowired
    private AcdCallsService callsService;

    @Autowired
    private RpcNotificationService rpcNotificationService;

    public void sendRingingEvent(String callId) {
        log.info("sendRingingEvent callId:{}", callId);
        sendNotificationWithCall(callId,(caller,agent,jsonObject)->{
            if (caller != null) {
                String callerClientId = caller.getClientId();
                log.info("sendRingingEvent callerClientId:{}", callerClientId);
                sendNotification(caller.getClientId(), AcdMsgType.RINGING_EVENT, jsonObject);
            }
            if (agent != null) {
                String agentClientId = agent.getClientId();
                log.info("sendRingingEvent agentClientId:{}", agentClientId);
                sendNotification(agentClientId, AcdMsgType.RINGING_EVENT, jsonObject);
            }
        });

    }

    public void sendJoinCallEvent(String callId, Object metaData) {
        log.info("sendJoinCallEvent callId:{}", callId);
        sendNotificationWithCall(callId,(caller,agent,jsonObject)->{
            jsonObject.add("metaData_accept", new GsonBuilder().create().toJsonTree(metaData));
            if (caller != null) {
                String callerClientId = caller.getClientId();
                log.info("sendJoinCallEvent callerClientId:{}", callerClientId);
                sendNotification(callerClientId, AcdMsgType.JOINCALL_EVENT, jsonObject);
            }
            if (agent != null) {
                String agentClientId = agent.getClientId();
                log.info("sendJoinCallEvent agentClientId:{}", agentClientId);
                sendNotification(agentClientId, AcdMsgType.JOINCALL_EVENT, jsonObject);
            }
        });

    }

    public void sendRejectCallEvent(String callId) {
        log.info("sendRingingEvent callId:{}", callId);

        sendNotificationWithCall(callId,(caller,agent,jsonObject)->{
            if (caller != null) {
                String callerClientId = caller.getClientId();
                log.info("sendRejectCallEvent callerClientId:{}", callerClientId);
                sendNotification(callerClientId, AcdMsgType.REJECTCALL_EVENT, jsonObject);
            }
        });

    }

    public void sendHangupEvent(String callId,String reason) {
        log.info("sendHangupEvent callId:{},reason:{}", callId,reason);
        sendNotificationWithCall(callId,(caller,agent,jsonObject)->{
            jsonObject.addProperty("reason", reason);
            if (caller != null) {
                String callerClientId = caller.getClientId();
                log.info("sendHangupEvent callerClientId:{}", callerClientId);
                sendNotification(callerClientId, AcdMsgType.HANGUP_EVENT, jsonObject);
            }
            if (agent != null) {
                String agentClientId = agent.getClientId();
                log.info("sendHangupEvent agentClientId:{}", agentClientId);
                sendNotification(agentClientId, AcdMsgType.HANGUP_EVENT, jsonObject);
            }
        });

    }

    public void sendTimeoutEvent(String callId,String reason) {
        log.info("sendTimeoutEvent callId:{},reason:{}", callId,reason);
        sendNotificationWithCall(callId,(caller,agent,jsonObject)->{
            jsonObject.addProperty("reason", reason);
            if (caller != null) {
                String callerClientId = caller.getClientId();
                log.info("sendTimeoutEvent callerClientId:{}", callerClientId);
                sendNotification(callerClientId, AcdMsgType.TIMEOUT_EVENT, jsonObject);
            }
            if (agent != null) {
                String agentClientId = agent.getClientId();
                log.info("sendTimeoutEvent agentClientId:{}", agentClientId);
                sendNotification(agentClientId, AcdMsgType.TIMEOUT_EVENT, jsonObject);
            }
        });
    }
//
//    public void sendRingingEvent(final String clientId, final String method, final Object result) {
////        log.info("sendRingingEvent clientId:{},callId:{},callerId:{},queueID:{},agentId:{}", clientId, callId, callerId, queueID, agentId);
////        JsonObject jsonObject = getJsonObject(callId, callerId, queueID, agentId);
////        jsonObject.add("metaData_makecall", new GsonBuilder().create().toJsonTree(metaData_makecall));
////        sendNotification(clientId, AcdMsgType.RINGING_EVENT, jsonObject);
//    }
//
//    public void sendJoinCallEvent(String clientId, String callId, String callerId, String queueID, String agentId, Object metaData, Object metaData_makecall) {
//        log.info("sendJoinCallEvent clientId:{},callId:{},callerId:{},queueID:{},agentId:{}", clientId, callId, callerId, queueID, agentId);
//        JsonObject jsonObject = getJsonObject(callId, callerId, queueID, agentId);
////        jsonObject.addProperty("metaData", metaData);
//        jsonObject.add("metaData", new GsonBuilder().create().toJsonTree(metaData));
//        jsonObject.add("metaData_makecall", new GsonBuilder().create().toJsonTree(metaData_makecall));
//        sendNotification(clientId, AcdMsgType.ACCEPTCALL_EVENT, jsonObject);
//        sendNotification(clientId, AcdMsgType.JOINCALL_EVENT, jsonObject);
//    }
//
//    public void sendRejectCallEvent(String clientId, String callId, String callerId, String queueID, String agentId) {
//        log.info("sendRejectCallEvent clientId:{},callId:{},callerId:{},queueID:{},agentId:{}", clientId, callId, callerId, queueID, agentId);
//        Object jsonObject = getJsonObject(callId, callerId, queueID, agentId);
//        sendNotification(clientId, AcdMsgType.REJECTCALL_EVENT, jsonObject);
//    }
//
//    public void sendHangupEvent(String clientId, String callId, String hangupType) {
//        log.info("sendHangupEvent clientId:{},callId:{},hangupType:{}", clientId, callId, hangupType);
//        Object jsonObject = getJsonObject(callId, hangupType);
//        sendNotification(clientId, AcdMsgType.HANGUP_EVENT, jsonObject);
//    }
//    public void sendTimeoutEvent(String clientId, String callId, String queueId, String agentName, String reason) {
//        log.info("sendTimeoutEvent clientId:{},callId:{},reason:{}", clientId, callId, reason);
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("callId", callId);
//        jsonObject.addProperty("queueId", queueId);
//        jsonObject.addProperty("agentName", agentName);
//        jsonObject.addProperty("reason", reason);
//        sendNotification(clientId, AcdMsgType.TIMEOUT_EVENT, jsonObject);
//    }
    public void sendNotification(final String clientId, final String method, final Object result) {
        if (StringUtils.isNotEmpty(clientId) && StringUtils.isNotEmpty(method) && result != null) {
            rpcNotificationService.sendNotificationToClient(clientId, method, result);
        }
    }
    public void sendAgentStatusChange(String clientId,String status,String lastStatus) {
        log.info("sendAgentStatusChange clientId:{},status:{},lastStatus:{}", clientId,status,lastStatus);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("clientId", clientId);
        jsonObject.addProperty("status", status);
        jsonObject.addProperty("lastStatus", lastStatus);
        sendNotification(clientId, AcdMsgType.AGENT_STATUS_CHANGE_EVENT, jsonObject);
    }

    interface Executor {
        void execute(Caller caller,Agent agent,JsonObject jsonObject);
    }
    public void sendNotificationWithCall(String callId,Executor executor) {
        log.info("sendNotificationWithCall callId:{}", callId);
        AcdCalls calls = callsService.getCallById(callId);
        if (calls != null) {
            String callerClientId = calls.getCallerClientId();
            String queueId = calls.getQueueId();
            String agentClientId = calls.getAgentClientId();

            Agent agent = agentService.cacheGet(agentClientId);
            Caller caller = callerService.cacheGet(callerClientId);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("callId", callId);
            jsonObject.addProperty("queueId", queueId);
            jsonObject.add("agent", new GsonBuilder().create().toJsonTree(agent));
            if (caller != null) {
                Object metaData_makecall = caller.getMetaData();
                //屏蔽一些不需要的数据
                caller.setMetaData(null);
                jsonObject.add("caller", new GsonBuilder().create().toJsonTree(caller));

                if (metaData_makecall != null)
                {
                    log.info("sendNotificationWithCall metaData_makecall:{}", metaData_makecall);
                    jsonObject.add("metaData_makecall", new GsonBuilder().create().toJsonTree(metaData_makecall));
                }

            }
            executor.execute(caller,agent,jsonObject);
        } else {
            log.warn("sendNotificationWithCall callId not exist");
        }
    }
    public boolean checkClientOnline(String clientId) {
        return rpcNotificationService.checkClientIdOnline(clientId);
    }
}
