package com.grgbanking.grgacd.controller;


import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.R;
import com.grgbanking.grgacd.common.AgentStatus;
import com.grgbanking.grgacd.common.CallStatus;
import com.grgbanking.grgacd.dto.CallDto;
import com.grgbanking.grgacd.dto.converter.CallConverter;
import com.grgbanking.grgacd.service.AcdCallsService;
import com.grgbanking.grgacd.service.AgentService;
import com.grgbanking.grgacd.service.CallerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * <p>
 * 坐席状态变迁表 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2019-05-05
 */
@Slf4j
@RestController
@RequestMapping("/status")
public class AcdStatusController {

    @Autowired
    private AcdCallsService acdCallsService;
    @Autowired
    private AgentService agentService;
    @Autowired
    private CallerService callerService;

    /**
     * **********************************************
     * 状态数据统计  <Summary Interval>
     * **********************************************
     */
    @GetMapping("/year")
    public R<Object> getStatusYear(){

        return null;
    }

    /**
     * 按天-状态统计列表
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/day")
    public R<Object> getStatusDay(@RequestParam Map<String, Object> params) throws Exception {
        Page<Object> page = acdCallsService.getCallStatus(new Query<>(params));
        return new R<>(Boolean.TRUE,page);
    }

    /**
     * 按天-统计列表 -横坐标
     * @param params
     * @return
     */
    @GetMapping("/day/field")
    public R<Object> getStatusDayField(@RequestParam Map<String, Object> params){
        List<String> list = acdCallsService.getStatusFields(new Query<>(params));
        return new R<>(Boolean.TRUE,list);
    }

    @GetMapping("/week")
    public R<Object> getStatusWeek(@RequestParam Map<String, Object> params){

        return null;
    }

    /**
     * 获取当前状态下的各类人员数据
     * @return
     */
    @GetMapping("/current")
    public R<Object> getStatusCurrent(){
        Map<String,Integer> map=new HashMap<>(3);
        //正在咨询人数
        map.put("onlineCaller",agentService.getAgentCountWithStatus(AgentStatus.SERVICE));
        //在线客服人数
        map.put("onlineAgent", agentService.getAgentOnlineCount());
        //当前等待人数
        map.put("pendingCaller",callerService.cachePendingCount(null));
        return new R<>(Boolean.TRUE,map);
    }

    /**
     * 获取实时的通话状态列表
     * @return
     */
    @GetMapping("/page")
    public R<Object> getCallsPage(@RequestParam Map<String, Object> params){
        Page<CallDto> page = CallConverter.INSTANCE.map(acdCallsService.getCallPage(new Query<>(params)));
        return new R<>(Boolean.TRUE,page);
    }

}

