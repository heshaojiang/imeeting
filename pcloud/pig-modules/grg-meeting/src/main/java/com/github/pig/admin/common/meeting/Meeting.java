package com.github.pig.admin.common.meeting;


import com.github.pig.admin.common.util.MD5;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Meeting{
    private long id;//主键.

    private String name;//测试名称.

    //会议密码
    private String meetingPwd;
    //会议ID
    private String meetingId;
    //会议主持
    private String meetingHost="";
    //会议主持密码
    private String meetingHostPwd=MD5.getMD5("000000");
    //会议人员信息集合
    private Map<String, Participant> mapParticipants = new ConcurrentHashMap<>();
    //会议主题
    private String topic;
    //会议状态
    private String status;
    //会议加入URL
    private String joinURL;
    //
    private String createdAt;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJoinURL() {
        return joinURL;
    }

    public void setJoinURL(String joinURL) {
        this.joinURL = joinURL;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Meeting() {
    }
    public Meeting(String meetingId) {
        this.meetingId = meetingId;
    }

    public Map<String, Participant> getMapParticipants() {
        return mapParticipants;
    }

    public void putParticipant(Participant participant) {
        String userId = participant.getUserId();
        this.mapParticipants.put(userId, participant);
    }

    public void removeParticipant(String userId) {
        this.mapParticipants.remove(userId);
    }

    public String getMeetingPwd() {
        return meetingPwd;
    }

    public void setMeetingPwd(String meetingPwd) {
        this.meetingPwd =MD5.getMD5(meetingPwd) ;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public void setMapParticipants(Map<String, Participant> mapParticipants) {
        this.mapParticipants = mapParticipants;
    }

    public void setMeetingHost(String meetingHost) {
        this.meetingHost = meetingHost;
    }

    public String getMeetingHost() {
        return this.meetingHost;
    }

    public boolean isMeetingHostValid() {
        if (meetingHost.isEmpty())
            return false;
        else
            return true;
    }

    /**
     * 校验主持密码是否正确
     * @param pwd
     * @return
     */
    public boolean verifyMeetingHostPwd(String pwd) {
        if (meetingHostPwd.equals(pwd)){
            return true;
        }else {
            return false;
        }
    }

    public String getMeetingHostPwd() {
        return meetingHostPwd;
    }

    public void setMeetingHostPwd(String meetingHostPwd) {
        this.meetingHostPwd = meetingHostPwd;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", meetingPwd='" + meetingPwd + '\'' +
                ", meetingId='" + meetingId + '\'' +
                ", meetingHost='" + meetingHost + '\'' +
                ", meetingHostPwd='" + meetingHostPwd + '\'' +
                ", mapParticipants=" + mapParticipants +
                '}';
    }
}
