package com.github.pig.admin.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.github.pig.admin.model.entity.BizMeeting;
import com.github.pig.common.util.Query;
import com.github.pig.common.vo.MeetingVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 会议预约、会议召开后会生成会议信息，会议信息记录会议的基本信息 Mapper 接口
 * </p>
 *
 * @author grg
 * @since 2018-05-09
 */
public interface BizMeetingMapper extends BaseMapper<BizMeeting> {

    BizMeeting findByMeetingId(String meetingId);

    MeetingVo findMeetingVoByMeetingId(String meetingId);

    int updateCompere(BizMeeting bizMeeting);

    List<BizMeeting> findBizMeetingByTime(String createdTime);

    List<BizMeeting> findBizMeetingIsNull();

    /**
     *
     * @param query 查询对象
     * @param condition 条件
     * @return List
     */
    List<Object> selectMeetingPage(Query<Object> query, Map<String, Object> condition);
    List<Object> selectMeetingWithUserID(Query<Object> query, Map<String, Object> condition);
    List<Object> selectMeetingNoParticipant(Query<Object> query, Map<String, Object> condition);

    MeetingVo SelectVoById(Integer id);

}
