package com.grgbanking.grgacd.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.Query;
import com.grgbanking.grgacd.mapper.AcdReportFormMapper;
import com.grgbanking.grgacd.service.AcdReportFormService;
import com.grgbanking.grgacd.vo.AgentInfoVo;
import com.grgbanking.grgacd.vo.ReportVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author yjw
 * @since 2019-6-27
 */
@Slf4j
@Service
@Transactional
public class AcdReportServiceImpl implements AcdReportFormService {

    @Autowired
    private  AcdReportFormMapper acdReportMapper;
    private final static String RECEIVE = "receive";
    private final static String NORECEIVE = "noreceive";
    private final static String WECHAT = "weixin";
    private final static String IMEETING = "imeeting";
    private final static String SOLVE = "solve";
    private final static String UNSOLVE = "unsolve";
    private final static String SATISFIED = "Satisfied";
    private final static String DISSATISFIED = "DisSatisfied";
    private final static int HOUR24=24;

    @Override
    public List<Object> getCountReceiveCalls(Map<String,Object> params) {
        Map<String,Object> receiveCall = new LinkedHashMap<>();
        Map<String,Object> notReceiveCall = new LinkedHashMap<>();
        Map<String,Object> hashMap1 = getHour24Map();
        Map<String,Object> hashMap2 =getHour24Map();
        List list = new ArrayList();

        List<Map<String, Object>> countReceive = acdReportMapper.getCountReceive(params);
        List<Map<String, Object>> countNotReceive = acdReportMapper.getCountNotReceive(params);
        countReceive.forEach(countReceiveMap -> hashMap1.put(countReceiveMap.get("key").toString(),Integer.parseInt(countReceiveMap.get("value").toString())));
        countNotReceive.forEach(countNotReceiveMap -> hashMap2.put(countNotReceiveMap.get("key").toString(),Integer.parseInt(countNotReceiveMap.get("value").toString())));

        receiveCall.put("name",RECEIVE);
        receiveCall.putAll(hashMap1);
        notReceiveCall.put("name",NORECEIVE);
        notReceiveCall.putAll(hashMap2);
        list.add(receiveCall);
        list.add(notReceiveCall);
        return list;
    }



    @Override
    public Page<AgentInfoVo> getAgentInfo(Query<AgentInfoVo> query) {
        List<AgentInfoVo> agentInfo = acdReportMapper.getAgentInfo(query, query.getCondition());

        return query.setRecords(agentInfo);
    }


    @Override
    public List<Map<String,Object>> countCallerPlatFormSum() {
        Map<String,Object> map = new LinkedHashMap<>();
        Map<String,Object> countWCMap = new LinkedHashMap<>();
        List<Map<String,Object>> callerPlatFormList = new ArrayList();
        Integer integer = acdReportMapper.countImeetingSum();
        Integer countWC = acdReportMapper.countWeChatSum();
        map.put("item",IMEETING);
        map.put("count",integer);
        countWCMap.put("item",WECHAT);
        countWCMap.put("count",countWC);
        callerPlatFormList.add(map);
        callerPlatFormList.add(countWCMap);
        return callerPlatFormList;
    }

    @Override
    public List<ReportVo> countResultSolveSum() {
        ReportVo solveSumVo = new ReportVo();
        ReportVo unSolveSumVo = new ReportVo();
        List countResultSolveList = new ArrayList();
        Integer solveSum = acdReportMapper.countSolveSum();
        Integer unSolveSum = acdReportMapper.countUnSolveSum();
        solveSumVo.setItem(SOLVE);
        solveSumVo.setCount(solveSum);
        unSolveSumVo.setItem(UNSOLVE);
        unSolveSumVo.setCount(unSolveSum);
        countResultSolveList.add(solveSumVo);
        countResultSolveList.add(unSolveSumVo);
        return countResultSolveList;
    }

    @Override
    public List<ReportVo> countScoreValueSum() {
        return  acdReportMapper.countSatisfied();
    }

    /**
     * 填充0-23之间的时间
     * @return
     */
    private Map<String, Object> getHour24Map(){
        Map<String,Object> hashMap = new HashMap<>(HOUR24);
        for (int i=0;i< HOUR24;i++){
            hashMap.put(i+":00",0);
        }
        return  hashMap;
    }
}
