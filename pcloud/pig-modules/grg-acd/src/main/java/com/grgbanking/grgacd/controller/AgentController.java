package com.grgbanking.grgacd.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.*;
import com.github.pig.common.util.exception.GrgException;
import com.grgbanking.grgacd.common.*;
import com.grgbanking.grgacd.dto.converter.AgentConverter;
import com.grgbanking.grgacd.model.AcdCalls;
import com.grgbanking.grgacd.model.SysUser;
import com.grgbanking.grgacd.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wjqiu
 * @date 2019-04-20
 */
@Slf4j
@RestController
@RequestMapping("/agent")
public class AgentController {

    @Autowired
    private AcdCallsService acdCallsService;
    @Autowired
    private AcdQueueService queueService;
    @Autowired
    private AgentService agentService;
    @Autowired
    private UserService userService;
    @Autowired
    private AcdNotificationService notificationService;

    @RequestMapping("/login")
    public R login(HttpServletRequest request, @RequestBody Map<String, Object> params) throws GrgException {
//        String agentName = CommonUtils.getMappingParams(params,"agentName");
        String clientId = CommonUtils.getMappingParams(params,"clientId");
        String machineCode = CommonUtils.getMappingParams(params,"machineCode");
        String token = UserUtils.getToken(request);
        Agent agent;
        try {
            agent = agentService.login(clientId,token);
        } catch (GrgException e) {
            return new R(Boolean.FALSE,e.getStatusCode());
        }
        if (agent != null){
            return new R(true,agent);
        } else {
            return new R(false);
        }
    }

    @RequestMapping("/logout")
    public R logout(@RequestBody Map<String, Object> params) throws GrgException {
        String clientId = CommonUtils.getMappingParams(params,"clientId");
        log.info("logout clientId:{}",clientId);
        //检查指定的clientId是否为当前登录的Agent
        agentService.verifyCurAgent(clientId);
//        String agentName = getAgentName(clientId);
        agentService.logout(clientId);
        return new R(true);
    }
    private String getAgentName(String clientId) {
        String userName = UserUtils.getUser();
        String agentName;
        if (StringUtils.isEmpty(userName)) {
            agentName = clientId;
        } else {
            agentName = userName;
        }
        return agentName;
    }

    /**
     * 设置状态
     * @return R
     */
    @RequestMapping("/setStatus")
    public R setStatus(@RequestBody Map<String, Object> params) throws GrgException {
        String clientId = CommonUtils.getMappingParams(params,"clientId");
        String status = CommonUtils.getMappingParams(params,"status");
        String agentName = getAgentName(clientId);
        log.info("setStatus clientId:{},agentName:{},status:{}",clientId,agentName,status);
        Agent agent = agentService.cacheGet(clientId);
        //检查指定的clientId是否为当前登录的Agent
        agentService.verifyCurAgent(agent);
        if (agent != null){
            boolean b = agentService.setStatusStr(agent,status);
            return new R(true,b);
        }else {
            return new R(RespCode.ACD_AGENT_NOT_LOGIN);
        }
    }

    /**
     * 获取状态
     * @return R
     */
    @RequestMapping("/getStatus")
    public R getStatus(@RequestBody Map<String, Object> params) throws GrgException {
        String clientId = CommonUtils.getMappingParams(params,"clientId");
        String agentName = getAgentName(clientId);
        log.info("getStatus clientId:{},agentName:{}",clientId,agentName);
        String status = agentService.getStatus(clientId);
        if (StringUtils.isNotEmpty(status)){
            return new R(Boolean.TRUE,status);
        } else {
            return new R(Boolean.FALSE);
        }
    }

    @RequestMapping("/accept")
    public R accept(@RequestBody Map<String, Object> params) throws GrgException {
        String clientId = CommonUtils.getMappingParams(params,"clientId");
        String callId = CommonUtils.getMappingParams(params,"callId");
        Object metaData = params.get("metaData");
        log.info("accept callId:{},metaData:{}",callId,metaData);

        Agent agent = agentService.cacheGet(clientId);
        //检查指定的clientId是否为当前登录的Agent
        agentService.verifyCurAgent(agent);

        if (!StringUtils.isEmpty(callId)){
            AcdCalls calls = acdCallsService.getCallById(callId);
            if (calls != null) {
                //通话连接，更新通话记录
                calls.setAnswerTime(new Date());
                calls.setCallStatus(CallStatus.CONNECT.name());
                calls.setCallId(callId);
                acdCallsService.updateCallById(calls);


//                if (agent != null){

                    String callerClientId = calls.getCallerClientId();
                    String queueId = calls.getQueueId();
                    log.info("accept callerClientId:{} queueId:{}",callerClientId,queueId);
                    QueueManager queueManager = queueService.getQueue(queueId);

                    //判断当前的客户，是否在pending队列中。
                    if (queueManager != null && queueManager.caller_InPendingQueue(callerClientId)){
                        //caller已经在服务中。删除出队列
                        queueManager.caller_removeQueue(callerClientId,Constants.REMOVE_QUEUE_TYPE_ACCEPT);
                        //发起入会事件，通知caller和agent加入通话。
                        notificationService.sendJoinCallEvent(callId,metaData);
                        agentService.setStatus(agent,AgentStatus.SERVICE,queueId);
                        return new R(Boolean.TRUE);
                    } else {
                        if (queueManager != null) {
                            log.warn("queue:{} caller:{} not find in pending list.",queueManager.getQueueId(),callerClientId);
                            return new R(RespCode.ACD_ACCEPT_FAIL,"Caller Not Calling!");
                        } else {
                            log.warn("queue:{} not find.",queueManager.getQueueId());
                            return new R(RespCode.ACD_ACCEPT_FAIL,"Queue Not Found!");
                        }
                    }
//                } else {
//                    log.warn("agent:{} not find in cache.",clientId);
//                    return new R(RespCode.ACD_ACCEPT_FAIL,"Agent Not Found!");
//                }

            } else {
                log.warn("callId:{} not exist",callId);
                return new R(RespCode.ACD_ACCEPT_FAIL,"CallId Not Found!");
            }

        }
        return new R(Boolean.FALSE);
    }

