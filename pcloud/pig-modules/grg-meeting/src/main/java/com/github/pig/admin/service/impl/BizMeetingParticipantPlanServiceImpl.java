package com.github.pig.admin.service.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pig.admin.mapper.BizMeetingParticipantPlanMapper;
import com.github.pig.admin.model.entity.BizMeetingParticipantPlan;
import com.github.pig.admin.service.BizMeetingParticipantPlanService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 创建会议时设定的参会人员列表 服务实现类
 * </p>
 *
 * @author fmsheng
 * @since 2018-12-25
 */
@Service
public class BizMeetingParticipantPlanServiceImpl extends ServiceImpl<BizMeetingParticipantPlanMapper, BizMeetingParticipantPlan> implements BizMeetingParticipantPlanService {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(BizMeetingParticipantPlanServiceImpl.class);
    public boolean CheckIsPlanParticipant(String meeting_mid, Integer userid) {
        log.debug("CheckIsPlanParticipant meeting_mid:{},userid:{}",meeting_mid,userid);
        BizMeetingParticipantPlan participant = selectOne(new EntityWrapper<BizMeetingParticipantPlan>()
                .eq("user_id", userid)
                .eq("meeting_mid", meeting_mid));
        if (participant != null){
            return true;
        }

        return false;
    }
}
