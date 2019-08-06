package com.github.pig.admin.controller;
import java.util.Map;
import java.util.Date;

import com.github.pig.admin.model.entity.BizMeetingParticipantPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.github.pig.common.constant.CommonConstant;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.Query;
import com.github.pig.admin.service.BizMeetingParticipantPlanService;
import com.github.pig.common.web.BaseController;

/**
 * <p>
 * 创建会议时设定的参会人员列表 前端控制器
 * </p>
 *
 * @author fmsheng
 * @since 2018-12-25
 */
@RestController
@RequestMapping("/bizMeetingParticipantPlan")
public class BizMeetingParticipantPlanController extends BaseController {
    @Autowired private BizMeetingParticipantPlanService bizMeetingParticipantPlanService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return BizMeetingParticipantPlan
    */
    @GetMapping("/{id}")
    public BizMeetingParticipantPlan get(@PathVariable Integer id) {
        return bizMeetingParticipantPlanService.selectById(id);
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @RequestMapping("/page")
    public Page page(@RequestParam Map<String, Object> params) {
        params.put(CommonConstant.DEL_FLAG, CommonConstant.STATUS_NORMAL);
        return bizMeetingParticipantPlanService.selectPage(new Query<>(params), new EntityWrapper<>());
    }

    /**
     * 添加
     * @param  bizMeetingParticipantPlan  实体
     * @return success/false
     */
    @PostMapping
    public Boolean add(@RequestBody BizMeetingParticipantPlan bizMeetingParticipantPlan) {
        return bizMeetingParticipantPlanService.insert(bizMeetingParticipantPlan);
    }

    /**
     * 删除
     * @param id ID
     * @return success/false
     */
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        BizMeetingParticipantPlan bizMeetingParticipantPlan = new BizMeetingParticipantPlan();
        bizMeetingParticipantPlan.setId(id);
        return bizMeetingParticipantPlanService.deleteById(bizMeetingParticipantPlan);
    }

    /**
     * 编辑
     * @param  bizMeetingParticipantPlan  实体
     * @return success/false
     */
    @PutMapping
    public Boolean edit(@RequestBody BizMeetingParticipantPlan bizMeetingParticipantPlan) {
        return bizMeetingParticipantPlanService.updateById(bizMeetingParticipantPlan);
    }
}
