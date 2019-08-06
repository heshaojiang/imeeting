package com.github.pig.admin.mapper;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.github.pig.admin.model.entity.BizMeetingParticipant;
import com.github.pig.common.vo.MeetingParticipant;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author bywei
 * @since 2018-05-12
 */
public interface BizMeetingParticipantMapper extends BaseMapper<BizMeetingParticipant> {
    /**
     * 更新
     *
     * @param BizMeetingParticipant
     * @return
     */
//    Integer updateById(BizMeetingParticipant BizMeetingParticipant);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    Integer deleteBySId(int id);

    List<BizMeetingParticipant> selectAllList(String meetingId);

    /**
     * @param
     * @author fmsheng
     * @description 更新心跳最新时间
     * @date 2018/8/2 18:14
     */
    Integer updateParticipantTimeAndStatusById(BizMeetingParticipant BizMeetingParticipant);

    /**
     * @param
     * @author fmsheng
     * @description 查询超过3次没有收到心跳
     * @date 2018/8/2 18:15
     */
    BizMeetingParticipant selectParticipantByIdAndHbtime(BizMeetingParticipant BizMeetingParticipant);

    /**
     * @param
     * @author fmsheng
     * @description 更新Participant状态
     * @date 2018/8/2 18:15
     */
    Integer updateParticipantStatusById(BizMeetingParticipant BizMeetingParticipant);

    /**
     * @param
     * @author fmsheng
     * @description 删除无心跳Participants
     * @date 2018/8/2 18:15
     */
    Integer deleteLessHeartParticipants(Date latestHbTime);

    /**
     * @param
     * @author fmsheng
     * @description 获取无心跳Participants
     * @date 2018/8/2 18:15
     */
    List<BizMeetingParticipant> selectParticipantByHbtime(Date latestHbTime);

    /**
     * @author: wjqiu
     * @date: 2019-01-07
     * @description:
     */
    Integer countParticipantOnLine(@Param("meetingMid") String meetingMid);
    /**
     * @author: wjqiu
     * @date: 2019-02-26
     * @description: 统计当前进行中的会议数量
     */
    Integer countMeetingOnLine();

    MeetingParticipant selectParticipantByMid(String meetingMid);
}
