package com.grgbanking.grgacd.mapper;

import com.github.pig.common.util.Query;
import com.grgbanking.grgacd.model.AcdTerminal;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 终端表，用于保存agent可以登录的授权的终端 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2019-05-24
 */
@Repository
public interface AcdTerminalMapper extends BaseMapper<AcdTerminal> {

    /**
     * 终端列表
     * 参数 terminalNo 终端编号
     * 参数 name 终端名称
     * 参数 accessType 接入类型 pc pad phone
     * 参数 keyword 查询关键字 terminalNo+name
     * @param query
     * @param condition
     * @return
     */
    List<AcdTerminal> selectTerminalPage(Query<AcdTerminal> query, Map<String, Object> condition);
}
