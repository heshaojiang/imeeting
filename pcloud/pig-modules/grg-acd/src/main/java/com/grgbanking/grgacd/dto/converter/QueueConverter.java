package com.grgbanking.grgacd.dto.converter;

import com.baomidou.mybatisplus.plugins.Page;
import com.grgbanking.grgacd.dto.QueueDto;
import com.grgbanking.grgacd.dto.converter.transform.AgentTransFrom;
import com.grgbanking.grgacd.model.AcdQueue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author tjshan
 * @since 2019/5/28 10:42
 */
@Mapper(imports = AgentTransFrom.class)
public interface QueueConverter {

    QueueConverter INSTANCE= Mappers.getMapper(QueueConverter.class);

    @Mappings({
            @Mapping(target = "totalAgentCount",expression = "java(new AgentTransFrom().getAgentCountByQueueId(queue.getQueueId()))"),
            @Mapping(target = "onlineAgentCount",expression = "java(new AgentTransFrom().getAgentOnlineCountByQUeueId(queue.getQueueId()))")
    })
    QueueDto map(AcdQueue queue);

    List<QueueDto> map(List<AcdQueue> queues);

    Page<QueueDto> map(Page<AcdQueue> queuePage);
}
