package com.grgbanking.grgacd.mapper;

import com.github.pig.common.util.Query;
import com.grgbanking.grgacd.dto.CountSessionVo;
import com.grgbanking.grgacd.vo.AgentInfoVo;
import com.grgbanking.grgacd.vo.ReportVo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 报表管理
 * @author yjw
 * @since 2019-6-27
 */
@Repository
public interface AcdReportFormMapper {

    /**
     * 统计已接听会话数量
     * @param params
     * @return 总数
     */
    List<Map<String, Object>> getCountReceive(Map<String,Object> params);


    /**
     * 获取agent的服务时间相关统计
     * @param query
     * @param condition
     * @return
     */
    List<AgentInfoVo> getAgentInfo(Query<AgentInfoVo> query, Map<String, Object> condition);

    /**
     * 统计未接听
     * @param params
     * @return
     */
    List<Map<String, Object>>  getCountNotReceive(Map<String,Object> params);

    /**
     * 获取微信方式的接入数量
     * @return
     */
    Integer countWeChatSum();

    /**
     * 获取imeeting方式接入的接入数量
     * @return
     */
    Integer countImeetingSum();

    /**
     * 获取问题解决的数量
     * @return
     */
    Integer countSolveSum();

    /**
     * 获取问题未解答的数量
     * @return
     */
    Integer countUnSolveSum();

    /**
     *获取问题解决满意度
     * @return
     */
    List<ReportVo> countSatisfied();

}
