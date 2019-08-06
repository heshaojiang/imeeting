package com.grgbanking.grgacd.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.util.exception.GrgException;
import com.grgbanking.grgacd.common.*;
import com.grgbanking.grgacd.config.AcdConfig;
import com.grgbanking.grgacd.dto.QueueStrategyVo;
import com.grgbanking.grgacd.mapper.AcdAgentQueueMapper;
import com.grgbanking.grgacd.mapper.AcdQueueMapper;
import com.grgbanking.grgacd.mapper.SysUserMapper;
import com.grgbanking.grgacd.model.AcdAgentQueue;
import com.grgbanking.grgacd.model.AcdCalls;
import com.grgbanking.grgacd.model.AcdQueue;
import com.grgbanking.grgacd.model.SysUser;
import com.grgbanking.grgacd.queue.QueueManageFactory;
import com.grgbanking.grgacd.queue.QueueStrategy;
import com.grgbanking.grgacd.redisevent.AgentQueueListener;
import com.grgbanking.grgacd.redisevent.RedisListenerConfig;
import com.grgbanking.grgacd.service.*;
import com.xiaoleilu.hutool.date.DateUnit;
import com.xiaoleilu.hutool.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 坐席队列 服务实现类
 * </p>
 *
 * @author tjshan
 * @since 2019-05-05
 */
@Service
@Slf4j
public class AcdQueueServiceImpl extends ServiceImpl<AcdQueueMapper, AcdQueue> implements AcdQueueService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private  AcdConfig acdConfig;

    Map<String, QueueManager> mapAcdQueues = new HashMap<>();
    @Autowired
    private  AcdQueueMapper queueMapper;
    @Autowired
    private  SysUserMapper sysUserMapper;
    @Autowired
    private  AcdAgentQueueMapper agentQueueMapper;

    @Autowired
    private AgentService agentService;
    @Autowired
    private CallerService callerService;
    @Autowired
    private AcdCallsService acdCallsService;
    @Autowired
    private AcdNotificationService acdNotificationService;
    @Autowired
    private RedisListenerConfig redisListenerConfig;
    public void deleteByPrex(String prex) {
        try {
            Set<String> keys = redisTemplate.keys(prex);
            if (!CollectionUtils.isEmpty(keys)) {
                redisTemplate.delete(keys);
            }
        } catch ( Exception e ) {

        }
     }

    /**
     * 初始化所有的队列到缓存中
     */
