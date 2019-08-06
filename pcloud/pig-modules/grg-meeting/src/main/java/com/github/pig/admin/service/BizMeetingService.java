package com.github.pig.admin.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.github.pig.admin.model.entity.BizMeeting;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.R;
import com.github.pig.common.vo.MeetingVo;

import java.util.List;

/**
 * <p>
 * 会议预约、会议召开后会生成会议信息，会议信息记录会议的基本信息 服务类
 * </p>
 *
 * @author grg
 * @since 2018-05-09
 */
public interface BizMeetingService extends IService<BizMeeting> {

    BizMeeting findByMeetingId(String meetingId);

    MeetingVo findMeetingVoByMeetingId(String meetingId);

    MeetingVo selectVoById(Integer id);

    boolean updateCompere(BizMeeting bizMeeting);

    List<BizMeeting> findBizMeetingByTime(String createdTime);

    List<BizMeeting>  findBizMeetingIsNull();

    /**
     *
     *
     * @param objectQuery         查询条件
     * @param objectEntityWrapper wapper
     * @return page
     */
    Page selectMeetingPage(Query<Object> objectQuery, EntityWrapper<Object> objectEntityWrapper);
    Page selectComingMeeting(Query<Object> objectQuery, EntityWrapper<Object> objectEntityWrapper);

    /**
     * @author: wjqiu
     * @date: 2019-01-17
     * @description: 检查会议信息是否准确
     * 1、会议名称不能重复
     * 2、最大参会方限制
     * 3、最大通话方限制
     * 4、默认分屏数量需大于参会方数量加一
     */
    R<Boolean> checkMeeting(MeetingVo meetingVo,BizMeeting bizMeetingOri);

}
