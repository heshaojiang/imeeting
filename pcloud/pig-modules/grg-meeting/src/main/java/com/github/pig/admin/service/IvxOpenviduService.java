package com.github.pig.admin.service;


import com.github.pig.admin.common.meeting.MeetingSession;
import com.github.pig.common.util.exception.GrgException;

import java.util.List;

public interface IvxOpenviduService {
    List<String> GetAllKeysInCache(String publicUrl);

    MeetingSession getSessionIdToken(String openviduUrl, String sMeetingID);

    //使用MeetingId创建会议
    MeetingSession getMeetingSessionByMid(String sMeetingID, String secret) throws GrgException;
    //使用SessionId创建会议
    MeetingSession getMeetingSessionBySid(String sMeetingID, String secret,String sOpenviduUrl) throws GrgException;
    String GetSessionIdWithUrl(String url);
    String GetSessionIdWithUrl(String url,String split);
}