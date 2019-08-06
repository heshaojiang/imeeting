package com.github.pig.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.admin.common.util.MeetingUtils;
import com.github.pig.admin.mapper.BizMeetingMapper;
import com.github.pig.admin.model.entity.BizMeeting;
import com.github.pig.admin.service.BizMeetingService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pig.common.constant.MeetingConstant;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.R;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.vo.MeetingParticipantPlan;
import com.github.pig.common.vo.MeetingVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 会议预约、会议召开后会生成会议信息，会议信息记录会议的基本信息 服务实现类
 * </p>
 *
 * @author grg
 * @since 2018-05-09
 */
@Service
public class BizMeetingServiceImpl extends ServiceImpl<BizMeetingMapper, BizMeeting> implements BizMeetingService {
    @Autowired
    private BizMeetingMapper bizMeetingMapper;

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(BizMeetingServiceImpl.class);

    @Override
    public BizMeeting findByMeetingId(String meetingId) {
//        log.info("调用数据库的数据");
        return bizMeetingMapper.findByMeetingId(meetingId);
    }

    @Override
    public MeetingVo findMeetingVoByMeetingId(String meetingId) {
//        log.info("调用数据库的数据");
        return bizMeetingMapper.findMeetingVoByMeetingId(meetingId);
    }

    @Override
    public boolean updateCompere(BizMeeting bizMeeting) {
        int result=bizMeetingMapper.updateCompere(bizMeeting);
        if(result==1){
            return true;
        }
        return false;
    }

    /**
     * @author fmsheng
     * @param
     * @description  获取无创建时间及创建时间超过24小时的会议
     * @date 2018/7/20 9:09
     */
    @Override
    public List<BizMeeting> findBizMeetingByTime(String createdTime) {
        log.info("findBizMeetingByTime: Get a meeting that has been created for more than 24 hours");
        return  bizMeetingMapper.findBizMeetingByTime(createdTime);
    }


    /**
     * @author fmsheng
     * @param
     * @description  获取无创建时间及创建时间超过24小时的会议
     * @date 2018/7/20 9:09
     */
    @Override
    public List<BizMeeting> findBizMeetingIsNull() {
        log.info("findBizMeetingIsNull: Get no creation time");
        return  bizMeetingMapper.findBizMeetingIsNull();
    }

    @Override
    public Page selectMeetingPage(Query<Object> query, EntityWrapper<Object> objectEntityWrapper) {
        query.setRecords(bizMeetingMapper.selectMeetingPage(query, query.getCondition()));
        return query;
    }

    @Override
    public Page selectComingMeeting(Query<Object> query, EntityWrapper<Object> objectEntityWrapper) {
        //查询出指定用户有配置在参会人员列表中的会议
        List<Object> meetingList = bizMeetingMapper.selectMeetingWithUserID(query, query.getCondition());
        //查询出没有配置参会人员的会议（即允许所有人入会的会议）
        List<Object> meetingListNoParticipant = bizMeetingMapper.selectMeetingNoParticipant(query, query.getCondition());
        meetingList.addAll(meetingListNoParticipant);
        query.setRecords(meetingList);
        return query;
    }

    @Override
    public boolean insert(BizMeeting bizMeeting) {
        int result = bizMeetingMapper.insert(bizMeeting);
        if(result==1){
            return true;
        }
        return false;
    }
    @Override
    public MeetingVo selectVoById(Integer id) {
        MeetingVo meetingVo = bizMeetingMapper.SelectVoById(id);
        Map map;
        if (meetingVo != null) {
            if (meetingVo.getExternConfigs() == null) return meetingVo;
            map = JSON.parseObject(meetingVo.getExternConfigs());
            if (map.get("compereDpi") != null) meetingVo.setCompereDpi(map.get("compereDpi").toString());
            meetingVo.setCompereVideoEnable(map.get("compereVideoEnable").toString().equals("true"));
            meetingVo.setCompereAudioEnable(map.get("compereAudioEnable").toString().equals("true"));
            meetingVo.setParticipantVideoEnable(map.get("participantVideoEnable").toString().equals("true"));
            meetingVo.setParticipantAudioEnable(map.get("participantAudioEnable").toString().equals("true"));
            if (map.get("participantDpi") != null) meetingVo.setParticipantDpi(map.get("participantDpi").toString());
            if (map.get("showMode") != null) meetingVo.setShowMode(map.get("showMode").toString());
        }
        return meetingVo;
    }

