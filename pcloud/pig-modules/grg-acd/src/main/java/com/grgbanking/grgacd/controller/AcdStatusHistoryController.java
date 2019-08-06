package com.grgbanking.grgacd.controller;


import com.github.pig.common.util.R;
import com.grgbanking.grgacd.model.AcdStatusHistory;
import com.grgbanking.grgacd.service.AgentStatusHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

/**
 * <p>
 * 坐席状态变迁表 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2019-05-05
 */
@Controller
@RequestMapping("/agentStatusHistory")
public class AcdStatusHistoryController {

    @Autowired
    private AgentStatusHistoryService agentStatusHistoryService;


    /**
     * 添加变迁纪录
     * @param agentStatusHistory 纪录
     * @return
     */
    @PostMapping
    public R<Boolean> addHistory(@RequestBody AcdStatusHistory agentStatusHistory){
        boolean b = agentStatusHistoryService.insert(agentStatusHistory);
        return new R<>(b);
    }
}

