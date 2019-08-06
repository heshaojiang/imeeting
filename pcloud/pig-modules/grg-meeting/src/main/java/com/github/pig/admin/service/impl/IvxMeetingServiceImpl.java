package com.github.pig.admin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pig.admin.common.license.LicenseManage;
import com.github.pig.admin.common.meeting.Meeting;
import com.github.pig.admin.common.meeting.ProtocolElements;
import com.github.pig.common.util.exception.GrgException;
import com.github.pig.admin.common.util.MD5;
import com.github.pig.admin.common.util.MeetingUtils;
import com.github.pig.admin.model.entity.BizMeeting;
import com.github.pig.admin.model.entity.BizMeetingParticipant;
import com.github.pig.admin.service.BizMeetingParticipantPlanService;
import com.github.pig.admin.service.BizMeetingParticipantService;
import com.github.pig.admin.service.BizMeetingService;
import com.github.pig.admin.service.IvxMeetingService;
import com.github.pig.common.constant.CommonConstant;
import com.github.pig.common.constant.MeetingConstant;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.vo.UserVo;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * <p>
 * 会议服务实现类
 * </p>
 *
 * @author xuesen
 * @since 2018-03-26
 */
@Service
public class IvxMeetingServiceImpl implements IvxMeetingService {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(IvxMeetingServiceImpl.class);
    @Autowired
    private BizMeetingParticipantService bizMeetingParticipantService;
    @Autowired
    private BizMeetingParticipantPlanService bizMeetingParticipantPlanService;
    @Autowired
    private BizMeetingService bizMeetingService;
    @Autowired
    private LicenseManage licenseManage;
    @Value("${openvidu.secret}")
    String openvidu_secret;
    /**
     * 新建会议
     * @param ivxMeetingSessionName sessionName
     * @return 新建会议结果
     */
    public Meeting newMeeting(String ivxMeetingSessionName) {
        Meeting meeting = new Meeting();
        meeting.setId(2);
        meeting.setName("ddddAngel");
        return meeting;
    }

    /**
     * 加入会议
     *
     * @param ivxMeetingSessionName sessionName
     * @return 加入会议结果
     */
    public Meeting joinMeeting(String ivxMeetingSessionName) {
        log.info("sessionName: " + ivxMeetingSessionName);

        Meeting meeting = new Meeting();
        meeting.setId(2);
        meeting.setName("ddddAngel");
        return meeting;
    }

    /**
     * 退出会议
     * @param ivxMeetingSessionName sessionName
     * @return 退出会议结果
     */
    public Meeting exitMeeting(String ivxMeetingSessionName) {
        Meeting meeting = new Meeting();
        meeting.setId(2);
        meeting.setName("ddddAngel");
        return meeting;
    }

    /**
     * 静音
     * @param from/to dest/src participantId
     * @return 无
     */
    public String muteMic(String from, String to) {
        String msg = getEventRequest(from, to, ProtocolElements.SSE_MUTE_REQUEST_METHOD);
        return msg;
    }

    /**
     * 取消静音
     * @param from/to dest/src participantId
     * @return 无
     */
    public String unmuteMic(String from, String to) {
        String msg = getEventRequest(from, to, ProtocolElements.SSE_UNMUTE_REQUEST_METHOD);
        return msg;
    }

    /**
     * 请求发言
     * @param requestId ivxMeetingParticipantId
     * @return  无
     */
    public String requestSpeak(String requestId) {
        // add params
        JSONObject paramsJson = new JSONObject();
        paramsJson.put("requestId", requestId);

        // pack event resp
        JSONObject sseRespJson = new JSONObject();
        sseRespJson.put("method", ProtocolElements.SSE_SPEAK_REQUEST_METHOD);
        sseRespJson.put("params", paramsJson);

        return sseRespJson.toString();
    }

    /**
     * 允许或/拒绝发言
     *
     * @param isApprove
     * @param hostId
     * @return 无
     */
    public String approveSpeak(String isApprove, String userId, String hostId) {
        // add params
        JSONObject paramsJson = new JSONObject();
        paramsJson.put("isApproved", isApprove);
        paramsJson.put("from", hostId);
        paramsJson.put("to", userId);

        // pack event resp
        JSONObject sseRespJson = new JSONObject();
        sseRespJson.put("method", ProtocolElements.SSE_SPEAK_APPROVE_METHOD);
        sseRespJson.put("params", paramsJson);

        return sseRespJson.toString();
    }

    /**
     * 申请主持
     * @param from/to dest/src participantId
     * @return 无
     */
    public String requestHost(String from, String to) {
        String msg = getEventRequest(from, to, ProtocolElements.SSE_HOST_APPLY_METHOD);
        return msg;
    }

