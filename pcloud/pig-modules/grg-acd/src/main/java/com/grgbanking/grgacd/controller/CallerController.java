package com.grgbanking.grgacd.controller;

import com.github.pig.common.util.CommonUtils;
import com.github.pig.common.util.R;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.util.exception.GrgException;
import com.grgbanking.grgacd.common.*;
import com.grgbanking.grgacd.model.AcdCalls;
import com.grgbanking.grgacd.model.AcdEvaluation;
import com.grgbanking.grgacd.model.AcdEvaluationScore;
import com.grgbanking.grgacd.service.*;
import com.grgbanking.grgacd.vo.AcdEvaluationScoreVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wjqiu
 * @date 2019-04-20
 */

@RestController
@RequestMapping("/caller")
public class CallerController {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CallerService callerService;
    @Autowired
    private AcdCallsService callsService;
    @Autowired
    private AcdEvaluationService evaluationService;
    @Autowired
    private AcdEvaluationScoreService evaluationScoreService;

    @PostConstruct
    private void init(){
    }

    @RequestMapping("/login")
    public R login(@RequestBody Map<String, Object> params) throws GrgException {
        String callerName = CommonUtils.getMappingParams(params,"callerName");
        String clientId = CommonUtils.getMappingParams(params,"clientId");
        String platformType = CommonUtils.getMappingParams(params,"platformType");

        Caller caller = callerService.login(callerName,clientId,platformType);

        if (caller != null){
            return new R(true,caller);
        } else {
            return new R(RespCode.ACD_CALLER_NOT_LOGIN);
        }
    }
    @RequestMapping("/logout")
    public R logout(@RequestBody Map<String, Object> params) throws GrgException {
        String clientId = (String) params.get("clientId");
        return new R(callerService.logout(clientId));
    }

        @RequestMapping("/makeCall")
    public R makeCall(@RequestBody Map<String, Object> params) throws GrgException {
        String clientId = CommonUtils.getMappingParams(params,"clientId");
        String queueId = CommonUtils.getMappingParams(params,"queueId");
        String callerName = CommonUtils.getMappingParams(params,"callerName",false);
        Object metaData = params.get("metaData");
        log.info("makeCall queueId:{} clientId:{},callerName:{}",queueId,clientId,callerName);

        Caller caller = callerService.makeCall(clientId,queueId,callerName,metaData);

        return new R(Boolean.TRUE,caller);
    }
    @RequestMapping("/hangup")
    public R hangup(@RequestBody Map<String, Object> params) throws GrgException {
        String clientId = CommonUtils.getMappingParams(params,"clientId");
//        String callId = CommonUtils.getMappingParams(params,"callId");
//        if (StringUtils.isEmpty(callId)) {
//            log.info("callId is empty,find callerId fromClientId");
//            callId = callerService.getCallIdFromClinetId(clientId);
//        }
//        return new R(callerService.hangupCall(callId,clientId,HangupReason.FROM_CALLER));
        return new R(callerService.hangupCall(clientId,HangupReason.FROM_CALLER));
    }

    @RequestMapping("/evaluation")
    public R evaluation(@RequestBody Map<String, Object> params) throws GrgException {
        String clientId = CommonUtils.getMappingParams(params,"clientId");
        String callId = CommonUtils.getMappingParams(params,"callId");
        String resultType = CommonUtils.getMappingParams(params,"resultType");
        String scoreId = CommonUtils.getMappingParams(params,"scoreId");
        String desc = CommonUtils.getMappingParams(params,"desc",false);
        log.info("evaluation clientId:{},callId:{},resultType:{},scoreId:{}",clientId,callId,resultType,scoreId);
        AcdCalls calls = callsService.getCallById(callId);
        if (calls != null) {
            String callerClientId = calls.getCallerClientId();
            log.info("evaluation clientId:{},ClientId In Call:{}",clientId,callerClientId);
            if (!clientId.equals(callerClientId)) {
                throw new GrgException(RespCode.ACD_CALL_NOT_EXIST);
            }
        }
        AcdEvaluation evaluation = new AcdEvaluation();
        evaluation.setCallId(callId);
        evaluation.setResultType(Integer.valueOf(resultType));
        evaluation.setScoreId(Integer.valueOf(scoreId));
        evaluation.setDesc(desc);
        evaluation.setUpdateTime(new Date());
        evaluation.setCreatedTime(new Date());

        boolean flag = evaluationService.insert(evaluation);

        return new R(flag);
    }
    @RequestMapping("/evaluationScoreList")
    public R evaluationScoreList(@RequestBody Map<String, Object> params){
        String agentName = CommonUtils.getMappingParams(params,"name",false);
        List<AcdEvaluationScore> list = evaluationScoreService.getList(agentName);
        List<AcdEvaluationScoreVo> listVo = new ArrayList<>();
        list.stream().forEach(score -> {
            AcdEvaluationScoreVo vo = new AcdEvaluationScoreVo();
            BeanUtils.copyProperties(score, vo);
            listVo.add(vo);
        });
        return new R(Boolean.TRUE,listVo);
    }
}
