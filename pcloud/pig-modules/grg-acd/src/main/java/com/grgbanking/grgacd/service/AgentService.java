package com.grgbanking.grgacd.service;


import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pig.common.constant.MeetingConstant;
import com.github.pig.common.security.util.TokenUtil;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.util.exception.GrgException;
import com.github.pig.common.vo.UserVo;
import com.grgbanking.grgacd.common.*;
import com.grgbanking.grgacd.config.AcdConfig;
import com.grgbanking.grgacd.mapper.SysUserMapper;
import com.grgbanking.grgacd.model.AcdCalls;
import com.grgbanking.grgacd.model.SysUser;
import com.xiaoleilu.hutool.date.DateUnit;
import com.xiaoleilu.hutool.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author wjqiu
 * @date 2019-04-24
 */
@Slf4j
@Service
public class AgentService extends ServiceImpl<SysUserMapper, SysUser> {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AcdNotificationService acdNotificationService;
    @Autowired
    private AcdConfig acdConfig;
    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    SysUserMapper userMapper;
    @Autowired
    private AcdQueueService acdQueueService;

    @Autowired
    private UserService userService;

    @Autowired
    private AgentStatusHistoryService agentStatusHistoryService;

    /**
     * 所有agent详细信息的保存列表
     * agent每次login的时候加入此队列中，logout时移出队列
     * 使用clientId作为key。因为，1、websocket通道关闭时需要使用clientId作为关闭标记，2、可以支持同一agent在不同终端上登录并服务。
     */
    private RedisUtil<Agent> cacheAllAgentList;
    @PostConstruct
    private void init(){
        //启动时，先清空所有客服列表
        cacheAllAgentList = new RedisUtil<>(redisTemplate,Constants.KEY_AGENT);
        cacheRemoveAll();
    }

    public Agent login(String clientId,String access_token) throws GrgException {
        log.info("login clientId:{} access_token:{}",clientId,access_token);
        if (StringUtils.isEmpty(clientId)){
            log.warn("parameter error:clientId is required.");
            throw new GrgException(RespCode.IME_INVALIDPARAMETER);
        }
        if (StringUtils.isEmpty(access_token) || !tokenUtil.checkToken(access_token)) {
            log.warn("Agent login access_token check failed! access_token:{}",access_token);
            throw new GrgException(RespCode.IME_UNAUTHORIZED);
        }
        Agent agent = cacheGet(clientId);
        if (agent != null){
            log.info("agent already login clientId:{},agentName:{},state:{}",clientId,agent.getAgentName(),agent.getStatus());
            acdNotificationService.sendAgentStatusChange(agent.getClientId(),agent.getStatus(),agent.getStatusLast());
//            setStatus(agent,AgentStatus.ONLINE);
        } else {
            String agentName = "";
            String role = "";

            UserVo userVo = UserUtils.getUserVoFromToken(access_token);
            if (userVo != null) {
                agentName = userVo.getUsername();
                role = userVo.getRole();
            }

            log.info("new agent login add cache clientId:{} agentName:{} role:{}",clientId,agentName,role);
            if (StringUtils.isEmpty(agentName)) {
                throw new GrgException(RespCode.ACD_AGENT_NOT_EXIST);
            }
            if (!MeetingConstant.USER_ROLE_Agent.equals(role)){
                throw new GrgException(RespCode.ACD_AGENT_ROLE_ERROR);
            }

            SysUser sysUser = userService.getByName(agentName);
            if (sysUser != null) {
                agent = cacheAdd(clientId,sysUser);
                setStatus(clientId,AgentStatus.ONLINE);
            } else {
                log.warn("Agent agentName:{} not exist!",agentName);
                throw new GrgException(RespCode.ACD_AGENT_NOT_EXIST);
            }

        }
        return agent;
    }
    public boolean logout(String clientId) throws GrgException {
        return logout(clientId,HangupReason.FROM_AGNET);
    }
    public boolean logout(Agent agent,String reason) throws GrgException {
        String callId = agent.getCallId();
        String clientId = agent.getClientId();
        log.info("Agent logout clientId:{},reason:{}",clientId,reason);
        if (StringUtils.isEmpty(clientId)) {
            log.warn("Agent logout clientId is Empty");
            throw new GrgException(RespCode.ACD_AGENT_NOT_LOGIN);
        }
        setStatus(agent,AgentStatus.OFFLINE);

        if (StringUtils.isNotEmpty(callId)) {
            //有Caller正在呼叫此Agent时
            if (AgentStatus.SELECTED.name().equals(agent.getStatus())){
                // agent状态为SELECTED时，即Caller在pengding队列中等待agent接听的时候。
                // 如果agent断线，或者异常退出时，则通话未建立成功。需要把Caller重新进行排队。
                acdQueueService.setCallerReCall(callId);
            }

            hangupCall(callId,clientId,reason);
        }


        cacheRemove(clientId);
        return true;

    }
    public boolean logout(String clientId,String reason) throws GrgException {
        log.info("Agent logout clientId:{},reason:{}",clientId,reason);
        Agent agent = cacheGet(clientId);
        if (agent == null) {
            log.warn("Agent:{} is not exist", clientId);
            throw new GrgException(RespCode.ACD_AGENT_NOT_EXIST);
        }
        return logout(agent,reason);
    }

