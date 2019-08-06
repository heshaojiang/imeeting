package com.grgbanking.grgacd.dto.converter;

import com.baomidou.mybatisplus.plugins.Page;
import com.grgbanking.grgacd.dto.AcdUserDto;
import com.grgbanking.grgacd.dto.converter.transform.AgentTransFrom;
import com.grgbanking.grgacd.model.SysUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author tjshan
 * @since 2019/5/29 9:33
 */
@Mapper(imports = AgentTransFrom.class)
public interface AgentConverter {

    AgentConverter INSTANCE= Mappers.getMapper(AgentConverter.class);


    @Mappings({
            @Mapping(target = "status",expression = "java(new AgentTransFrom().getAgentStatus(user.getUsername()))"),
            @Mapping(target = "queueCount",expression = "java(new AgentTransFrom().getQueueCountByAgentId(user.getUserId()))")

    })
    AcdUserDto map(SysUser user);

    List<AcdUserDto> map(List<SysUser> users);

    Page<AcdUserDto> map(Page<SysUser> userPage);
}
