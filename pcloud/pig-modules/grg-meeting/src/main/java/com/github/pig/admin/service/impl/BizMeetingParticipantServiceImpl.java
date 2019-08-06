package com.github.pig.admin.service.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pig.admin.mapper.BizMeetingParticipantMapper;
import com.github.pig.admin.model.entity.BizMeetingParticipant;
import com.github.pig.admin.service.BizMeetingParticipantService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author bywei
 * @since 2018-05-12
 */
@Service
public class BizMeetingParticipantServiceImpl extends ServiceImpl<BizMeetingParticipantMapper, BizMeetingParticipant> implements BizMeetingParticipantService {

    @Autowired
    private BizMeetingParticipantMapper bizMeetingParticipantMapper;

    @Override
    @Transactional
    public Boolean add(BizMeetingParticipant bizMeetingParticipant) {
        int result = bizMeetingParticipantMapper.insert(bizMeetingParticipant);
        if (result == 1) {
            return true;
        }
        
        return false;
    }

    @Override
    public Boolean deleteBySId(String id) {
        int result = bizMeetingParticipantMapper.deleteById(id);
        if (result == 1) {
            return true;
        }
        return false;
    }

    @Override
    public List<BizMeetingParticipant> selectAllList(String meetingId) {
        return bizMeetingParticipantMapper.selectAllList(meetingId);
    }

    @Override
    public Boolean updateParticipantTimeAndStatusById(BizMeetingParticipant bizMeetingParticipant) {

        int result = bizMeetingParticipantMapper.updateParticipantTimeAndStatusById(bizMeetingParticipant);

        if (result == 1) {
            return true;
        }
        return false;
    }

    public BizMeetingParticipant selectParticipantByIdAndHbtime(BizMeetingParticipant bizMeetingParticipant) {
        return bizMeetingParticipantMapper.selectParticipantByIdAndHbtime(bizMeetingParticipant);
    }

    @Override
    public Boolean updateParticipantStatusById(BizMeetingParticipant bizMeetingParticipant) {

        int result = bizMeetingParticipantMapper.updateParticipantStatusById(bizMeetingParticipant);

        if (result == 1) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteLessHeartParticipants(Date latestHbTime)
    {
        int result = bizMeetingParticipantMapper.deleteLessHeartParticipants(latestHbTime);
        if (result == 1) {
            return true;
        }
        return false;
    }

    @Override
    public List<BizMeetingParticipant> selectParticipantByHbtime(Date latestHbTime)
    {
        return bizMeetingParticipantMapper.selectParticipantByHbtime(latestHbTime);
    }

    @Override
    public Integer countParticipantOnLine(String meetingMid) {
        return bizMeetingParticipantMapper.countParticipantOnLine(meetingMid);
    }    
	@Override
    public Integer countMeetingOnLine() {
        return bizMeetingParticipantMapper.countMeetingOnLine();
    }
}