    /**
     * 申请主持密码
     * @param /to dest/src participantId
     * @return 无
     */
    public String requestHostPwd(String from, String to) {
        String msg = getEventRequest(from, to, ProtocolElements.SSE_HOST_REQUEST_PWD_METHOD);
        return msg;
    }

    /**
     * 允许或拒绝主持
     *
     * @param  id   申请者ID
     * @param isApproved 批准或拒绝
     * @return 无
     */
    public String approveHost(String id, String isApproved) {
        // add params
        JSONObject paramsJson = new JSONObject();
        paramsJson.put("isApproved", isApproved);
        paramsJson.put("to", id);

        // pack event resp
        JSONObject sseRespJson = new JSONObject();
        sseRespJson.put("method", ProtocolElements.SSE_HOST_APPROVE_METHOD);
        sseRespJson.put("params", paramsJson);

        return sseRespJson.toString();
    }

    /**
     * 指定主持
     * @param id 指定者ID
     * @return 无
     */
    public String assignHost(String id) {
        // add params
        JSONObject paramsJson = new JSONObject();
        paramsJson.put("to", id);

        // pack event resp
        JSONObject sseRespJson = new JSONObject();
        sseRespJson.put("method", ProtocolElements.SSE_HOST_ASSIGN_METHOD);
        sseRespJson.put("params", paramsJson);

        return sseRespJson.toString();
    }

    /**
     * 共享桌面
     * @param ivxMeetingParticipantId ivxMeetingParticipantId
     * @return 无
     */
    public Meeting shareDesktop(String ivxMeetingParticipantId) {
        Meeting meeting = new Meeting();
        meeting.setId(2);
        meeting.setName("ddddAngel");
        return meeting;
    }

    /**
     * 获取会议URL
     * @param ivxMeetingSessionName ivxMeetingSessionName
     * @return 会议URL
     */
    public Meeting getMeetingUrl(String ivxMeetingSessionName) {
        Meeting meeting = new Meeting();
        meeting.setId(2);
        meeting.setName("ddddAngel");
        return meeting;
    }

    /**
     * 获取会议信息
     * @param ivxMeetingSessionName ivxMeetingSessionName
     * @return 会议信息
     */
    public Meeting getMeetingInfo(String ivxMeetingSessionName) {
        Meeting meeting = new Meeting();
        meeting.setId(2);
        meeting.setName("ddddAngel");
        return meeting;
    }

    /**
     * 获取与会者列表
     * @param ivxMeetingSessionName ivxMeetingSessionName
     * @return 与会者列表
     */
    public Meeting getParticipantList(String ivxMeetingSessionName) {
        Meeting meeting = new Meeting();
        meeting.setId(2);
        meeting.setName("ddddAngel");
        return meeting;
    }

    /**
     * 上传ngrok穿透信息
     * @param ivxParticipantId ivxParticipantId
     * @param ivxNgrokUrl ivxNgrokUrl
     * @return 无
     */
    public Meeting updateNgrokINfo(String ivxParticipantId, String ivxNgrokUrl) {
        Meeting meeting = new Meeting();
        meeting.setId(2);
        meeting.setName("ddddAngel");
        return meeting;
    }

    /**
     * 请求VNC控制
     * @param ivxParticipantId ivxParticipantId
     * @return 无
     */
    public Meeting requestVNCControl(String ivxParticipantId) {
        Meeting meeting = new Meeting();
        meeting.setId(2);
        meeting.setName("ddddAngel");
        return meeting;
    }

    /**
     * 批转或拒绝VNC控制
     * @param ivxParticipantId ivxParticipantId
     * @return 无
     */
    public Meeting applyVNCControl(String ivxParticipantId) {
        Meeting meeting = new Meeting();
        meeting.setId(2);
        meeting.setName("ddddAngel");
        return meeting;
    }

    /**
     * 发送消息
     *
     * @param message
     * @param from /to from/to participant
     * @param to
     * @param userName
     * @return
     */
    @Override
    public String sendMessage(String message, String from, String to, String userName) {
        String msg = getMessageRequest(from, to, message,userName);
        return msg;
    }

    /**
     * 主持密码错误
     * @param userId
     * @return
     */
    @Override
    public String invalidHostPwd(String userId) {
        // pack event resp
        JSONObject sseRespJson = new JSONObject();
        sseRespJson.put("method", ProtocolElements.SSE_HOST_INVALIDPWD_METHOD);

        return sseRespJson.toString();
    }

    @Override
    public String closeVideo(String from, String to) {
        String msg = getEventRequest(from, to, ProtocolElements.SSE_CLOSEVIDEO_REQUEST_METHOD);
        return msg;
    }

