package com.grgbanking.grgacd.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.grgbanking.grgacd.mapper.AcdEvaluationMapper;
import com.grgbanking.grgacd.mapper.AcdEvaluationScoreMapper;
import com.grgbanking.grgacd.model.AcdEvaluationScore;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.grgbanking.grgacd.service.AcdEvaluationScoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 评价等级表。用于可供用户选择的评价等级 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-24
 */
@Slf4j
@Service
public class AcdEvaluationScoreServiceImpl extends ServiceImpl<AcdEvaluationScoreMapper, AcdEvaluationScore> implements AcdEvaluationScoreService {

    @Autowired
    AcdEvaluationScoreMapper evaluationScoreMapper;
    @Autowired
    AcdEvaluationMapper evaluationMapper;

    @Override
    public List<AcdEvaluationScore> getList(String evaluateName) {
        EntityWrapper wrapper=new EntityWrapper();
        if (StringUtils.isNotEmpty(evaluateName)){
            wrapper.eq("name",evaluateName);
        }
        List list = evaluationScoreMapper.selectList(wrapper);
        return list;
    }

    @Override
    public Boolean checkUse(Integer scoreId) {
        EntityWrapper wrapper=new EntityWrapper();
        wrapper.eq("score_id",scoreId);
        wrapper.setSqlSelect("score_id");
        int count= evaluationMapper.selectCount(wrapper);
        if (count>0){
            log.warn("score "+scoreId+" is in use");
            return true;
        }
        return false;
    }
}
