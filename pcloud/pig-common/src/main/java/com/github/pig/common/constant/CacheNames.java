package com.github.pig.common.constant;

public interface CacheNames {
    /**
     * 保存所有Openvidu Server到同一个Session。使用SessionId作为key
     */
    String OPENVIDU_SESSIONS_ALL = "openviduSessions_All";

    /**
     * 用于通过SessionId找回MeetingId
     */
    String OPENVIDU_SESSIONS_MEETING = "openviduSessions_Meeting";


    /**
     * 保存所有Openvidu Server到同一个Session。使用SessionId作为key
     */
    String OPENVIDU_SESSIONS_ONE_SERVER = "openviduSessions_OneServer";

    /**
     * 保存所有MeetingId与SessionId的关系，使用MeetingId作为key
     */
    String MEETING_SESSIONS = "meetingSessions";

    /**
     * 用于通过MeetingId找回Openvidu Websocket Url
     */
    String MEETING_GET_WS_URL = "meetingWebsocketUrls";

    /**
     * 用于通过MeetingId找回主席ID
     */
    String MEETING_GET_COMPERE = "meetingCompere";

    /**
     * 参会人
     */
    String MEETING_PATTICIPANT = "meetingParticipant";
}
