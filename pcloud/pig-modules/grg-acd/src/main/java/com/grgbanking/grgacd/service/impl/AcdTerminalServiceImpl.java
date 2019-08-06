package com.grgbanking.grgacd.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.Query;
import com.grgbanking.grgacd.mapper.AcdTerminalMapper;
import com.grgbanking.grgacd.model.AcdTerminal;
import com.grgbanking.grgacd.service.AcdTerminalService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 终端表，用于保存agent可以登录的授权的终端 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-24
 */
@Service
public class AcdTerminalServiceImpl extends ServiceImpl<AcdTerminalMapper, AcdTerminal> implements AcdTerminalService {
    @Autowired
    AcdTerminalMapper terminalMapper;

    @Override
    public Page<AcdTerminal> getTerminalPage(Query<AcdTerminal> query) {
        query.setRecords(terminalMapper.selectTerminalPage(query,query.getCondition()));
        return query;
    }

    @Override
    public boolean batchDeleteTerminal(String[] terminalIds) {
        Integer count = terminalMapper.deleteBatchIds(Arrays.asList(terminalIds));
        return count==terminalIds.length;
    }

    @Override
    public boolean changeStatus(String terminalNo, String status) {

        AcdTerminal terminal=new AcdTerminal();
        terminal.setTerminalNo(terminalNo);
        terminal.setStatus(status);
        Integer cout = terminalMapper.updateById(terminal);
        return cout==1;
    }
}