    public boolean cacheSave(Agent agent){
        log.debug("cacheSave agent:{}",agent);
        if (agent != null) {
            cacheAllAgentList.addMap(agent.getClientId(),agent);
            return true;
        } else {
            return false;
        }
    }
    public boolean setStatus(Agent agent, AgentStatus status) throws GrgException {
        return setStatus(agent,status,null);
    }
    public boolean setStatusStr(Agent agent, String status) throws GrgException {
        AgentStatus agentStatus = AgentStatus.valueOf(status);
        return setStatus(agent,agentStatus,null);
    }
    public boolean setStatus(Agent agent, AgentStatus status, String callId) throws GrgException {
        if (agent != null) {
            String lastStatus = agent.getStatus();
            String agentName = agent.getAgentName();
            String clientId = agent.getClientId();
            log.info("set agent:{}<{}> setStatus {} lastStatus:{}",agentName,clientId,status,lastStatus);
            if (AgentStatus.OFFLINE.name().equals(lastStatus) &&
                    !AgentStatus.ONLINE.name().equals(status.name())) {
                log.warn("agent is OFFLINE, only setStatus to ONLINE!");
                // Agent未登录时，只能是SET ONLINE才能设置状态，否则，不给执行操作
                throw new GrgException(RespCode.ACD_AGENT_NOT_LOGIN);
            }
            if (StringUtils.isEmpty(clientId)) {
                log.info("setStatus clientId is Empty");
                throw new GrgException(RespCode.ACD_AGENT_NOT_LOGIN);
            }

            Set<String> acdQueues = agent.getAcdQueues();
            //检查Agent是否在队列中
            if (AgentStatus.AVAILABLE.name().equals(status) && acdQueues.size() == 0) {
                log.info("setStatus AVAILABLE but Agent not in Any Queue!");
                throw new GrgException(RespCode.ACD_AGENT_NOT_INQUEUE);
            }
            //遍历客服所在的所有队列，设置队列中的状态
            for (String queueID: acdQueues){
                QueueManager queueManager = acdQueueService.getQueue(queueID);
                if (queueManager != null){
                    if (AgentStatus.AVAILABLE == status){
                        //AVAILABLE)状态时把agent加入到排队中
                        log.info("set agent:{} {} Add to Queue:{}",agentName,status.name(),queueManager.getQueueId());
                        queueManager.agent_addQueue(clientId);
                    } else {
                        //其他状态都需要从排队队列中移除
                        log.info("set agent:{} {} remove from Queue:{}",agentName,status.name(),queueManager.getQueueId());
                        queueManager.agent_removeQueue(clientId);
                    }
                }
            }
            //保存上一次设备状态变更状态与时间。用于做统计时使用
            agent.setStatusLast(lastStatus);
            agent.setStatusTimeLast(agent.getStatusTime());

            agent.setStatus(status.name());
            agent.setStatusTime(new Date());

            //agent不在通话状态，则清空callid
            if (status != AgentStatus.SELECTED && status != AgentStatus.SERVICE){
                agent.setCallId("");
            }
            cacheSave(agent);

            acdNotificationService.sendAgentStatusChange(agent.getClientId(),status.name(),lastStatus);

            //插入历史状态变更表
            if(!StringUtils.isEmpty(lastStatus) && !lastStatus.equals(status.name())){
                agentStatusHistoryService.insertStatusHistory(agent,callId);
            }
            return true;
        } else {
            return false;
        }
    }
    /**
     * 更新agent 状态 需要在登陆之后使用
     * @param clientId
     * @param status
     * @return
     */
    public boolean setStatus(String clientId, AgentStatus status, String queueId) throws GrgException {
        Agent agent = cacheGet(clientId);
        boolean flag = false;
        if (agent != null){
            flag = setStatus(agent,status,queueId);
        }
        return flag;
    }
    public boolean setStatus(String clientId, AgentStatus status) throws GrgException {
        return setStatus(clientId,status,null);
    }
    public String getStatus(String clientId){
        Agent agent = cacheGet(clientId);
        if (agent != null){
            return agent.getStatus();
        } else {
            return null;
        }
    }
    public String getStatusWithName(String agentName){

        for (String clientId:getClientIdWithUsername(agentName)){
            return getStatus(clientId);
        }
        return AgentStatus.OFFLINE.name();
    }
    public Agent cacheGet(String clientId){
        if (StringUtils.isEmpty(clientId)){
            return null;
        } else {
            return cacheAllAgentList.getMapField(clientId);
        }
    }

