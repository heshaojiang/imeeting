package com.grgbanking.grgacd.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pig.common.util.CommonUtils;
import com.github.pig.common.util.Query;
import com.grgbanking.grgacd.mapper.AcdEvaluationMapper;
import com.grgbanking.grgacd.model.AcdEvaluation;
import com.grgbanking.grgacd.service.AcdEvaluationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 通话评价表，记录用户对当前通话的评价等级与内容 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-24
 */
@Service
public class AcdEvaluationServiceImpl extends ServiceImpl<AcdEvaluationMapper, AcdEvaluation> implements AcdEvaluationService {

    private static  final String AGENT_NAME="agentName";
    private static final  String SORT="sort";
    private static final String QUEUE_ID="queueId";

    @Autowired
    AcdEvaluationMapper evaluationMapper;
    @Override
    public Page getPage(Query<Object> query) {
        String sort = CommonUtils.getMappingParams(query.getCondition(),SORT,false);
        String agentName=CommonUtils.getMappingParams(query.getCondition(),AGENT_NAME,false);
        String queueId=CommonUtils.getMappingParams(query.getCondition(),QUEUE_ID,false);
        EntityWrapper wrapper=new EntityWrapper();
        boolean isAsc = Boolean.parseBoolean(sort);
        wrapper.orderBy("evaluation.created_time", isAsc);
        wrapper.isWhere(true);
        wrapper.eq("evaluation.del_flag","0");
        wrapper.like("agent.username",agentName);
        if(StringUtils.isNotBlank(queueId)){
            wrapper.eq(" calls.queue_id",queueId);
        }
        List list = evaluationMapper.getList(query, wrapper);
        query.setRecords(list);
        return query;
    }
}
