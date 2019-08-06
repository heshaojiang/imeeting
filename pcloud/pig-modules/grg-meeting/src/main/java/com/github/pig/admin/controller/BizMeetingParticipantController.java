package com.github.pig.admin.controller;
import java.util.Map;
import java.util.Date;

import com.github.pig.common.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.github.pig.common.constant.CommonConstant;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.Query;
import com.github.pig.admin.service.BizMeetingParticipantService;
import com.github.pig.common.web.BaseController;
import com.github.pig.admin.model.entity.BizMeetingParticipant;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author bywei
 * @since 2018-06-15
 */
@RestController
@RequestMapping("/bizMeetingParticipant")
public class BizMeetingParticipantController extends BaseController {
    @Autowired private BizMeetingParticipantService bizMeetingParticipantService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return BizMeetingParticipant
    */
    @GetMapping("/{id}")
    public BizMeetingParticipant get(@PathVariable Integer id) {
        return bizMeetingParticipantService.selectById(id);
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
        return bizMeetingParticipantService.selectPage(new Query<>(params), new EntityWrapper<>());
    }

    /**
     * 添加
     * @param  bizMeetingParticipant  实体
     * @return success/false
     */
    @PostMapping
    public R<Boolean> add(@RequestBody BizMeetingParticipant bizMeetingParticipant) {
//        bizMeetingParticipant.setCreatedAt(new Date());
//        bizMeetingParticipant.setNickname("cmeeting");
        return new R<>(bizMeetingParticipantService.insert(bizMeetingParticipant));
    }

    /**
     * 删除
     * @param id ID
     * @return success/false
     */
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable String id) {
        BizMeetingParticipant bizMeetingParticipant = new BizMeetingParticipant();
        bizMeetingParticipant.setId(id);
//        bizMeetingParticipant.setUpdatedAt(new Date());
        return new R<>(bizMeetingParticipantService.updateById(bizMeetingParticipant));
    }

    /**
     * 编辑
     * @param  bizMeetingParticipant  实体
     * @return success/false
     */
    @PutMapping
    public R<Boolean> edit(@RequestBody BizMeetingParticipant bizMeetingParticipant) {
//        bizMeetingParticipant.setUpdatedAt(new Date());
        return new R<>(bizMeetingParticipantService.updateById(bizMeetingParticipant));
    }
}