    /**
     * 初始化 agent信息并保存到缓存
     * @param user user
     */
    public Agent cacheAdd(String clientId,SysUser user){
        log.info("cacheAdd clientId:{} name:{}",clientId,user.getUsername());
        Agent agent = new Agent();
        agent.setAgentId(user.getUserId().toString());
        agent.setClientId(clientId);
        agent.setAgentName(user.getUsername());
        agent.setAcdQueues(user.getAcdQueues());
        agent.setStatus(AgentStatus.OFFLINE.name());
        cacheSave(agent);
        return agent;
    }
    public void cacheRemove(String clientId) throws GrgException {
        if (StringUtils.isNotEmpty(clientId)) {
            log.info("cacheRemove clientId:{}",clientId);
//            Agent agent = cacheGet(clientId);
//            if (agent != null){
//                setStatus(agent,AgentStatus.OFFLINE);
//            } else {
//                log.warn("Agent clientId:{} can not find in cache!",clientId);
//                throw new GrgException(RespCode.ACD_AGENT_NOT_LOGIN);
//            }
            cacheAllAgentList.removeMapField(clientId);
        }
    }
    public void cacheRemoveAll(){
        cacheAllAgentList.removeAll();
    }
    // 通过用户名获取 clientId
    public List<String> getClientIdWithUsername(String agentName){
        List<String> clientIdList = new ArrayList<>();
        if (StringUtils.isNotEmpty(agentName)) {
            List<Agent> agentList = getAgentList();
            for (Agent agent:agentList) {
                if (agentName.equals(agent.getAgentName())) {
                    clientIdList.add(agent.getClientId());
                }
            }
        }
        return clientIdList;
    }

    /**
     * 删除agent
     * @param agentName 客服名称
     * @return boolean
     */
    public boolean agentSetQueues(String agentName, Set<String> acdQueues){
        //通过agentName来设置Queue
        for (String clientId:getClientIdWithUsername(agentName)){
            Agent agent = cacheGet(clientId);
            if (agent != null){
                agent.setAcdQueues(acdQueues);
                log.info("agentSetQueues clientId:{},acdQueues:{}",clientId,acdQueues);
            }
            cacheSave(agent);
        }
        return true;
    }


    /**
     * 删除agent
     * @param agentName 客服名称
     * @return boolean
     */
    public boolean delete(String agentName){
        for (String clientId:getClientIdWithUsername(agentName)){
            if (!checkUse(clientId)){
                cacheAllAgentList.removeMapField(clientId);
            } else {
                return false;
            }
        }
        return true;
    }
    /**
     * 检查客服是否正在使用中
     * @param agentName 客服名称
     * @return true-正在使用 false-没有使用
     */
    public boolean checkUseWithName(String agentName){
        for (String clientId:getClientIdWithUsername(agentName)){
            if (checkUse(clientId)){
                return true;
            }
        }
        return false;
    }
    /**
     * 检查客服是否正在使用中
     * @param clientId 客服名称
     * @return true-正在使用 false-没有使用
     */
    public boolean checkUse(String clientId){
        Agent agent = cacheGet(clientId);
        if(agent==null){
            return false;
        }

        return !agent.getStatus().equals(AgentStatus.OFFLINE.name());
    }

