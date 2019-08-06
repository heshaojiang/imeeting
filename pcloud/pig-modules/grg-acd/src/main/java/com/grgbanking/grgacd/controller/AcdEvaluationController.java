package com.grgbanking.grgacd.controller;


import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.CommonUtils;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.R;
import com.grgbanking.grgacd.dto.CallDto;
import com.grgbanking.grgacd.dto.converter.CallConverter;
import com.grgbanking.grgacd.dto.converter.EvaluateConverter;
import com.grgbanking.grgacd.model.AcdEvaluation;
import com.grgbanking.grgacd.service.AcdEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * <p>
 * 通话评价表，记录用户对当前通话的评价等级与内容 前端控制器
 * </p>
 *
 * @author yjw
 * @since 2019-06-17
 */
@RestController
@RequestMapping("/evaluation")
public class AcdEvaluationController {

    @Autowired
    AcdEvaluationService evaluationService;

    /**
     * 评论分页列表
     * 参数 sort false 升序 true 降序
     * 参数 description 业务场景
     * 参数 agentName 客服名称
     * @param params
     * @return
     */
    @GetMapping("/page")
    public R<Object> page(@RequestParam Map<String, Object> params){
        Page page = evaluationService.getPage(new Query<>(params));
        Page map = EvaluateConverter.INSTANCE.map(page);
        return new R<>(Boolean.TRUE,map);
    }

    /**
     * 添加评论
     * @param evaluation
     * @return
     */
    @PostMapping
    public R<Object> add(@RequestBody AcdEvaluation evaluation){
        boolean flag = evaluationService.insert(evaluation);
        return new R<>(flag);
    }

    /**
     * 删除评论
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public R<Object> delete(@RequestParam("id") Integer id){
        boolean flag = evaluationService.deleteById(id);
        return new R<>(flag);
    }

}

