package com.github.pig.admin.service;


import com.baomidou.mybatisplus.service.IService;
import com.github.pig.admin.model.entity.BizMeetingParticipant;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author bywei
 * @since 2018-05-12
 */
public interface BizMeetingParticipantService extends IService<BizMeetingParticipant> {
    Boolean add (BizMeetingParticipant bizMeetingParticipant);

    Boolean deleteBySId(String id);

    List<BizMeetingParticipant> selectAllList(String meetingId);

    Boolean updateParticipantTimeAndStatusById(BizMeetingParticipant bizMeetingParticipant);

    BizMeetingParticipant selectParticipantByIdAndHbtime (BizMeetingParticipant bizMeetingParticipant);

    Boolean updateParticipantStatusById(BizMeetingParticipant bizMeetingParticipant);

    Boolean deleteLessHeartParticipants(Date latestHbTime);

    List<BizMeetingParticipant> selectParticipantByHbtime(Date latestHbTime);

    Integer countParticipantOnLine(String meetingMid);
    Integer countMeetingOnLine();
}