    /**
     * 获取agent 列表
     * 所有在redis中的agent都是在线的
     * @return
     */
    public List<Agent> getAgentList(){
        List<Agent> mapFieldValues = cacheAllAgentList.getMapFieldValues();
        return mapFieldValues;
    }

    /**
     * 通过agentId获取agent
     * @param agentId
     * @return
     */
    public Agent getAgentById(String agentId){
        Agent agent1 = getAgentList().stream().filter(agent -> agent.getAgentId().equals(agentId)).findFirst().orElse(null);
        return agent1;
    }

    /**
     * 获取agent的在线人数
     * @return
     */
    public int getAgentOnlineCount(){
        return getAgentList().size();
    }

    /**
     * 获取某种或某几种状态的客服人数，如果参数为空，则全部返回
     * @param status
     * @return
     */
    public int getAgentCountWithStatus(AgentStatus ...status){
        List<Agent> agentList = this.getAgentList();
        if (status==null){

            return agentList.size();
        }
        long count = agentList.stream().filter(agent -> {
            for (AgentStatus s : status) {
                if (s.name().equals(agent.getStatus())) {
                    return true;
                }
            }
            return false;
        }).count();
        return (int) count;
    }

    /**
     * 检查指定的clientId是否为当前登录的Agent
     * @param agent
     * @return
     */
    public boolean verifyCurAgent(Agent agent) throws GrgException {
        if (agent == null) {
            log.warn("agent == null");
            throw new GrgException(RespCode.ACD_AGENT_NOT_LOGIN);
        }
        String userName = UserUtils.getUser();
        if (StringUtils.isEmpty(userName)) {
            log.warn("userName is empty agent not login.");
            throw new GrgException(RespCode.ACD_AGENT_VERIFY_FAIL);
        } else if (!userName.equals(agent.getAgentName())) {
            log.warn("userName:{} not equals agentName:{}",userName,agent.getAgentName());
            throw new GrgException(RespCode.ACD_AGENT_VERIFY_FAIL);
        }
        return true;
    }
    public boolean verifyCurAgent(String clientId) throws GrgException {
        Agent agent = cacheGet(clientId);
        return verifyCurAgent(agent);
    }
    public String getCallIdFromClinetId(String clientId){
        Agent agent = cacheGet(clientId);
        if (agent == null) {
            log.warn("Agent:{} is not exist", clientId);
            throw new GrgException(RespCode.ACD_AGENT_NOT_EXIST);
        }
        log.info("getCallIdFromClinetId clientId:{} CallId:{}", clientId,agent.getCallId());
        return agent.getCallId();
    }
    public boolean hangupCall(String clientId,String reason) throws GrgException {
        log.info("Agent hangupCall clientId:{} reason:{}",clientId,reason);
        String callId = getCallIdFromClinetId(clientId);
        return hangupCall(callId,clientId,reason);
    }
    public boolean hangupCall(String callId,String clientId,String reason) throws GrgException {
        log.info("Agent hangupCall callId:{} clientId:{} reason:{}",callId,clientId,reason);
        if (StringUtils.isEmpty(callId)) {
            callId = getCallIdFromClinetId(clientId);
        }
        acdQueueService.hangupCall(callId,clientId,Constants.CLIENT_TYPE_AGENT,reason);
        return true;
    }

    /**
     * 记录客服服务时长
     * @param clientId 坐席clientId
     * @return
     * @author hsjiang
     * @date 2019/7/18/018
     **/
    public void recordAgentServiceTime(String clientId,long serviceSecond){
        if(serviceSecond>0){
            Agent agent = this.cacheGet(clientId);
            if(agent != null){
                Long serviceTime = agent.getServiceTime();
                serviceTime = serviceTime == null?0:serviceTime;
                serviceTime += serviceSecond;
                agent.setServiceTime(serviceTime);
                this.cacheSave(agent);
            }
            log.info("this service is over, clientId:{} serviceTime(s):{}",clientId,serviceSecond);
        }

    }
}
