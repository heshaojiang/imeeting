package com.github.pig.admin.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.admin.common.util.MeetingUtils;
import com.github.pig.admin.model.entity.BizMeeting;
import com.github.pig.admin.model.entity.BizMeetingParticipantPlan;
import com.github.pig.admin.model.entity.SysUser;
import com.github.pig.admin.service.*;
import com.github.pig.common.constant.CommonConstant;
import com.github.pig.common.constant.MeetingConstant;
import com.github.pig.common.util.*;
import com.github.pig.common.util.exception.UnloginException;
import com.github.pig.common.vo.MeetingParticipantPlan;
import com.github.pig.common.vo.MeetingVo;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 会议预约、会议召开后会生成会议信息，会议信息记录会议的基本信息 前端控制器
 * </p>
 *
 * @author grg
 * @since 2018-05-09
 */
@RestController
@RequestMapping("/bizMeeting")
@Api
public class BizMeetingController extends BaseController {

    @Autowired
    private BizMeetingService bizMeetingService;

    @Autowired
    private BizMeetingParticipantService bizMeetingParticipantService;

    @Autowired
    private BizMeetingParticipantPlanService bizMeetingParticipantPlanService;

    @Autowired
    private SysUserService userService;

    @Autowired
    private IvxMeetingService ivxMeetingService;

    private static final Logger logger = LoggerFactory.getLogger(BizMeetingController.class);

    /**
    * 通过ID查询
    *
    * @param id
    * @return BizMeeting
    */
    @ApiOperation(value = "通过ID查询")
    @GetMapping("/{id}")
    public R<MeetingVo> get(@PathVariable Integer id) {
        return new R<>(Boolean.TRUE, bizMeetingService.selectVoById(id));
    }


