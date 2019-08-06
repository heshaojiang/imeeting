package com.grgbanking.grgacd.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pig.common.util.CommonUtils;
import com.github.pig.common.util.R;
import com.github.pig.common.util.RespCode;
import com.grgbanking.grgacd.model.AcdEvaluationScore;
import com.grgbanking.grgacd.service.AcdEvaluationScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 评价等级表。用于可供用户选择的评价等级 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2019-05-24
 */
@RestController
@RequestMapping("/evaluation/score")
public class AcdEvaluationScoreController {

    @Autowired
    AcdEvaluationScoreService evaluationScoreService;

    /**
     * 评论登记列表
     * @param params 参数，只接收name
     * @return
     */
    @GetMapping("/list")
    R<Object> getList(@RequestParam Map<String, Object> params){
        String agentName = CommonUtils.getMappingParams(params,"name",false);
        List<AcdEvaluationScore> list = evaluationScoreService.getList(agentName);
        return new R<>(Boolean.TRUE,list);
    }


    @DeleteMapping("/{id}")
    R<Boolean> delete(@PathVariable("id") Integer id){
        if (evaluationScoreService.checkUse(id)){
            return new R<>(RespCode.CNSL_IN_USE);
        }else{
            evaluationScoreService.deleteById(id);
        }
        return new R<>(Boolean.TRUE);
    }

    @PutMapping
    R<Boolean> update(@RequestBody AcdEvaluationScore score){
        score.setUpdateTime(new Date());
        boolean flag = evaluationScoreService.updateById(score);
        return new R<>(flag);
    }

    @PostMapping
    R<Boolean> add(@RequestBody AcdEvaluationScore score){
        boolean flag = evaluationScoreService.insert(score);
        return new R<>(flag);
    }

    @GetMapping("/{id}")
    R<Object> getOne(@PathVariable Integer id){
        AcdEvaluationScore score = evaluationScoreService.selectById(id);
        return new R<>(Boolean.TRUE,score);
    }

}

