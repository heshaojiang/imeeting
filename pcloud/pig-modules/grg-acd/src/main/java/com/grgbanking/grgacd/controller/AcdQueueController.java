package com.grgbanking.grgacd.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.R;
import com.grgbanking.grgacd.dto.QueueStrategyVo;
import com.grgbanking.grgacd.dto.converter.AgentConverter;
import com.grgbanking.grgacd.dto.converter.QueueConverter;
import com.grgbanking.grgacd.model.AcdQueue;
import com.grgbanking.grgacd.service.AcdQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * <p>
 * 坐席队列 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2019-05-05
 */
@RestController
@RequestMapping("/queue")
public class AcdQueueController {

    @Autowired
    private  AcdQueueService acdQueueService;



    @GetMapping("/{queueId}")
    public R<AcdQueue>getQueue(@PathVariable  String queueId){
        AcdQueue acdQueue = acdQueueService.getOneQueue(queueId);
        return new R<>(Boolean.TRUE,acdQueue);
    }

    /**
     * 添加队列
     * @param acdQueue 队列
     * @return R
     */
    @PostMapping
    public R<Boolean> addQueue(@RequestBody AcdQueue acdQueue){
        return new R<>(acdQueueService.addQueue(acdQueue));
    }

    /**
     * 删除队列
     * @param queueIds 队列id
     * @return R
     */
    @DeleteMapping("/{queueIds}")
    public R<Boolean> delQueue(@PathVariable("queueIds") String[] queueIds){
        return new R<>(acdQueueService.deleteQueue(queueIds));
    }

    /**
     * 更新队列
     * @param acdQueue 队列
     * @return R
     */
    @PutMapping
    public R<Boolean> updateQueue(@RequestBody AcdQueue acdQueue){
        acdQueue.setUpdateTime(new Date());
        return new R<>(acdQueueService.updateQueue(acdQueue));
    }

    /**
     * 添加队列成员
     * @param queueId 队列id
     * @param ids 成员id
     * @return R
     */
    @PostMapping("/addMember/{queueId}")
    public R<Boolean> addMember(@PathVariable String queueId, @RequestBody List<Integer> ids){
        Boolean aBoolean = acdQueueService.addMembers(queueId,null,ids);
        return new R<>(aBoolean);
    }

    /**
     * 获取队列列表
     * @param params 参数
     * @return R<Page>
     */
    @GetMapping("/page")
    public R<Page> getQueuePage(@RequestParam Map<String, Object> params){
        Page queuePage = acdQueueService.getQueuePage(new Query<>(params));
        Page page = QueueConverter.INSTANCE.map(queuePage);
        return new R<>(Boolean.TRUE,page);
    }

    /**
     * 获取所有的队列
     * @return
     */
    @GetMapping("/list")
    public  R<List> getQueueList(){
        List<AcdQueue> queues = acdQueueService.selectList(new EntityWrapper<>());
        return new R<>(Boolean.TRUE,queues);
    }

    /**
     * 获取队列所有的成员
     * @param queueId 队列id
     * @param params 参数
     * @return R<Page>
     */
    @GetMapping("/page/members/{queueId}")
    public R<Page> getQueueMember(@PathVariable String queueId,@RequestParam Map<String, Object> params){
        Page page = AgentConverter.INSTANCE.map(acdQueueService.getQueueMembers(queueId, new Query<>(params)));
        return new R<>(Boolean.TRUE,page);
    }

    /**
     * 获取队列所有的在线成员
     * @param queueId
     * @param params
     * @return
     */
    @GetMapping("/page/members/{queueId}/online")
    public R<Page> getQueueMemberOnline(@PathVariable String queueId,@RequestParam Map<String, Object> params){
        Page queueMembers = acdQueueService.getQueueMembersOnline(queueId, new Query<>(params));
        return new R<>(Boolean.TRUE,queueMembers);
    }


    @GetMapping("/strategy")
    public R getQueueStrategy(){
        List<QueueStrategyVo> strategyList = acdQueueService.getQueueStrategyList();
        return new R(Boolean.TRUE,strategyList);
    }
}

