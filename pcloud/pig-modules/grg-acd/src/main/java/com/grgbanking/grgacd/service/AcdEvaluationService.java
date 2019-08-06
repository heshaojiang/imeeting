package com.grgbanking.grgacd.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.Query;
import com.grgbanking.grgacd.model.AcdEvaluation;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 通话评价表，记录用户对当前通话的评价等级与内容 服务类
 * </p>
 *
 * @author yjw
 * @since 2019-06-17
 */
public interface AcdEvaluationService extends IService<AcdEvaluation> {


    /**
     * 评论分页
     * @param query
     * @return
     */
    Page getPage(Query<Object> query);
}
