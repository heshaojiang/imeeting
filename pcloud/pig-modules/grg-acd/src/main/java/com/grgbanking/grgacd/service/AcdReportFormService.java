package com.grgbanking.grgacd.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.Query;
import com.grgbanking.grgacd.dto.CountSessionVo;
import com.grgbanking.grgacd.vo.AgentInfoVo;
import com.grgbanking.grgacd.vo.ReportVo;

import java.util.List;
import java.util.Map;

/**
 * @author yjw
 * @since 2019-6-27
 * 报表管理类
 */
public interface AcdReportFormService {

    /**
     * 按天统计
     * 查询到的接听和未接听数
     * 参数 date 某一天
    * @param params 参数
     *
     * @return
     */
    List<Object> getCountReceiveCalls(Map<String,Object> params);

    /**
     * 客服人员统计数据
     * @param query
     * @return
     */
    Page<AgentInfoVo> getAgentInfo(Query<AgentInfoVo> query);

    List<Map<String,Object>> countCallerPlatFormSum();

    List<ReportVo> countResultSolveSum();

    List<ReportVo> countScoreValueSum();




}
