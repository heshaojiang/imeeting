package com.grgbanking.grgacd.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pig.common.util.Query;
import com.grgbanking.grgacd.common.Agent;
import com.grgbanking.grgacd.common.CallStatus;
import com.grgbanking.grgacd.common.Constants;
import com.grgbanking.grgacd.common.RedisUtil;
import com.grgbanking.grgacd.dto.CallDayDto;
import com.grgbanking.grgacd.dto.CallMinuteDto;
import com.grgbanking.grgacd.dto.ChatRecord;
import com.grgbanking.grgacd.mapper.AcdCallsMapper;
import com.grgbanking.grgacd.model.AcdCalls;
import com.grgbanking.grgacd.service.AcdCallsService;
import com.xiaoleilu.hutool.bean.BeanUtil;
import com.xiaoleilu.hutool.bean.copier.CopyOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 通话记录表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-05
 */
@Slf4j
@Service
@CacheConfig( cacheNames = Constants.KEY_CALL_LINE)
public class AcdCallsServiceImpl extends ServiceImpl<AcdCallsMapper, AcdCalls> implements AcdCallsService {

    @Autowired
    private AcdCallsMapper acdCallsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    private final static String CALL_STATUS_SUCCESS = "success";
    private final static String CALL_STATUS_TIMEOUT = "timeout";
    private final static String CALL_STATUS_OTHER = "other";

    private final static String FROM_DATE = "fromDate";
    private final static String TO_DATE = "toDate";


    @CachePut(key = "#calls.callId")
    @Override
    public AcdCalls addCall(AcdCalls calls) {
        acdCallsMapper.insert(calls);
        return calls;
    }

    @Override
    public Page<CallDayDto> getAvgAnswerSpeed(Query<CallDayDto> query) {
        query.getCondition().put("from_time","makecall_time");
        query.getCondition().put("to_time","answer_time");
        query.getCondition().put("queue_id",false);
        List<CallDayDto> callDtos = acdCallsMapper.avgDayTime(query, query.getCondition());
        query.setRecords(callDtos);
        return query;
    }

    @Override
    public Page<CallDayDto> getAbandonTime(Query<CallDayDto> query) {
        query.getCondition().put("from_time","makecall_time");
        query.getCondition().put("to_time","hangup_time");
        query.getCondition().put("queue_id",false);
        query.getCondition().put("call_status",CallStatus.TIMEOUT.name());
        List<CallDayDto> callDtos = acdCallsMapper.avgDayTime(query, query.getCondition());
        query.setRecords(callDtos);
        return query;
    }

    @Override
    public Page<CallDayDto> getNormalTime(Query<CallDayDto> query) {
        query.getCondition().put("from_time","answer_time");
        query.getCondition().put("to_time","hangup_time");
        query.getCondition().put("queue_id",false);
        query.getCondition().put("call_status", CallStatus.CONNECT.name());
        List<CallDayDto> callDtos = acdCallsMapper.avgDayTime(query, query.getCondition());
        query.setRecords(callDtos);
        return query;
    }

    @Override
    public Page<CallDayDto> getAbanCount(Query<CallDayDto> query) {
        query.getCondition().put("queue_id",false);
        query.getCondition().put("call_status",CallStatus.TIMEOUT.name());
        List<CallDayDto> callDtos = acdCallsMapper.sumCount(query, query.getCondition());
        query.setRecords(callDtos);
        return query;
    }

    @Override
    public Page<CallDayDto> getNormalCount(Query<CallDayDto> query) {
        query.getCondition().put("queue_id",false);
        query.getCondition().put("call_status",CallStatus.CONNECT.name());
        List<CallDayDto> callDtos = acdCallsMapper.sumCount(query, query.getCondition());
        query.setRecords(callDtos);
        return query;
    }


    /***************************************************************/

    @Override
    public Page<CallMinuteDto> getMinuteAvgAnswerSpeed(Query<CallMinuteDto> query) {
        query.getCondition().put("from_time","makecall_time");
        query.getCondition().put("to_time","answer_time");
        query.getCondition().put("queue_id",false);
        List<CallMinuteDto> callMinuteDtos = acdCallsMapper.avgMinuteTime(query, query.getCondition());
        query.setRecords(callMinuteDtos);
        return query;
    }

    @Override
    public Page<CallMinuteDto> getMinuteAbandonTime(Query<CallMinuteDto> query) {
        query.getCondition().put("from_time","makecall_time");
        query.getCondition().put("to_time","hangup_time");
        query.getCondition().put("queue_id",false);
        query.getCondition().put("call_status",CallStatus.TIMEOUT.name());
        List<CallMinuteDto> callMinuteDtos = acdCallsMapper.avgMinuteTime(query, query.getCondition());
        query.setRecords(callMinuteDtos);
        return query;
    }