    @Override
    public String openVideo(String from, String to) {
        String msg = getEventRequest(from, to, ProtocolElements.SSE_OPENVIDEO_REQUEST_METHOD);
        return msg;
    }

    @Override
    public String giveUpHost(String hostId) {
        String msg = getEventRequest(hostId, "", ProtocolElements.SSE_HOST_GIVEUP_METHOD);
        return msg;
    }

    //
    private String getEventRequest(String from, String to, String method) {
        // add params
        JSONObject paramsJson = new JSONObject();
        paramsJson.put("from", from);
        paramsJson.put("to", to);

        // pack event resp
        JSONObject sseRespJson = new JSONObject();
        sseRespJson.put("method", method);
        sseRespJson.put("params", paramsJson);

        return sseRespJson.toString();
    }

    private String getMessageRequest(String from, String to, String message, String fromName) {
        // add params
        JSONObject paramsJson = new JSONObject();
        paramsJson.put("from", from);
        paramsJson.put("to", to);
        paramsJson.put("fromName", fromName);

        // pack event resp
        JSONObject sseRespJson = new JSONObject();
        sseRespJson.put("message", message);
        sseRespJson.put("params", paramsJson);

        return sseRespJson.toString();
    }


    private String getEventReply(String from, String to, String method, boolean stat) {
        return "";
    }


/**
 * @auther: wjqiu
 * @date: 2019-01-07
 * @description :
 */
    @Override
    public boolean checkOnLine(String compereId) {

        //判断主席是否为空
        if (StringUtils.isEmpty(compereId)) {
            log.info("checkOnLine {}: compereId isEmpty");
            return false;
        }

        // 当前时间减去十五秒
        Date currentTimeAfterTenSeconds = MeetingUtils.getStringDateAfterTenSeconds();
        // 判断当前会议主持人是否已掉线
        BizMeetingParticipant bizMeetingParticipant = bizMeetingParticipantService.selectOne(new EntityWrapper<BizMeetingParticipant>().eq("compere_id", compereId).ge("latest_hbtime", currentTimeAfterTenSeconds).eq(CommonConstant.DEL_FLAG, CommonConstant.STATUS_NORMAL).eq("status", MeetingConstant.STATUS_HEART));
        log.info("checkOnLine {}: get currentTimeAfterTenSeconds {},latestHbTime {}", compereId, currentTimeAfterTenSeconds, bizMeetingParticipant != null ? bizMeetingParticipant.getLatestHbTime() : "");
        if (bizMeetingParticipant == null) {
            log.info("checkOnLine {}: participant off line");
            return false;
        } else {
            log.info("checkOnLine {}: participant on line");
            return true;
        }
    }

    @Override
    public boolean checkHostOnLine(BizMeeting bizMeeting) {
        // get hostId
        return checkOnLine(bizMeeting.getCompereId());
    }