    /**
     * 分页查询信息（预约会议）
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @ApiOperation(value = "分页查询信息（预约会议）")
    @GetMapping("/appointmentMeeting")
    public Page appointmentMeeting(@RequestParam Map<String, Object> params) {

        params.put("delFlag", CommonConstant.STATUS_NORMAL);
        //预约
        params.put("status",MeetingConstant.MEETING_STATUS_PLAN);
        Page<MeetingVo> page = bizMeetingService.selectMeetingPage(new Query<>(params), new EntityWrapper<>());
        List<MeetingVo> meetingVoList = page.getRecords();
        for (MeetingVo meetingVo : meetingVoList) {
            meetingVo.setComperePwd("");
            meetingVo.setMeetingPwd("");
        }
        page.setRecords(meetingVoList);
        return page;
    }

    /**
     * 分页查询信息（即将召开的会议）
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @ApiOperation(value = "分页查询信息（即将召开的会议）")
    @PostMapping("/comingMeeting")
    public R<Page> comingMeeting(@RequestParam Map<String, Object> params) {

        //当前用户所属客户
        Integer customerId = userService.getCustomerIdByUserId();

        params.put("customerId",customerId);

        params.put("delFlag", CommonConstant.STATUS_NORMAL);

        Page<MeetingVo> page = bizMeetingService.selectMeetingPage(new Query<>(params), new EntityWrapper<>());
        List<MeetingVo> meetingVoList = page.getRecords();
        for (MeetingVo meetingVo : meetingVoList) {
            Integer countParticipant = bizMeetingParticipantService.countParticipantOnLine(meetingVo.getMeetingMid());
            if (countParticipant != null)
                meetingVo.setParticipants(String.valueOf(countParticipant));
            meetingVo.setComperePwd("");
            meetingVo.setMeetingPwd("");
        }
        page.setRecords(meetingVoList);
        return new R<>(Boolean.TRUE, page);
    }

    /**
     * 分页查询信息（历史会议）
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @RequestMapping("/historyMeeting")
    public Page historyMeeting(@RequestParam Map<String, Object> params) {
        params.put("delFlag", CommonConstant.STATUS_DEL);
        //历史
        params.put("status","2");
        Page<MeetingVo> page = bizMeetingService.selectMeetingPage(new Query<>(params), new EntityWrapper<>());
        List<MeetingVo> meetingVoList = page.getRecords();
        for (MeetingVo meetingVo : meetingVoList) {
            meetingVo.setComperePwd("");
            meetingVo.setMeetingPwd("");
        }
        page.setRecords(meetingVoList);
        return page;
    }

    /**
     * 分页查询信息（之前的会议）
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @RequestMapping("/previousMeeting")
    public Page previousMeeting(@RequestParam Map<String, Object> params) {
        params.put(CommonConstant.DEL_FLAG, CommonConstant.STATUS_DEL);
        Page<BizMeeting> page = bizMeetingService.selectPage(new Query<>(params), new EntityWrapper<>());
        List<BizMeeting> bizMeetingList = page.getRecords();
        for (BizMeeting bizMeeting : bizMeetingList) {
            bizMeeting.setComperePwd("");
            bizMeeting.setMeetingPwd("");
        }
        page.setRecords(bizMeetingList);
        return page;
    }

    /**
     * 添加(安排新会议)
     * @param  meetingVo  实体
     * @return success/false
     */
    @ApiOperation(value = "添加(安排新会议)")
    @PostMapping
    @Transactional
    public R<Boolean> add(@RequestBody MeetingVo meetingVo) {

        //通过token获取用户
        String username = UserUtils.getUser();
        SysUser sysUser = userService.getCurSysUser();
        if (StringUtils.isEmpty(meetingVo.getCompere())) {
            meetingVo.setCompere(String.valueOf(sysUser.getUserId()));
        }

        if (StringUtils.isEmpty(username)) {
            throw new UnloginException("验证过期，请重新登录！");
        }

        logger.info("ADD MEETING: meetingVo:"+meetingVo);
        //根据会议模式指定通话方数量
        if (meetingVo.getMeetingType() != null && !meetingVo.getMeetingType().equals("0")) {
            meetingVo.setNumVideo(MeetingUtils.getVideoNumFromMeetingType(meetingVo.getMeetingType()));
        }

        //检查会议信息是否可以添加
        R<Boolean> rCheck = bizMeetingService.checkMeeting(meetingVo,null);
        if (!rCheck.getSuccess()){
            logger.warn("bizMeetingService.checkMeeting fail RCheck:"+rCheck);
            return rCheck;
        }

        BizMeeting bizMeeting = new BizMeeting();
        BeanUtils.copyProperties(meetingVo, bizMeeting);

        //处理会议扩展数据
        bizMeeting = handleMeetingData(meetingVo, bizMeeting);

        Integer customerId = userService.getCustomerIdByUserId();

        if (!UserUtils.isAdminUserWithCstmId(customerId)){
            //非管理员只能给自己所属客户添加会议
            bizMeeting.setCustomerId(String.valueOf(customerId));
        }


        //初始化会议数据
        ivxMeetingService.initMeeting(bizMeeting);


        //插入计划 参会人员
        for (MeetingParticipantPlan participantPlan : meetingVo.getParticipantPlanList()) {
            BizMeetingParticipantPlan bizMeetingParticipantPlan = new BizMeetingParticipantPlan();
            BeanUtils.copyProperties(participantPlan,bizMeetingParticipantPlan);
            //写入新生成的MeetingMid
            bizMeetingParticipantPlan.setMeeting_mid(bizMeeting.getMeetingMid());
            logger.info("ADD ParticipantPlan: ParticipantPlan:"+bizMeetingParticipantPlan);
            bizMeetingParticipantPlanService.insert(bizMeetingParticipantPlan);
        }

        return new R<>(bizMeetingService.insert(bizMeeting));
    }

    private BizMeeting handleMeetingData(MeetingVo meetingVo, BizMeeting bizMeeting) {
        try {
            Map map = new HashMap();
            if (meetingVo.getCompereDpi() != null) map.put("compereDpi", meetingVo.getCompereDpi());
            map.put("compereVideoEnable", meetingVo.isCompereVideoEnable());
            map.put("compereAudioEnable", meetingVo.isCompereAudioEnable());
            map.put("participantVideoEnable", meetingVo.isParticipantVideoEnable());
            map.put("participantAudioEnable", meetingVo.isParticipantAudioEnable());
            if (meetingVo.getParticipantDpi() != null) map.put("participantDpi", meetingVo.getParticipantDpi());
            if (meetingVo.getShowMode() != null) map.put("showMode", meetingVo.getShowMode());
            ObjectMapper json = new ObjectMapper();
            String result = json.writeValueAsString(map);
            bizMeeting.setExternConfigs(result);
        } catch (Exception e) {

        }
        return bizMeeting;
    }

