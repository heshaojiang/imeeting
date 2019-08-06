package com.grgbanking.grgacd.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.github.pig.common.util.Query;
import com.grgbanking.grgacd.model.AcdQueue;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 坐席队列 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2019-05-05
 */
@Repository
public interface AcdQueueMapper extends BaseMapper<AcdQueue> {

    /**
     * 分页+条件查询
     * 参数 queueId 队列ID
     * 参数　queueName 队列名称
     * 参数 keyword 关键字（queueId和queueName中任意一个或多个）
     * @param query
     * @param condition
     * @return
     */
    List<Object> selectQueuePage(Query<Object> query, Map<String, Object> condition);

    /**
     * 通过agentId 查询队列信息
     * @param agentId
     * @return
     */
    List<AcdQueue> selectQueueByAgentId(Integer agentId);

}
