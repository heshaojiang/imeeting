package com.grgbanking.grgacd.dto.converter;

import com.baomidou.mybatisplus.plugins.Page;
import com.grgbanking.grgacd.dto.EvaluateDto;
import com.grgbanking.grgacd.dto.converter.transform.AgentTransFrom;
import com.grgbanking.grgacd.dto.converter.transform.CallTransFrom;
import com.grgbanking.grgacd.model.AcdEvaluation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author tjshan
 * @since 2019/6/26 17:17
 */
@Mapper(imports = {AgentTransFrom.class, CallTransFrom.class})
public interface EvaluateConverter {

    EvaluateConverter INSTANCE= Mappers.getMapper(EvaluateConverter.class);


    @Mappings({
            @Mapping(target = "callerName",source = "calls.callerName"),
            @Mapping(target = "startTime",source = "calls.answerTime"),
            @Mapping(target = "endTime",source = "calls.hangupTime"),
            @Mapping(target = "scoreText",source = "score.name"),
            @Mapping(target = "scoreValue",source = "score.scoreValue"),
            @Mapping(target = "agentName",expression = "java(new AgentTransFrom().gentAgentName(evaluation.getCalls().getAgentId()))"),
            @Mapping(target = "scene",expression = "java(new CallTransFrom().getQueue(evaluation.getCalls().getQueueId()).getDescription())")
    })
    EvaluateDto map(AcdEvaluation evaluation);

    List<EvaluateDto> map(List<AcdEvaluation> evaluations);

    Page<EvaluateDto> map(Page<AcdEvaluation> evaluationPage);
}
