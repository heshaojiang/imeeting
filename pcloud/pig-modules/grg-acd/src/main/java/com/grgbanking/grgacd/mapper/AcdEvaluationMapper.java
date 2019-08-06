package com.grgbanking.grgacd.mapper;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.Query;
import com.grgbanking.grgacd.model.AcdEvaluation;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.grgbanking.grgacd.model.AcdEvaluationScore;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 通话评价表，记录用户对当前通话的评价等级与内容 Mapper 接口
 * </p>
 *
 * @author tjsshan
 * @since 2019-05-24
 */
@Repository
public interface AcdEvaluationMapper extends BaseMapper<AcdEvaluation> {

    /**
     * 获取评论列表
     * 关联表 acd_calls 别名 calls
     * 关联表 acd_evaluation 别名 evaluation
     * @param page
     * @param wrapper
     * @return
     */
    List getList(Page page, @Param("ew") EntityWrapper wrapper);
}