    /**
     * 删除
     * @param ids
     * @return success/false
     */
    @ApiOperation(value = "删除会议")
    @DeleteMapping("/{id}")
    @Transactional
    public R<Boolean> delete(@PathVariable("id") String[] ids) {
        RespCode respCode = RespCode.SUCCESS;
        Boolean success = Boolean.TRUE;
        for (String id: ids) {
            BizMeeting bizMeeting = bizMeetingService.selectById(id);
            if (bizMeeting == null){
                respCode = RespCode.CNSL_OBJ_NOT_FOUND;
                success = Boolean.FALSE;
            } else {
                Integer countParticipant = bizMeetingParticipantService.countParticipantOnLine(bizMeeting.getMeetingMid());
                if (countParticipant == null || countParticipant == 0){
                    //真正结束时间
                    bizMeeting.setRealEndTime(new Date());
                    bizMeeting.setStatus(MeetingConstant.MEETING_STATUS_END);
                    bizMeeting.setDelFlag(CommonConstant.STATUS_DEL);
                    boolean result = bizMeetingService.updateById(bizMeeting);
                    if (!result) {
                        respCode = RespCode.IME_DB_FAIL;
                        success = Boolean.FALSE;
                    }
                } else {
                    //仍有参会人员在。不能删除
                    respCode = RespCode.CNSL_MEETING_HAS_PARTICIPANT;
                    success = Boolean.FALSE;
                }

            }

        }
        return new R<>(success,respCode);
    }

    /**
     * 编辑
     * @param  meetingVo  实体
     * @return success/false
     */
    @ApiOperation(value = "编辑会议")
    @PutMapping
    @Transactional
    public R<Boolean> edit(@RequestBody MeetingVo meetingVo) {
//        bizMeetingNew.setUpdateTime(new Date());
        String username = UserUtils.getUser();
        BizMeeting bizMeetingOri = bizMeetingService.selectById(meetingVo.getId());
        if (bizMeetingOri == null){
            return new R<>(Boolean.FALSE,RespCode.CNSL_OBJ_NOT_FOUND);
        }
        //根据会议模式指定通话方数量
        if (!meetingVo.getMeetingType().equals("0")) {
            meetingVo.setNumVideo(MeetingUtils.getVideoNumFromMeetingType(meetingVo.getMeetingType()));
        }

        //检查会议信息是否可以修改
        R<Boolean> rCheck = bizMeetingService.checkMeeting(meetingVo,bizMeetingOri);
        if (!rCheck.getSuccess()){
            logger.warn("bizMeetingService.checkMeeting fail");
            return rCheck;
        }

        BizMeeting bizMeetingNew = new BizMeeting();
        BeanUtils.copyProperties(meetingVo, bizMeetingNew);
        bizMeetingNew = handleMeetingData(meetingVo, bizMeetingNew);
        logger.info("edit MEETING: meetingVo:" + meetingVo);

        //先删除，再重新入库
        bizMeetingParticipantPlanService.delete(new EntityWrapper<BizMeetingParticipantPlan>().eq("meeting_mid", bizMeetingNew.getMeetingMid()));

        //更新计划参会人员
        for (MeetingParticipantPlan participantPlan : meetingVo.getParticipantPlanList()) {

            BizMeetingParticipantPlan bizMeetingParticipantPlan = new BizMeetingParticipantPlan();
            BeanUtils.copyProperties(participantPlan,bizMeetingParticipantPlan);

            //更新MeetingMid
            bizMeetingParticipantPlan.setMeeting_mid(bizMeetingNew.getMeetingMid());
            logger.info("EDIT ParticipantPlan: ParticipantPlan:"+bizMeetingParticipantPlan);
            bizMeetingParticipantPlanService.insert(bizMeetingParticipantPlan);
        }
        return new R<>(bizMeetingService.updateById(bizMeetingNew));
    }
}