    @Override
    public boolean setHostEmpty(BizMeeting bizMeeting) {
        // get hostId
        String compereId = bizMeeting.getCompereId();
        // 已有主席，但无心跳,设置主持为空
        if (!StringUtils.isEmpty(compereId)) {
            bizMeeting.setCompereId("");
            bizMeeting.setCompere("");
            boolean result = bizMeetingService.updateById(bizMeeting);
            if (result){
                //清理参会人员表中记录的主持人信息
                BizMeetingParticipant participant = bizMeetingParticipantService.selectOne(new EntityWrapper<BizMeetingParticipant>()
                        .eq("compere_id", compereId)
                        .eq("del_flag", CommonConstant.STATUS_NORMAL));
                if (participant != null){
                    participant.setCompereId("");
                    result = bizMeetingParticipantService.updateById(participant);
                }
            }

            return result;
        }
        return true;
    }
    @Override
    public RespCode permissonVerify(BizMeeting bizMeeting, UserVo userVo, String pwd, String secret) {
        RespCode respCode = RespCode.SUCCESS;
        try {
            if (bizMeeting == null){
                log.warn("permissonVerify(): bizMeeting == null");
                throw new GrgException(RespCode.ERROR);
            }
            boolean bNeedCheckPasswrd = true;
            boolean isAnonymous = false;
            if (userVo == null) {
                isAnonymous = true;
            }
            //检查是否是使用openvidu secret的方式入会，使用secret方式入会（用于融屏），则不需要检查密码
            if (openvidu_secret.equals(secret)){
                log.info("is in openvidu secret do not check meeting password");
                bNeedCheckPasswrd = false;
            }
            //检查是否需要验证密码
            if (!isAnonymous &&
                    userVo !=null &&
                    bizMeetingParticipantPlanService.CheckIsPlanParticipant(bizMeeting.getMeetingMid(),userVo.getUserId()))
            {
                log.info("user:{} is in ParticipantPlan do not check meeting password",userVo.getUsername());
                bNeedCheckPasswrd = false;
            }
            String meetingPwd = bizMeeting.getMeetingPwd();
            if (StringUtils.isEmpty(meetingPwd)){
                log.info("the meeting do not need password!");
                bNeedCheckPasswrd = false;
            }
            if (bNeedCheckPasswrd) {
                log.info("bizMeeting psw: " + meetingPwd + " pwd:"+pwd);
                //需要密码验证
                if (StringUtils.isEmpty(pwd)) {
                    log.warn("permissonVerify(): password not exist");
                    throw new GrgException(RespCode.IME_REQUESTPWD);
                } else {
                    //密码错误
                    if (!pwd.equals(MD5.getMD5(bizMeeting.getMeetingPwd()))) {
                        log.warn("permissonVerify(): password validation error");
                        throw new GrgException(RespCode.IME_INVALIDPWD);
                    }
                }
            }

            //检查是否允许匿名登录
            log.info("MeetingType: " + bizMeeting.getMeetingType() + " userVo:"+userVo);
            if (bizMeeting.getMeetingType().equals(MeetingConstant.MEETING_TYPE_FREEDOM)){
                //自由会议模式下不允许匿名登录
                if (isAnonymous){
                    log.warn("permissonVerify(): can not be anonymous when MEETING_TYPE_FREEDOM!");
                    //FIXME 暂时关闭校验
//                    throw new GrgException(RespCode.IME_NO_ANONYMOUS);
                }


            }
            //检查已入会人数
            Integer countParticipant = bizMeetingParticipantService.countParticipantOnLine(bizMeeting.getMeetingMid());
            Integer MaxNumJoin = bizMeeting.getNumJoin();
            if (MaxNumJoin == null) MaxNumJoin = 0;
            log.info("countParticipant: " + countParticipant + " MaxNumJoin:"+MaxNumJoin);
            if (MaxNumJoin != 0 && countParticipant >= MaxNumJoin){
                log.warn("count of Participant Is Over MaxNumJoin!");
                throw new GrgException(RespCode.IME_MAX_JOIN_NUMS);
            }
            //检查是否需要判断计划参会人员列表


        }  catch (GrgException e) {
            log.error("permissonVerify GrgException: " + e.getMessage());
            respCode = e.getStatusCode();
        } catch (Exception e) {
            log.error("permissonVerify Exception: " + e.getMessage());
            e.printStackTrace();
            respCode = RespCode.ERROR;
        }
        return respCode;
    }

    @Override
    public RespCode licenseVerify() {
        RespCode respCode = RespCode.SUCCESS;
        //查询当前所有会议的总入会成员数
        Integer countParticipant = bizMeetingParticipantService.countParticipantOnLine(null);
        //查询当前正在开会的会议数
        Integer countMeeting = bizMeetingParticipantService.countMeetingOnLine();
        int nParticipantCount = (countParticipant == null) ? 0 : countParticipant;
        int nMeetingCount = (countMeeting == null) ? 0 : countMeeting;
        respCode = licenseManage.licenseVerfify(nParticipantCount,nMeetingCount);

        return respCode;
    }

    @Override
    public String createMeetingMid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    @Override
    public void initMeeting(BizMeeting bizMeeting) {

        //会议标识
        bizMeeting.setMeetingMid(createMeetingMid());
        //创建时间
        bizMeeting.setCreatedTime(new Date());
        //0 - 进行中； 1 - 预约； 2 - 完成
        if (StringUtils.isEmpty(bizMeeting.getStatus())){
            bizMeeting.setStatus(MeetingConstant.MEETING_STATUS_NORMAL);
        }

        //设置密码
        if(StringUtils.isEmpty(bizMeeting.getComperePwd())){
            //默认密码
            bizMeeting.setComperePwd(MeetingConstant.MEETING_COMPERE_PWD);
        }
//        if(StringUtils.isEmpty(bizMeeting.getMeetingPwd())){
//            //默认入会密码
//            bizMeeting.setMeetingPwd(MeetingConstant.MEETING_JOIN_PWD);
//        }
        //正常
        bizMeeting.setDelFlag(CommonConstant.STATUS_NORMAL);

    }

    @Override
    public boolean checkNickname(String meetingMid, String nickName) {
        if (bizMeetingParticipantService.selectOne(new EntityWrapper<BizMeetingParticipant>().eq("meeting_mid", meetingMid).eq("nickname", nickName).eq(CommonConstant.DEL_FLAG, CommonConstant.STATUS_NORMAL).eq("status", MeetingConstant.STATUS_HEART)) == null) {
            return false;
        } else {
            return true;
        }
     }

}