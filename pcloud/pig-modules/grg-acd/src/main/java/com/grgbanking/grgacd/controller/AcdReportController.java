package com.grgbanking.grgacd.controller;


import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.R;
import com.grgbanking.grgacd.service.AcdReportFormService;
import com.grgbanking.grgacd.vo.AgentInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author yjw
 * 报表管理
 *
 */
@RestController
@RequestMapping("/report")
public class AcdReportController {

    private final AcdReportFormService acdReportService;

    @Autowired
    public AcdReportController(AcdReportFormService acdReportService) {
        this.acdReportService = acdReportService;
    }


    /**
     * 今日会话统计
     * @param params
     * @return
     */
    @GetMapping("/receivecall")
    public R<Object> getCountSessions(@RequestParam Map<String, Object> params){
        List<Object> receiveCalls = acdReportService.getCountReceiveCalls(params);
        return new R<>(Boolean.TRUE,receiveCalls);
    }


    @GetMapping("/agentinfo")
    public  R<AgentInfoVo> getAgentInfo(@RequestParam Map<String, Object> params){
        Page<AgentInfoVo> getAgentInfo = acdReportService.getAgentInfo(new Query<>(params));
        return new R<>(Boolean.TRUE,getAgentInfo);
    }

    /**
     * 客服来源
     * @return
     */
    @GetMapping("/callplatform")
    public R<Object> countCallerPlatForm(){
        List<Map<String, Object>> maps = acdReportService.countCallerPlatFormSum();
        return new R<>(Boolean.TRUE,maps);

    }

    /**
     * 问题解决率列表
     * @return
     */
    @GetMapping("/resultsolve")
    public  R<Object> countResultTypeSum(){
        return new R<>(Boolean.TRUE, acdReportService.countResultSolveSum());
    }



    /**
     * 满意度统计列表
     * @return
     */
    @GetMapping("satistify")
    public R<Object> countSatistify(){
        return  new R<>(Boolean.TRUE,acdReportService.countScoreValueSum());
    }


}