    public Boolean checkNameExist(String name){
        Boolean exist = false;
        if (selectOne(new EntityWrapper<BizMeeting>().eq("meeting_name", name)) != null){
            exist = true;
        }
        return exist;
    }
    /**
     * @author: wjqiu
     * @date: 2019-01-17
     * @description: 检查会议信息是否准确
     * 1、会议名称不能重复
     * 2、最大参会方限制
     * 3、最大通话方限制
     * 4、默认分屏数量需大于参会方数量加一
     */
    @Override
    public R<Boolean> checkMeeting(MeetingVo meetingVoNew,BizMeeting bizMeetingOri) {
        if (bizMeetingOri == null){
            bizMeetingOri = new BizMeeting();
        }
        //检查会议号
        String meetingId = meetingVoNew.getMeetingId();
        log.info("checkMeeting meetingId:"+meetingId+" Ori:"+bizMeetingOri.getMeetingId());
        if (StringUtils.isEmpty(meetingId)) {
            return new R<>(Boolean.FALSE, RespCode.CNSL_MEETING_ID_EMPTY);
        } else if (!meetingId.equals(bizMeetingOri.getMeetingId())){
            if (findMeetingVoByMeetingId(meetingId) != null){
                return new R<>(Boolean.FALSE, RespCode.CNSL_MEETING_ID_EXIST);
            }
        }


        //检查会议名称是否存在。只检查在biz_meeting表的数据。不需要去检查biz_meeting_hist
//        String nameNew = meetingVoNew.getMeetingName();
//        log.info("checkMeeting nameNew:"+nameNew+" OriName:"+bizMeetingOri.getMeetingName());
//        if (StringUtils.isEmpty(nameNew)) {
//            return new R<>(Boolean.FALSE, RespCode.CNSL_NAME_EMPTY);
//        } else if (!nameNew.equals(bizMeetingOri.getMeetingName())){
//            if (checkNameExist(nameNew)){
//                return new R<>(Boolean.FALSE, RespCode.CNSL_NAME_EXIST);
//            }
//        }

        //检查通话方与参会方数量
        int splitNum = MeetingUtils.getSplitTypeScreenNum(meetingVoNew.getSplitType());
        int videoNum = meetingVoNew.getNumVideo()!=null ? meetingVoNew.getNumVideo():0;
        int joinNum = meetingVoNew.getNumJoin()!=null ? meetingVoNew.getNumJoin():0;

        log.info("checkMeeting splitNum:"+splitNum+" videoNum:"+videoNum+" joinNum:"+joinNum);
        if (splitNum > 0 || videoNum > 0 || joinNum > 0) {

            int joinNumPlan = 0;
            int videoNumPlan = 0;

            //计划参会人员
            for (MeetingParticipantPlan participantPlan : meetingVoNew.getParticipantPlanList()) {
                String joinType = participantPlan.getJoin_type() == null ? MeetingConstant.JOIN_TYPE_Join:participantPlan.getJoin_type();
                if (MeetingConstant.JOIN_TYPE_Join.equals(joinType)){
                    joinNumPlan ++;
                }
                if (MeetingConstant.JOIN_TYPE_Video.equals(joinType)){
                    videoNumPlan ++;
                }
            }
            log.info("checkMeeting joinNumPlan:"+joinNumPlan+" videoNumPlan:"+videoNumPlan);
            if (joinNum > 0 && joinNumPlan > joinNum){
                //计划参会人员中接入方数量大于会议最大接入方数量
                log.warn("checkMeeting joinNumPlan > joinNum");
                return new R<>(Boolean.FALSE, RespCode.CNSL_MAX_JOIN_NUMS);
            }
            if (videoNum > 0 && videoNumPlan > videoNum){
                //计划参会人员中通话方数量大于会议最大通话方数量
                log.warn("checkMeeting videoNumPlan > videoNum");
                return new R<>(Boolean.FALSE, RespCode.CNSL_MAX_VIDEO_NUMS);
            }
//            if (splitNum > 2 && joinNumPlan > 0) {
//                //分屏数大于2时才判断
//                if (joinNumPlan >= splitNum) {
//                    //默认分屏数量需大于通话方数量加一
//                    log.warn("checkMeeting videoNumPlan >= splitNum");
//                    return new R<>(Boolean.FALSE, RespCode.CNSL_SPLIT_NUM_LESS_VIDEO_NUM);
//                }
//            }
        }

        log.info("checkMeeting OK");
        return new R<>(Boolean.TRUE);
    }
}
