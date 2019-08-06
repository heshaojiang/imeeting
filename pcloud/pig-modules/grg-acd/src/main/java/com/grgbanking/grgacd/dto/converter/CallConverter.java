package com.grgbanking.grgacd.dto.converter;

import com.baomidou.mybatisplus.plugins.Page;
import com.grgbanking.grgacd.dto.CallDto;
import com.grgbanking.grgacd.dto.converter.transform.BasicTransFrom;
import com.grgbanking.grgacd.dto.converter.transform.CallTransFrom;
import com.grgbanking.grgacd.model.AcdCalls;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author tjshan
 * @since 2019/5/27 16:26
 */
@Mapper(imports = {BasicTransFrom.class, CallTransFrom.class})
public interface CallConverter {

    CallConverter INSTANCE= Mappers.getMapper(CallConverter.class);

    @Mappings({
            @Mapping(source = "agent.username",target = "agentName"),
            @Mapping(source = "queue.queueName",target = "queueName"),
            @Mapping(target = "duration",expression = "java(new BasicTransFrom().getDurationSecond(acdCalls.getMakecallTime(),acdCalls.getHangupTime()))"),
            @Mapping(target = "status",expression = "java(new CallTransFrom().CallStatusToStatus(acdCalls.getCallStatus()))")

    })
    CallDto map(AcdCalls acdCalls);

    List<CallDto> map(List<AcdCalls> acdCalls);

    Page<CallDto> map(Page<AcdCalls> acdCallsPage);
}