    /**
     * 拒接
     * @return R
     */
    @RequestMapping("/reject")
    public R reject(@RequestBody Map<String, Object> params) throws GrgException {
        String clientId = CommonUtils.getMappingParams(params,"clientId");
        String callId = CommonUtils.getMappingParams(params,"callId");
        log.info("reject callId:{} clientId:{}",callId,clientId);

        Agent agent = agentService.cacheGet(clientId);
        //检查指定的clientId是否为当前登录的Agent
        agentService.verifyCurAgent(agent);

        //拒接，则把Caller重新进行排队
        queueService.setCallerReCall(callId);
        //拒接后设置agent状态为ACW
        agentService.setStatus(agent,AgentStatus.ACW,callId);

        notificationService.sendRejectCallEvent(callId);

        return new R(Boolean.TRUE);
    }

    @RequestMapping("/hangup")
    public R hangup(@RequestBody Map<String, Object> params) throws GrgException {
        String clientId = CommonUtils.getMappingParams(params,"clientId");
        String callId = CommonUtils.getMappingParams(params,"callId",false);
        log.info("hangup callId:{}",callId);
        agentService.hangupCall(callId,clientId,HangupReason.FROM_AGNET);
        return new R(Boolean.TRUE);
    }


    /*后台列表*/

    /**
     * 获取agent的所有状态列表
     * @return R
     */
    @GetMapping("/status")
    public R<Object> getAgentStatus(){
        List<String> agentStatusList=new ArrayList<>();
        for (AgentStatus status :AgentStatus.values()){
            agentStatusList.add(status.name());
        }
        return new R<>(Boolean.TRUE,agentStatusList);
    }

    @GetMapping("/page")
    public R<Page> getAgentPage(@RequestParam Map<String, Object> params){

        Page page = AgentConverter.INSTANCE.map(userService.getAgentPage(new Query<>(params)));
        return new R<>(Boolean.TRUE,page);
    }

    @GetMapping("/select")
    public R getAgentList(@RequestParam Map<String, Object> params){
        List<SysUser> agentList = userService.getAgentList(params);

        return new R(Boolean.TRUE,agentList);
    }

    @PostMapping
    public R<Boolean> addAgent(@RequestBody SysUser user){
        RespCode respCode = userService.addAgent(user);
//        if (respCode==RespCode.SUCCESS){
//            agentService.cacheAdd(user);
//        }
        return new R<>(respCode);
    }

    @PutMapping
    public R<Boolean> userUpdate(@RequestBody SysUser userVo) {
        SysUser user = userService.selectById(userVo.getUserId());
        if (user == null) {
            return new R<>(Boolean.FALSE, RespCode.CNSL_OBJ_NOT_FOUND);
        }
        //检查用户信息是否可以修改
        RespCode respCode = userService.checkAgent(userVo,user);
        if (respCode != RespCode.SUCCESS){
            log.warn("userService.checkAgent fail");
            return new R<>(Boolean.FALSE, respCode);
        }
        boolean updateResult = userService.updateAgent(userVo, user.getUsername());
        if (!updateResult) {
            return new R<>(Boolean.FALSE, RespCode.IME_DB_FAIL);
        }
        return new R<>(Boolean.TRUE, RespCode.SUCCESS);
    }

    @DeleteMapping("/{id}")
    public R<Boolean> userDel(@PathVariable("id") String[] userIds) {

        List<String> usernameList = userService.checkAgentInUse(userIds);
        if (usernameList==null){
            return new R<>(RespCode.CNSL_IN_USE);
        }else{
            if (userService.batchDeleteUser(userIds)){
                usernameList.forEach(username->agentService.delete(username));
                return new R<>(Boolean.TRUE);
            }
        }
        return new R<>(Boolean.FALSE);
    }

    @GetMapping("/{id}")
    public R<SysUser> getAgent(@PathVariable Integer id){
        return new R<>(Boolean.TRUE, userService.selectById(id));
    }

    /**
     *
     * 获取agent的队列
     * @param agentId
     * @return
     */
    @GetMapping("{agentId}/queue")
    public R<List> getQueueByAgentId(@PathVariable Integer agentId){
        return new R<>(Boolean.TRUE,queueService.getQueueByAgentId(agentId));
    }

    /**
     *
     *后台管理员强制修改agent状态
     * 在agent为在线情况下可以修改为其他可执行状态
     * 在agent离线状态下不可改变
     * @param params
     * @return
     */
    @PutMapping("changeStatus")
    public R changeAgentStatus(@RequestBody Map<String, Object> params)throws GrgException{

        String status = CommonUtils.getMappingParams(params,"status");
        String agentUserId = CommonUtils.getMappingParams(params,"userId");
        log.info("changeAgentStatus userId:{},status:{}",agentUserId,status);
        if (userService.selectById(agentUserId)==null){
            return new R(RespCode.ACD_AGENT_NOT_EXIST);
        }
        Agent agentById = agentService.getAgentById(agentUserId);
        if (agentById==null){
            return new R(RespCode.ACD_AGENT_NOT_LOGIN);
        }
        boolean flag = false;
        if (AgentStatus.OFFLINE.name().equals(status)) {
            agentService.logout(agentById,HangupReason.FROM_SERVER);
        } else {
            flag = agentService.setStatusStr(agentById, status);
        }

        return new R<>(flag);
    }

}
