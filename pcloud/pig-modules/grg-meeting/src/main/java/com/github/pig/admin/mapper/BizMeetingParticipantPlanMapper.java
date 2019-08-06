package com.github.pig.admin.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.github.pig.admin.model.entity.BizMeetingParticipantPlan;
import com.github.pig.common.vo.MeetingParticipantPlan;

import java.util.List;

/**
 * <p>
 * 创建会议时设定的参会人员列表 Mapper 接口
 * </p>
 *
 * @author fmsheng
 * @since 2018-12-25
 */
public interface BizMeetingParticipantPlanMapper extends BaseMapper<BizMeetingParticipantPlan> {
/**
 * @author: wjqiu
 * @date: 2019-01-14
 * @description:
 */
    MeetingParticipantPlan selectParticipantPlanByMid(String meetingMid);
}
