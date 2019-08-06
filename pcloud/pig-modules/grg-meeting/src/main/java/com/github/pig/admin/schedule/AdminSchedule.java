package com.github.pig.admin.schedule;

import com.github.pig.admin.common.util.MeetingUtils;
import com.github.pig.admin.controller.MeetingController;
import com.github.pig.admin.model.entity.BizMeeting;
import com.github.pig.admin.model.entity.BizMeetingHist;
import com.github.pig.admin.model.entity.BizMeetingParticipant;
import com.github.pig.admin.service.BizMeetingHistService;
import com.github.pig.admin.service.BizMeetingParticipantService;
import com.github.pig.admin.service.BizMeetingService;
import com.github.pig.common.constant.CommonConstant;
import com.github.pig.common.constant.MeetingConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author fmsheng
 * @description 定时任务Schedule
 * @date 2018/7/18 16:44
 * @modified by
 */
@Component
@Slf4j
public class AdminSchedule {

    @Autowired
    private BizMeetingService bizMeetingService;

    @Autowired
    private BizMeetingHistService bizMeetingHistService;

    @Autowired
    private BizMeetingParticipantService bizMeetingParticipantService;


    /**
     * @param {0 5 0 * * ?}
     * @author fmsheng
     * @description 每天凌晨0点5分触发
     * @date 2018/7/24 11:34
     */
    @Scheduled(cron = "0 5 0 * * ?")
    @Transactional
    public void bizMeetingSchedule() {

        List list = getBizMeetingList();

        if (list.size() > 0 && list != null) {

//            logger.info("--------------------BizMeetingSchedule--------------------");
//            logger.info("BizMeetingSchedule: remove biz_meeting data size {} " , list.size());
            //logger.info("BizMeetingSchedule: no creation time and meeting time of more than 24 hours，biz_meeting_hist data move to biz_meeting_hist,and delete biz_meeting data");
            List<BizMeetingHist> bizMeetingHistList = list;
            List<BizMeeting> bizMeetingList = list;
            List idList = new ArrayList();

            bizMeetingList.forEach(b -> idList.add(b.getId()));
            bizMeetingHistService.insertOrUpdateBatch(bizMeetingHistList);
            bizMeetingService.deleteBatchIds(idList);
        }
    }


    /**
     * @param {0/30 * * * * ?}
     * @author fmsheng
     * @description 每30秒清理一次无心跳Participants
     * @date 2018/8/16 13:34
     */
    @Scheduled(cron = "0/30 * * * * ?")
    @Transactional
    public void lessHeartParticipantsSchedule() {

        Date latestHbTime = MeetingUtils.getStringDateBeforeThirtySeconds();

//        log.info("lessHeartParticipantsSchedule(): get currentTime {},currentTimeAfterThirtySeconds {}", new Date(), latestHbTime);

        //设置超时的成员为STATUS_LESSHEART
        bizMeetingParticipantService.deleteLessHeartParticipants(latestHbTime);

//        List<BizMeetingParticipant> bizMeetingParticipantList = bizMeetingParticipantService.selectParticipantByHbtime(latestHbTime);
//
//        if (bizMeetingParticipantList.size() > 0 && bizMeetingParticipantList != null) {
//
//            List idList = new ArrayList();
//
//            bizMeetingParticipantList.forEach(p -> idList.add(p.getId()));
//
//            //bizMeetingParticipantList.forEach(p -> p.setDelFlag(CommonConstant.STATUS_DEL));
//
//            bizMeetingParticipantList.forEach(p -> p.setStatus(MeetingConstant.STATUS_LESSHEART));
//
//            idList.forEach((id) -> MeetingController.mapEmitters.remove(id));
//
//            bizMeetingParticipantService.updateAllColumnBatchById(bizMeetingParticipantList);
//        }
    }


    /**
     * @param
     * @author fmsheng
     * @description 获取无创建时间及创建时间超过24小时的会议
     * @date 2018/7/20 18:04
     */
    public List getBizMeetingList() {
        List list = new ArrayList();
        String createdTime = MeetingUtils.getStringDateBefore();
        //List listtime = bizMeetingService.findBizMeetingByTime(createdTime);
        List listNull = bizMeetingService.findBizMeetingIsNull();
        //list.addAll(listtime);
        list.addAll(listNull);
        return list;
    }
}
