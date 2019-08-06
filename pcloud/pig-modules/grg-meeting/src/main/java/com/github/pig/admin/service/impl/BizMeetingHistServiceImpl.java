package com.github.pig.admin.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pig.admin.mapper.BizMeetingHistMapper;
import com.github.pig.admin.mapper.BizMeetingMapper;
import com.github.pig.admin.mapper.BizMeetingParticipantMapper;
import com.github.pig.admin.model.entity.BizMeeting;
import com.github.pig.admin.model.entity.BizMeetingHist;
import com.github.pig.admin.model.entity.BizMeetingParticipant;
import com.github.pig.admin.service.BizMeetingHistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 无创建时间、创建时间超过24小时的会议及会议已结束 服务实现类
 * </p>
 *
 * @author fmsheng
 * @since 2018-07-18
 */
@Service
public class BizMeetingHistServiceImpl extends ServiceImpl<BizMeetingHistMapper, BizMeetingHist> implements BizMeetingHistService {

    @Autowired
    private BizMeetingHistMapper bizMeetingHistMapper;

}
