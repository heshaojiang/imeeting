package com.grgbanking.grgacd.service;

import com.grgbanking.grgacd.model.AcdEvaluationScore;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 评价等级表。用于可供用户选择的评价等级 服务类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-24
 */
public interface AcdEvaluationScoreService extends IService<AcdEvaluationScore> {

    /**
     * 获取评级指标列表
     * @param EvaluateName 评价名称
     * @return
     */
    List<AcdEvaluationScore> getList(String EvaluateName);

    Boolean checkUse(Integer scoreId);
}
