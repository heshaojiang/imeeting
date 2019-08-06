package com.grgbanking.grgacd.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.grgbanking.grgacd.model.AcdAgentQueue;
import com.grgbanking.grgacd.model.AcdCalls;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * <p>
 * 通话记录表 Mapper 接口
 * </p>
 *
 * @author tjshan
 * @since 2019-05-05
 */
@Repository
public interface AcdAgentQueueMapper extends BaseMapper<AcdAgentQueue> {

    /**
     * 查询一个 客服所在的队列
     * @param agentId 客服id
     * @return Set
     */
    Set<String> selectQueueByAgent(Integer agentId);
}