//    @PostConstruct
    public void initQueue() {
        List<AcdQueue> acdAgentQueues = queueMapper.selectList(new EntityWrapper<>());
        log.info("initQueue acdAgentQueues:{}",acdAgentQueues.size());
        //启动时，清理缓存里的所有记录
        //FIXME 多服务部署时不能执行此操作
        deleteByPrex(Constants.KEY_QUEUE+"*");

        for (AcdQueue acdQueue : acdAgentQueues) {
            addQueueManager(acdQueue);
        }
        log.info("initQueue end");
    }
    @Override
    @Transactional
    public Boolean addMembers(String queueId,Integer capacity, List<Integer> agentIds) {

        int count=0;
        if (CollectionUtils.isEmpty(agentIds)){
            return false;
        }

        for (Integer agentId:agentIds) {
            AcdAgentQueue acdAgentQueue=new AcdAgentQueue(agentId,queueId);
            agentQueueMapper.insert(acdAgentQueue);
            count++;

        }
        Query query=new Query(new HashMap<>(0));

        if (capacity !=null){
            query.setSize(capacity);
        }
        List<SysUser> users = sysUserMapper.selectAgentByQueueId(query, queueId);
        users.forEach(user -> agentService.agentSetQueues(user.getUsername(),user.getAcdQueues()));
        return count==agentIds.size();
    }

    @Override
    public Boolean deleteMembers(String queueId, List<String> agentIds, boolean all) {
        int count=0;
        if (CollectionUtils.isEmpty(agentIds) && !all){
            return false;
        }
        List<Agent> agentList = getAgentByQueueId(queueId);
        //判断是否全部删除
        if(all){

            for (Agent agent:agentList){
                agent.getAcdQueues().remove(queueId);
                agentService.cacheSave(agent);
            }
        }else{
            List<Agent> agents = agentList.stream().filter(agent -> agentIds.indexOf(agent.getAgentId()) != -1).collect(Collectors.toList());
            for (Agent agent:agents){
                agent.getAcdQueues().remove(queueId);
                agentService.cacheSave(agent);
            }
        }
        cleanAgentQueue(queueId);
        return  count==agentIds.size();
    }

    @Override
    public Boolean addQueue(AcdQueue acdQueue) {
        Integer count = queueMapper.insert(acdQueue);
        addQueueManager(acdQueue);
        if (count==1){
            this.addMembers(acdQueue.getQueueId(),acdQueue.getMaxNum(),acdQueue.getAgentList());
            return true;
        }
        return false;
    }

    @Override
    public Boolean updateQueue(AcdQueue acdQueue) {
        Integer count= queueMapper.updateById(acdQueue);
        if (count==1){
            this.deleteMembers(acdQueue.getQueueId(), Collections.emptyList(),true);
            this.addMembers(acdQueue.getQueueId(),acdQueue.getMaxNum(),acdQueue.getAgentList());
            return true;
        }
        return false;
    }

    @Override
    public AcdQueue getOneQueue(String queueId) {
        AcdQueue acdQueue = queueMapper.selectById(queueId);
        List<SysUser> users = sysUserMapper.selectAgentByQueueId(new Query(new HashMap<>()), queueId);
        acdQueue.setAgentLists(users);
        return acdQueue;
    }

    @Override
    public Page getQueuePage(Query<Object> query) {
        List<Object> queuePage = queueMapper.selectQueuePage(query, query.getCondition());

        query.setRecords(queuePage);
        return query;
    }

    @Override
    public Page getQueueMembers(String queueId, Query<SysUser> query) {
        List<SysUser> users = sysUserMapper.selectAgentByQueueId(query, queueId);
        query.setRecords(users);
        return query;
    }

    @Override
    public Page getQueueMembersOnline(String queueId, Query<Agent> query) {
        //当前redis中存放的都是在线的agent
        List<Agent> agentList = agentService.getAgentList();
        query.setRecords(agentList);
        query.setSize(agentList.size());

        return query;
    }

    @Override
    @Transactional
    public RespCode deleteQueue(String[] queueIds) {
        List<String> queueIdList = Arrays.asList(queueIds);
        for (String queueId: queueIds){
         if ( checkQueueInUse(queueId)){
             return RespCode.CNSL_IN_USE;
         }
        }
        Integer count1 = queueMapper.deleteBatchIds(Arrays.asList(queueIds));

        queueIdList.forEach(queueId->{
            mapAcdQueues.remove(queueId);
            this.cleanAgentQueue(queueId);
            this.deleteMembers(queueId,Collections.emptyList(),true);
        });

        if (count1==queueIds.length){
            return RespCode.SUCCESS;
        }

        return RespCode.FAIL;
    }

    @Override
    public Boolean checkQueueInUse(String queueId) {
        AgentService agentService= SpringContextUtils.getBean(AgentService.class);
        Query query=new Query(new HashMap<>());
        List<SysUser> sysUsers = sysUserMapper.selectAgentByQueueId(query, queueId);
        List<String> collect = sysUsers.stream().map(SysUser::getUsername).collect(Collectors.toList());
        for (String username : collect){
            if (agentService.checkUseWithName(username)){
                return true;
            }
        }
        return false;
    }

    @Override
    public QueueManager addQueueManager(AcdQueue acdQueue) {
        QueueManager queueManager = QueueManageFactory.getQueueManager(acdQueue);
        mapAcdQueues.put(acdQueue.getQueueId(), queueManager);
        redisListenerConfig.addMessageListener(AgentQueueListener.getInstance(this),redisListenerConfig.zSetAddTopics);//注册监听
        return queueManager;
    }

    @Override
    public QueueManager getQueue(String queueID) {
        if (StringUtils.isEmpty(queueID)){
            queueID = Constants.DEF_QUEUE_ID;
        }

        QueueManager queueManager = mapAcdQueues.get(queueID);
        if (queueManager == null) {
            //mapAcdQueues 中没有，则从数据库中获取
            /*EntityWrapper entityWrapper=new EntityWrapper();
            entityWrapper.eq("queue_id",queueID);
            Integer queueCount = queueMapper.selectCount(entityWrapper);
            if (queueCount>=1){
                //有此队列，则创建队列管理器
                queueManager = addQueue(queueID);
            }*/
            AcdQueue acdQueue = queueMapper.selectById(queueID);
            if(null != acdQueue){
                addQueueManager(acdQueue);
            }
        }
        return queueManager;
    }

    @Override
    public List<AcdQueue> getQueueByAgentId(Integer agentId) {
        return queueMapper.selectQueueByAgentId(agentId);
    }

    /**
     * 清除客服与队列之间的关系（database）
     * @param queueId 队列id
     * @return
     */
    private void cleanAgentQueue(String queueId){
        EntityWrapper<AcdAgentQueue> wrapper=new EntityWrapper<>();
        wrapper.eq("queue_id",queueId);
        agentQueueMapper.delete(wrapper);
    }

    @Override
    public List<QueueStrategyVo> getQueueStrategyList() {
        List<QueueStrategyVo> list=new ArrayList<>();
        for (QueueStrategy strategy:QueueStrategy.values()){
            QueueStrategyVo strategyVo=new QueueStrategyVo();
            strategyVo.setType(strategy.getType());
            strategyVo.setLabel(strategy.getLabel());
            list.add(strategyVo);
        }

        return list;
    }


    private List<Agent> getAgentByQueueId(String queueId){
        List<Agent> agentList = agentService.getAgentList();
        List<Agent> collect = agentList.stream().filter(agent -> agent.getAcdQueues().contains(queueId)).collect(Collectors.toList());
        return collect;
    }

    @Override
    public void hangupCall(String callId,String clientId,String clientType, String reason) throws GrgException {
        log.info("hangupCall callId:{},clientId:{},clientType:{},reason:{}",callId,clientId,clientType,reason);
        if (StringUtils.isEmpty(clientId)) {
            log.warn("param Error clientId:{}", clientId);
            throw new GrgException(RespCode.IME_INVALIDPARAMETER);
        }
        if (!StringUtils.isEmpty(callId)) {

            AcdCalls acdCalls = acdCallsService.getCallById(callId);
            if (acdCalls == null) {
                log.warn("call:{} is not exist", callId);
                throw new GrgException(RespCode.ACD_CALL_NOT_EXIST);
            }
            String queueId = acdCalls.getQueueId();
            String callerClientId = acdCalls.getCallerClientId();
            String agentClientId = acdCalls.getAgentClientId();

            //检查clientId是否为通话中的成员
            String callClientId = null;
            switch (clientType) {
                case Constants.CLIENT_TYPE_CALLER:
                    callClientId = callerClientId;
                    break;
                case Constants.CLIENT_TYPE_AGENT:
                    callClientId = agentClientId;
                    break;
            }
            log.info("hangupCall check clientId callClientId:{},reason:{}",callClientId,reason);
            if (!clientId.equals(callClientId)) {
                //当前clientId不是通话中的Caller或agent
                throw new GrgException(RespCode.ACD_CALL_VERIFY_FAIL);
            }


            log.info("hangup queueId:{},callerClientId:{},agentClientId:{}",queueId,callerClientId,agentClientId);

            // 设置队列状态
            CallStatus callStatus;
            switch (reason){
                case HangupReason.FROM_CALLER:
                case HangupReason.FROM_AGNET:
                {
                    if (acdCalls.getAnswerTime() == null){
                        //如果没有answer时间，则通话未接通成功，是agent发起hangup的则为拒接通话，caller时则为取消呼叫
                        callStatus = reason.equals(HangupReason.FROM_AGNET) ? CallStatus.CALL_REJECT:CallStatus.CALL_CANCEL;
                    } else {
                        //正常结束通话
                        callStatus = CallStatus.HANGUP;
                    }
                }
                break;
                case HangupReason.TIME_OUT_makecall:
                case HangupReason.TIME_OUT_ringing:
                    callStatus = CallStatus.TIMEOUT;
                    break;
                default:
                    callStatus = CallStatus.ERROR;
            }

            //更新通话记录
            acdCallsService.setStatus(callId,callStatus);
            //删除缓存记录
            acdCallsService.deleteCache(callId);
            //如果已经有agent接听，则更新agent状态
            if (!StringUtils.isEmpty(agentClientId)) {
                agentService.setStatus(agentClientId,AgentStatus.ACW,queueId);
            }
            acdNotificationService.sendHangupEvent(callId,reason);

            callerService.removeQueue(callerClientId,queueId,Constants.REMOVE_QUEUE_TYPE_HANGUP);
            //记录客服服务的时间 add by hsjiang on 2019/7/18
            agentService.recordAgentServiceTime(acdCalls.getAgentClientId(),getServiceTime(acdCalls,callStatus));
        } else if (Constants.CLIENT_TYPE_CALLER.equals(clientType)) {
            // callid 为空时，直接从Caller中获取queueId进行移出队列操作
            log.info("Is Caller HangupCall But Callid is empty!");
            callerService.removeQueue(clientId,null,Constants.REMOVE_QUEUE_TYPE_HANGUP);
        } else {
            log.warn("Is Agent HangupCall But Callid is empty!");
            throw new GrgException(RespCode.IME_INVALIDPARAMETER);
        }

    }

    /**
     * 获取服务时长
     * @param call 通话记录对象
     * @param callStatus 通话状态
     * @return
     * @author hsjiang
     * @date 2019/7/26/026
     **/
    private long getServiceTime(AcdCalls call,CallStatus callStatus) {
        if(callStatus == CallStatus.HANGUP){//添加挂断时间，本来是有的
            call.setHangupTime(new Date());
        }
        else{
            return 0;
        }
        long serviceSecond = 0;
        if(call != null){
            if(call.getAgentClientId()!= null && call.getAnswerTime()!= null && call.getHangupTime()!= null){
                serviceSecond = DateUtil.between(call.getAnswerTime(),call.getHangupTime(), DateUnit.SECOND);//计算本次服务的时间，单位（秒）
            }
        }
        return serviceSecond;
    }
    @Override
    public void setCallerReCall(String callId) throws GrgException {
        log.info("setCallerReCall callId:{}",callId);
        if (StringUtils.isEmpty(callId)) {
            log.warn("param Error callId:{}", callId);
            throw new GrgException(RespCode.IME_INVALIDPARAMETER);
        }
        AcdCalls calls = acdCallsService.getCallById(callId);
        if (calls == null) {
            log.warn("call:{} is not exist", callId);
            throw new GrgException(RespCode.ACD_CALL_NOT_EXIST);
        }
        String callerClientId = calls.getCallerClientId();
        String queueId = calls.getQueueId();
        log.info("setCallerReCall callerClientId:{} queueId:{}",callerClientId,queueId);
        QueueManager queueManager = getQueue(queueId);
        if (queueManager == null) {
            log.warn("The queue:{} is not exist", queueId);
            throw new GrgException(RespCode.ACD_QUEUE_NOT_EXIST);
        }
        //判断当前的客户，是否在pending队列中。
        if (queueManager.caller_InPendingQueue(callerClientId)){
            //删除caller的等待
            queueManager.caller_removeQueue(callerClientId,Constants.REMOVE_QUEUE_TYPE_RECALL);

            //重新加入排队队列，并设置为到队首
            queueManager.caller_addQueue_head(callerClientId);
        } else {
            log.warn("queue:{} caller:{} not find in pending list.",queueManager.getQueueId(),callerClientId);
        }
    }


}
