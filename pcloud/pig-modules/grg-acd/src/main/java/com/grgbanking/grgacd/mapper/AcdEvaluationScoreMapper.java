package com.grgbanking.grgacd.mapper;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.grgbanking.grgacd.model.AcdEvaluationScore;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 评价等级表。用于可供用户选择的评价等级 Mapper 接口
 * </p>
 *
 * @author yjw
 * @since 2019-07-6
 */
@Repository
public interface AcdEvaluationScoreMapper extends BaseMapper<AcdEvaluationScore> {

    List selectList(EntityWrapper<AcdEvaluationScore> wrapper);
    AcdEvaluationScore selectById(Integer id);

}