    @Override
    public Page<CallMinuteDto> getMinuteNormalTime(Query<CallMinuteDto> query) {
        query.getCondition().put("from_time","answer_time");
        query.getCondition().put("to_time","hangup_time");
        query.getCondition().put("queue_id",false);
        query.getCondition().put("call_status",CallStatus.CONNECT.name());
        List<CallMinuteDto> callMinuteDtos = acdCallsMapper.avgMinuteTime(query, query.getCondition());
        query.setRecords(callMinuteDtos);
        return query;
    }

    @Override
    public Page<CallMinuteDto> getMinuteAbandCount(Query<CallMinuteDto> query) {
        query.getCondition().put("queue_id",false);
        query.getCondition().put("call_status",CallStatus.TIMEOUT.name());
        List<CallMinuteDto> callMinuteDtos = acdCallsMapper.sumMinuteCount(query, query.getCondition());
        query.setRecords(callMinuteDtos);

        return query;
    }

    @Override
    public Page<CallMinuteDto> getMinuteNormalCount(Query<CallMinuteDto> query) {
        query.getCondition().put("queue_id",false);
        query.getCondition().put("call_status",CallStatus.CONNECT.name());
        String acdTime;
        List<CallMinuteDto> callMinuteDtos = acdCallsMapper.sumMinuteCount(query, query.getCondition());

        if (callMinuteDtos.size()==0){
            query.setRecords(null);
        }else{
            acdTime = callMinuteDtos.get(0).getAcdTime();
            List<CallMinuteDto> last = this.getRecord(acdTime, callMinuteDtos);
            query.setTotal(48);
            query.setSize(48);
            query.setRecords(last);
        }
        return query;
    }

    @Override
    public Boolean batchDeleteCalls(String[] callsIds) {
        Integer count = acdCallsMapper.deleteBatchIds(Arrays.asList(callsIds));
        for (String id:callsIds){
            deleteCache(id);
        }
        return count==callsIds.length;
    }

    @CachePut(key = "#callId")
    @Override
    public AcdCalls setStatus(String callId, CallStatus callStatus) {
        return updateCalls(callId,null,null,callStatus);
    }

    @CachePut(key = "#callId")
    @Override
    public AcdCalls updateCalls(String callId, String queueId, Agent agent, CallStatus callStatus) {
        AcdCalls acdCalls=this.getCallById(callId);
        acdCalls.setCallId(callId);
        switch (callStatus){
            case LINE: log.info("updateCalls calls in LINE"); break;
            case RING: acdCalls.setSelectTime(new Date()); break;
            case CONNECT: acdCalls.setAnswerTime(new Date()); break;
            default:
                acdCalls.setHangupTime(new Date());
                break;
        }

        if (queueId!=null){
            acdCalls.setQueueId(queueId);
        }
        if (agent!=null){
            acdCalls.setAgentId(Integer.valueOf(agent.getAgentId()));
            acdCalls.setAgentClientId(agent.getClientId());
        }
        acdCalls.setCallStatus(callStatus.name());
        acdCallsMapper.updateById(acdCalls);

        return acdCalls;
    }


    @Cacheable(key = "#callId")
    @Override
    public AcdCalls getCallById(String callId) {
        return acdCallsMapper.getCallById(callId);
    }

    @Override
    public Page<AcdCalls> getCallPage(Query<AcdCalls> query) {
        List<AcdCalls> callList = acdCallsMapper.getCallList(query, query.getCondition());
        query.setRecords(callList);
        return query;
    }

