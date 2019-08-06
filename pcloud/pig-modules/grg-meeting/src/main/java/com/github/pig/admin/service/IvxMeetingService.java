package com.github.pig.admin.service;

import com.github.pig.admin.common.meeting.Meeting;
import com.github.pig.admin.model.entity.BizMeeting;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.vo.UserVo;

/**
 * <p>
 *  会议服务类
 * </p>
 *
 * @author xuesen
 * @since 2018-03-26
 */
public interface IvxMeetingService {
    /**
     * 新建会议
     * @param ivxMeetingSessionName sessionName
     * @return 新建会议结果
     */
    public Meeting newMeeting(String ivxMeetingSessionName);

    /**
     * 加入会议
     * @param ivxMeetingSessionName sessionName
     * @return 加入会议结果
     */
    public Meeting joinMeeting(String ivxMeetingSessionName);

    /**
     * 退出会议
     * @param ivxMeetingSessionName sessionName
     * @return 退出会议结果
     */
    public Meeting exitMeeting(String ivxMeetingSessionName);

    /**
     * 静音
     * @param from/to from/to participant
     * @return 无
     */
    public String muteMic(String from, String to);

    /**
     * 取消静音
     * @param from/to dest/src participantId
     * @return 无
     */
    public String unmuteMic(String from, String to);

    /**
     * 请求发言
     * @param ivxMeetingParticipantId ivxMeetingParticipantId
     * @return  无
     */
    public String requestSpeak(String ivxMeetingParticipantId);

    /**
     * 允许或/拒绝发言
     *
     * @param isApprove
     * @param userId userId
     * @param hostId
     * @return 无
     */
    public String approveSpeak(String isApprove, String userId, String hostId);

    /**
     * 申请主持
     * @param from/to dest/src participantId
     * @return 无
     */
    public String requestHost(String from ,String to);

    /**
     * 要求输入主持密码
     * @param /to dest/src participantId
     * @return 无
     */
    public String requestHostPwd(String from,String to);

    /**
     * 允许或拒绝主持
     *
     * @param ivxMeetingParticipantId ivxMeetingParticipantId
     * @param isApproved 申请或拒绝（true为批准，false为拒绝)
     * @return 无
     */
    public String approveHost(String ivxMeetingParticipantId, String isApproved);

    /**
     * 指定支持
     * @param ivxMeetingParticipantId ivxMeetingParticipantId
     * @return 无
     */
    public String assignHost(String ivxMeetingParticipantId);

    /**
     * 共享桌面
     * @param ivxMeetingParticipantId ivxMeetingParticipantId
     * @return 无
     */
    public Meeting shareDesktop(String ivxMeetingParticipantId);

    /**
     * 获取会议URL
     * @param ivxMeetingSessionName ivxMeetingSessionName
     * @return 会议URL
     */
    public Meeting getMeetingUrl(String ivxMeetingSessionName);

    /**
     * 获取会议信息
     * @param ivxMeetingSessionName ivxMeetingSessionName
     * @return 会议信息
     */
    public Meeting getMeetingInfo(String ivxMeetingSessionName);

    /**
     * 获取与会者列表
     * @param ivxMeetingSessionName ivxMeetingSessionName
     * @return 与会者列表
     */
    public Meeting getParticipantList(String ivxMeetingSessionName);

    /**
     * 上传ngrok穿透信息
     * @param ivxParticipantId ivxParticipantId
     * @param ivxNgrokUrl ivxNgrokUrl
     * @return 无
     */
    public Meeting updateNgrokINfo(String ivxParticipantId, String ivxNgrokUrl);

    /**
     * 请求VNC控制
     * @param ivxParticipantId ivxParticipantId
     * @return 无
     */
    public Meeting requestVNCControl(String ivxParticipantId);

    /**
     * 批转或拒绝VNC控制
     * @param ivxParticipantId ivxParticipantId
     * @return 无
     */
    public Meeting applyVNCControl(String ivxParticipantId);
    /**
     * 群发消息
     *
     * @param message
     * @param from /to from/to participant
     * @param userName
     * @return 无
     */
    public String sendMessage(String message, String from, String to, String userName);

    /**
     * 主持密码错误
     * @param userId
     * @return
     */
    String invalidHostPwd(String userId);

    /**
     * 关闭视频
     * @param userId
     * @param toId
     * @return
     */
    String closeVideo(String userId, String toId);

    /**
     * 打开视频
     * @param userId
     * @param toId
     * @return
     */
    String openVideo(String userId, String toId);

    /**
     * 放弃主持
     * @param hostId
     * @return
     */
    String giveUpHost(String hostId);

    /**
     * 判断成员是否在线
     */
    boolean checkOnLine(String participantId);
    /**
     * 判断主席是否在线
     * @author wjqiu
     * @date 2019-01-23
     */
    boolean checkHostOnLine(BizMeeting bizMeeting);
    boolean setHostEmpty(BizMeeting bizMeeting);
    /**
     * 检查入会权限
     */
    RespCode permissonVerify(BizMeeting bizMeeting, UserVo userVo, String pwd, String secret);
    /**
     * 检查license权限
     */
    RespCode licenseVerify();

    /**
     * 创建会议mid
     *
     * @author wjqiu
     * @date 2019-01-09
     */
    String createMeetingMid();
    /**
     * 初始化会议信息
     *
     * @author wjqiu
     * @date 2019-01-09
     */
    void initMeeting(BizMeeting bizMeeting);

    boolean checkNickname(String meetingMid, String nickName);
}
