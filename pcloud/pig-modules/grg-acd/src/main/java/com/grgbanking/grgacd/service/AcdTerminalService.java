package com.grgbanking.grgacd.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.Query;
import com.grgbanking.grgacd.model.AcdTerminal;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 终端表，用于保存agent可以登录的授权的终端 服务类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-24
 */
public interface AcdTerminalService extends IService<AcdTerminal> {

    boolean batchDeleteTerminal(String[] terminalIds);

    boolean changeStatus(String terminalNo, String status);

    Page<AcdTerminal> getTerminalPage(Query<AcdTerminal> query);
}
