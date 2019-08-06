package com.github.pig.admin.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.admin.model.entity.BizMeetingHist;
import com.github.pig.admin.service.BizMeetingHistService;
import com.github.pig.common.constant.CommonConstant;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.R;
import com.github.pig.common.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * <p>
 * 无创建时间、创建时间超过24小时的会议及会议已结束 前端控制器
 * </p>
 *
 * @author fmsheng
 * @since 2018-07-18
 */
@RestController
@RequestMapping("/bizMeetingHist")
public class BizMeetingHistController extends BaseController {
    @Autowired private BizMeetingHistService bizMeetingHistService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return BizMeetingHist
    */
    @GetMapping("/{id}")
    public BizMeetingHist get(@PathVariable Integer id) {
        return bizMeetingHistService.selectById(id);
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
        return bizMeetingHistService.selectPage(new Query<>(params), new EntityWrapper<>());
    }

    /**
     * 添加
     * @param  bizMeetingHist  实体
     * @return success/false
     */
    @PostMapping
    public R<Boolean> add(@RequestBody BizMeetingHist bizMeetingHist) {
        return new R<>(bizMeetingHistService.insert(bizMeetingHist));
    }

    /**
     * 删除
     * @param id ID
     * @return success/false
     */
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Integer id) {
        BizMeetingHist bizMeetingHist = new BizMeetingHist();
        bizMeetingHist.setId(id);
        //.setUpdateTime(new Date());
        bizMeetingHist.setDelFlag(CommonConstant.STATUS_DEL);
        return new R<>(bizMeetingHistService.updateById(bizMeetingHist));
    }

    /**
     * 编辑
     * @param  bizMeetingHist  实体
     * @return success/false
     */
    @PutMapping
    public R<Boolean> edit(@RequestBody BizMeetingHist bizMeetingHist) {
        //bizMeetingHist.setUpdateTime(new Date());
        return new R<>(bizMeetingHistService.updateById(bizMeetingHist));
    }
}