    @Override
    public Page<AcdCalls> getCallCurrentPage(Query<AcdCalls> query) {
        List list=Arrays.asList(CallStatus.CONNECT.name(),
                                CallStatus.LINE.name(),
                                CallStatus.RING.name());
        query.getCondition().put("status",list);
        List callListWithStatus = acdCallsMapper.getCallListWithStatus(query, query.getCondition());
        query.setRecords(callListWithStatus);
        return query;
    }
    @Override
    public List<AcdCalls> getCallCurrent() {
        List<AcdCalls> callList = acdCallsMapper.selectList(new EntityWrapper<AcdCalls>()
                .in("call_status",new String[]{CallStatus.CONNECT.name(),
                        CallStatus.LINE.name(),
                        CallStatus.RING.name()})

                );
        return callList;
    }
    @Override
    public Page<Object> getCallStatus(Query<Object> query) throws Exception {
        List list=new ArrayList();
        Map<String, Object> dateMap = this.pageSet(query);
        LocalDate from = LocalDate.parse(dateMap.get(FROM_DATE).toString());
        LocalDate to = LocalDate.parse(dateMap.get(TO_DATE).toString());

        Map<String, Integer>  mapSuccess= getfillStatus(from, to, query, CALL_STATUS_SUCCESS);
        Map<String, Integer> mapTimeout = getfillStatus(from, to, query, CALL_STATUS_TIMEOUT);
        Map<String, Integer> mapOther = getfillStatus(from, to, query, CALL_STATUS_OTHER);

        Map<String,Object> callSuccess=new LinkedHashMap<>();
        Map<String,Object> callTimeout=new LinkedHashMap<>();
        Map<String,Object> callOther=new LinkedHashMap<>();

        callSuccess.put("name",CALL_STATUS_SUCCESS);
        callSuccess.putAll(mapSuccess);
        callTimeout.put("name",CALL_STATUS_TIMEOUT);
        callTimeout.putAll(mapTimeout);
        callOther.put("name",CALL_STATUS_OTHER);
        callOther.putAll(mapOther);
        list.add(callSuccess);
        list.add(callTimeout);
        list.add(callOther);
        query.setRecords(list);
        return query;
    }

