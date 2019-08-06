package com.github.pig.admin.service;

import com.baomidou.mybatisplus.service.IService;
import com.github.pig.admin.model.entity.BizMeetingParticipantPlan;

/**
 * <p>
 * 创建会议时设定的参会人员列表 服务类
 * </p>
 *
 * @author fmsheng
 * @since 2018-12-25
 */
public interface BizMeetingParticipantPlanService extends IService<BizMeetingParticipantPlan> {
    boolean CheckIsPlanParticipant(String meeting_mid, Integer userid);
}
