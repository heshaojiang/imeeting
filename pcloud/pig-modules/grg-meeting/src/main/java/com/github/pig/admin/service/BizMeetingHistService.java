package com.github.pig.admin.service;

import com.baomidou.mybatisplus.service.IService;
import com.github.pig.admin.model.entity.BizMeeting;
import com.github.pig.admin.model.entity.BizMeetingHist;

import java.util.List;

/**
 * <p>
 * 无创建时间、创建时间超过24小时的会议及会议已结束 服务类
 * </p>
 *
 * @author fmsheng
 * @since 2018-07-18
 */
public interface BizMeetingHistService extends IService<BizMeetingHist> {

}