    @Override
    public List<String> getStatusFields(Query<Object> query) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        Map<String, Object> objectMap = pageSet(query);
        LocalDate beginDate=LocalDate.parse(objectMap.get(FROM_DATE).toString());
        LocalDate endDate=LocalDate.parse(objectMap.get(TO_DATE).toString());
        List<String> list = new ArrayList<>();
        while (beginDate.isBefore(endDate)) {
            list.add(beginDate.format(formatter));
            beginDate = beginDate.plusDays(1);
        }
        return list;
    }

    @Override
    public Boolean saveChatRecordToCache(ChatRecord chatRecord) {
        RedisUtil<ChatRecord> redisUtil = new RedisUtil<ChatRecord>(redisTemplate, Constants.KEY_CHAT_RECORD);
        redisUtil.pushFromTail(chatRecord);
        return true;
    }

    @CachePut(key = "#calls.callId")
    @Override
    public AcdCalls updateCallById(AcdCalls calls) {
        acdCallsMapper.updateById(calls);
        AcdCalls acdCalls = this.getCallById(calls.getCallId());
        BeanUtil.copyProperties(calls,acdCalls, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        return acdCalls;
    }

    @CacheEvict(key = "#id")
    @Override
    public boolean deleteById(Serializable id) {
        return super.deleteById(id);
    }

    @Override
    public void updateCache(AcdCalls calls) {
        AcdCalls acdCalls = this.getCallById(calls.getCallId());
        BeanUtil.copyProperties(calls,acdCalls, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        //redisUtil.set(Constants.KEY_AGENT_SERVICE_TIME+calls.getCallId(),acdCalls);
    }

    @Override
    public void deleteCache(String callId) {
        //redisUtil.deleteKey(callId);
    }

    @Override
    public Integer getCallingLength() {
        //return redisUtil.keysLength(Constants.KEY_CALL_LINE+"*");
        return 0;
    }

    /**
     * 对分页进行操作，修改时间参数
     * @param query
     * @return
     */
    private Map<String,Object> pageSet(Query<Object> query){
        Map<String, Object> param = query.getCondition();
        LocalDate from,from2 = null;
        LocalDate to,to2 = null;
        Object queryFromDate = param.get(FROM_DATE);
        Object queryToDate = param.get(TO_DATE);
        long between;
        if (queryFromDate!=null){
            from2=LocalDate.parse(queryFromDate.toString());
        }
        if (queryToDate!=null){
            to2=LocalDate.parse(queryToDate.toString());
        }


        if (queryFromDate == null&&queryToDate == null){
            from = LocalDate.now().plusDays(-query.getSize() * query.getCurrent());
            to = LocalDate.now().plusDays(-query.getSize() * (query.getCurrent() - 1));
            between = ChronoUnit.DAYS.between(from, to);


        }else if(queryFromDate != null&&queryToDate != null){
            from=LocalDate.parse(queryFromDate.toString()).plusDays(query.getSize() * (query.getCurrent()-1));
            to=LocalDate.parse(queryFromDate.toString()).plusDays(query.getSize() * query.getCurrent());

            between = ChronoUnit.DAYS.between(from2, to2);
            if(to.isAfter(to2)){
                to=to2;
            }

            query.setTotal((int) between);
        }else if(queryFromDate != null){
            from=LocalDate.parse(queryFromDate.toString()).plusDays(query.getSize() *( query.getCurrent()-1));
            to=LocalDate.parse(queryFromDate.toString()).plusDays(query.getSize() * (query.getCurrent()));
            if(to.isAfter(LocalDate.now())){
                to=LocalDate.now();
            }
             between = ChronoUnit.DAYS.between(from2,LocalDate.now() );

        }else {
            from=LocalDate.parse(queryToDate.toString()).plusDays(-query.getSize() * query.getCurrent());
            to=LocalDate.parse(queryToDate.toString()).plusDays(-query.getSize() * (query.getCurrent()-1));
            between = ChronoUnit.DAYS.between(from, to);
        }
        query.setTotal((int) between);
        param.put(FROM_DATE, from.toString());
        param.put(TO_DATE, to.toString());

        return param;
    }

    private Map<String,Integer> getfillStatus(LocalDate from, LocalDate to, Query<Object> query, String status) throws Exception {
        Query querys=new Query(query.getCondition());
        BeanUtils.copyProperties(query,querys);
        List<String> statusList = new ArrayList<>();
        switch (status) {
            case CALL_STATUS_SUCCESS:
                statusList.add(CallStatus.HANGUP.name());

                break;
            case CALL_STATUS_TIMEOUT:
                statusList.add(CallStatus.TIMEOUT.name());
                break;
            case CALL_STATUS_OTHER:
                statusList.add(CallStatus.RING.name());
                statusList.add(CallStatus.LINE.name());
                statusList.add(CallStatus.CONNECT.name());
                break;
            default:
                throw new Exception("The args is error ");
        }
        querys.getCondition().put("status", statusList);
        querys.setCurrent(1);
        List<Map<String, Object>> callCountWithStatus = acdCallsMapper.getCallCountWithStatus(querys, querys.getCondition());
        Map<String, Integer> map = new LinkedHashMap<>();
        callCountWithStatus.forEach(stringIntegerMap -> map.put(stringIntegerMap.get("key").toString(), Integer.parseInt(stringIntegerMap.get("value").toString())));
        Map<String, Integer> completeMap = this.fillCallStatus(from, to, map);
        return completeMap;
    }

    private Integer[] fillArray(int number){

        Integer array[]=new Integer[number];
        for (int i=0;i<24;i++){
            array[i]=i;
        }
        return  array;
    }

    private Map<String, Integer> fillCallStatus(LocalDate fromDate, LocalDate toDate, Map<String, Integer> origin) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        Map<String, Integer> map = new LinkedHashMap<>();
        if (fromDate != null && toDate != null) {
            Map<String, Integer> _map = new LinkedHashMap<>();
            LocalDate beginDate = fromDate;
            LocalDate endDate = toDate;
            while (beginDate.isBefore(endDate)) {
                _map.put(beginDate.toString(), 0);
                beginDate = beginDate.plusDays(1);
            }
            _map.putAll(origin);
            for (Map.Entry<String, Integer> entry : _map.entrySet()) {
                map.put(LocalDate.parse(entry.getKey()).format(formatter), entry.getValue());
            }
        } else {
            return Collections.emptyMap();
        }
        return map;

    }

    private  List<CallMinuteDto> getRecord(final String acdTime,List<CallMinuteDto> origin){
        List<List<CallMinuteDto>> callMinuteDtoArrayList=new ArrayList<>();
        callMinuteDtoArrayList.add(origin);
        Map<Integer, List<CallMinuteDto>> collect = origin.stream().collect(Collectors.groupingBy(CallMinuteDto::getMinute));

        Map<Integer,List<Integer>> hour24=new HashMap<>(collect.size());
        for (int i=0;i<collect.size();i++){
            hour24.put(i, new ArrayList<>(Arrays.asList(fillArray(24))));
        }

        for (Map.Entry<Integer, List<CallMinuteDto>> entry : collect.entrySet()){
            List<Integer> hour = entry.getValue().stream().map(CallMinuteDto::getHour).collect(Collectors.toList());
            hour24.get(entry.getKey()).removeAll(hour);

            List<CallMinuteDto> nullCollect = hour24.get(entry.getKey()).stream().map(integer -> {
                CallMinuteDto callMinuteDto = new CallMinuteDto();
                callMinuteDto.setMinute(entry.getKey());
                callMinuteDto.setHour(integer);
                callMinuteDto.setAcdTime(acdTime);
                callMinuteDto.setCallStatus(0);
                return callMinuteDto;
            }).collect(Collectors.toList());
            callMinuteDtoArrayList.add(nullCollect);
        }

        List<CallMinuteDto> record = callMinuteDtoArrayList
                .stream()
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(CallMinuteDto::getHour).thenComparing(CallMinuteDto::getMinute))
                .collect(Collectors.toList());
        return record;
    }


}
