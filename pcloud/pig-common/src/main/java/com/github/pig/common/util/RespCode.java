package com.github.pig.common.util;

public enum RespCode {
    //公共类，各个接口都有可能触发的错误码
    SUCCESS(0, "sucess"),
    FAIL(1, "fail"),
    ERROR(2, "unknown error"),
    IME_DB_FAIL(3, "database error"),
    IME_INVALIDPARAMETER(4, "parameter error"),
    IME_SYS_EXCEPTION(5, "system exception!"),
    //综合类，多个前端共用的错误码
    IME_LOGIN_FAIL(1001, "username or password error!"),
    IME_USER_LOCKED(1002, "this user has locked,pls contact system manger!"),
    IME_UNAUTHORIZED(1003, "Unauthoried!"),
    IME_NEED_LOGIN(1004, "Please login first!"),
    IME_LIC_LICENSE_EXPIRED(1005, "Server license has expired!"),
    IME_LIC_LICENSE_NOT_FOUND(1006, "Not found server license!"),
    IME_INVALID_TOKEN(1007, "Invalid token!"),
    IME_NO_ROLE(1008, "No role!"),
    //业务类 iMeeting前端使用相关
    IME_INVALIDMEETINGID(2001, "invalid meeting id！"),
    IME_INVALIDPWD(2002, "join meeting password invalid!"),
    IME_INVALIDHOSTPWD(2003, "host password invalid!"),
    IME_NO_RIGHT_JOIN(2004, "no right to join this meeting!"),
    IME_MAX_VIDEO_NUMS(2005, "reached the maximum video number of participants!"),
    IME_MAX_VIDEO_CHANGE_TO_JOIN(2006, "reached the maximum video number change to join-type!"),
    IME_MAX_JOIN_NUMS(2007, "reached the maximum number of participants!"),
    IME_MEETING_NOT_STARTED(2008, "meeting has not started!"),
    IME_NO_MEETING_NOW(2009, "no meeting now!"),
    IME_REQUESTPWD(2010, "meeting need password,pls input password"),
    IME_REQUEST_HOST_PWD(2011, "need host password,pls input host password"),
    IME_WAITHOSTRELEASE(2012, "has host,wait for host release！"),
    OV_GETTOKEN_FAIL(2013, "get video Token fail！"),
    OV_GETSERVER_FAIL(2014, "no available video server！"),
    IME_NO_ANONYMOUS(2015, "not allow anonymous!"),
    IME_NO_AVAILABLE_ROOM(2016, "no available room!"),
    IME_NICKNAME_EMPTY(2017, "nickname cannot empty"),
    IME_NICKNAME_EXIST(2018, "nickname is exist!"),
    IME_INVALID_PARTICIPANTID(2019, "invalid Participant id!"),
    IME_INVALID_CUSTOMERID(2020, "invalid customer id！"),
    IME_LIC_MAX_JOIN_NUMS(2021, "reached the maximum view participants number of license limit!"),
    IME_LIC_MAX_VIDEO_NUMS(2022, "reached the maximum video participants number of license limit!"),
    IME_LIC_MAX_MEETING_NUMS(2023, "reached the maximum meeting number of license limit!"),
    IME_WAITHOSTAPPROVED(2024, "has host,wait for host approved!"),

    //console相关错误码
    CNSL_NAME_EMPTY(3003, "name empty!"),
    CNSL_NAME_EXIST(3004, "name exist!"),
    CNSL_PHONE_NUM_EMPTY(3005, "phone number empty!"),
    CNSL_PHONE_NUM_EXIST(3006, "phone number exist!"),
    CNSL_IN_USE(3007, "record is in use cannot do this!"),
    CNSL_FILE_FORMAT_ERROR(3008, "incorrect file format!"),
    CNSL_MAX_VIDEO_NUMS(3009, "reached the maximum number of video-type participants!!"),
    CNSL_MAX_JOIN_NUMS(3010, "reached the maximum number of join-type participants!"),
    CNSL_DEFAULT_JOIN_PASSWORD(3011, "join password is empty,use default password!"),
    CNSL_DEFAULT_HOST_PASSWORD(3011, "host password is empty,use default password!"),
    CNSL_SPLIT_NUM_LESS_VIDEO_NUM(3013, "video-type participants must less than split screen num minus one!"),
    CNSL_ADD_ROOM_FAIL(3014, "add room fail!"),
    CNSL_OBJ_NOT_FOUND(3015, "object not found!!"),
    CNSL_ROOM_NOT_FOUND(3016, "room not found!!"),
    CNSL_MEETING_ID_EMPTY(3017, "meeting id empty!"),
    CNSL_MEETING_ID_EXIST(3018, "meeting id exist!"),
    CNSL_NICKNAME_EMPTY(3019, "nickname is empty!"),
    CNSL_NICKNAME_EXIST(3020, "nickname is exist!"),
    CNSL_ROOM_NO_EXIST(3021, "room no exist!"),
    CNSL_CUSTOMER_NAME_EXIST(3022, "customer name exist!"),
    CNSL_MEETING_HAS_PARTICIPANT(3023, "meeting has participants!cannot do this!"),
    CNSL_LICENSE_WRONG(3033, "Not a server license file!"),
    CNSL_LICENSE_ERROR(3034, "License Error!"),
    CNSL_LICENSE_GET_MACHINE_CODE_ERR(3035, "Get machine code error!"),
    CNSL_OLD_PASSWORD_WRONG(3036, "old password is not right!"),
    CNSL_NAME_FORMAT_ERROR(3037, "username must be 4-64 letters and numbers!"),
    CNSL_NICKNAME_FORMAT_ERROR(3038, "nickname must be 1-64 characters"),
    CNSL_PASSWORD_EMPTY(3039, "password can not empty!"),
    CNSL_PASSWORD_FORMAT_ERROR(3040, "password must be 6-32 characters!"),
    CNSL_PHONE_FORMAT_ERROR(3041, "phone number format error!"),
    CNSL_UPLOAD_FILE_FORMAT_ERROR(3042, "Upload file format error!"),
    CNSL_UPLOAD_FILE_PARSE_ERROR(3043, "Upload file parse error!"),
    CNSL_UPLOAD_FILE_INSERT_ERROR(3044, "Upload file insert error!"),
    ACD_IN_PENDING_QUEUE(4001, "The caller is in Pending Queue!"),
    ACD_IN_QUEUE(4002, "The caller already in Queue!"),
    ACD_CLIENT_NAME_EMPTY(4002, "The client name is empty!"),
    ACD_CALLER_NOT_LOGIN(4003, "The caller is not login!"),
    ACD_CALL_NOT_EXIST(4004, "The call is not exist!"),
    ACD_QUEUE_NOT_EXIST(4005, "The queue is not exist!"),
    ACD_ACCEPT_FAIL(4006, "Accept call failed!"),
    ACD_AGENT_NOT_LOGIN(4007, "Agent not login!"),
    ACD_AGENT_NOT_EXIST(4008, "Agent not exist!"),
    ACD_AGENT_VERIFY_FAIL(4009, "Agent verify fail,this clientId not login for cur agent!"),
    ACD_CALL_VERIFY_FAIL(4010, "Call verify fail,this clientId not for this call!"),
    ACD_AGENT_NOT_INQUEUE(4011, "Agent not in Queue!"),
    ACD_AGENT_ROLE_ERROR(4012, "User not a role of agent!"),
    //文件上传状态
    FILE_UPLOADING(5001,"Chunk Upload OK!"),
    FILE_MD5_FAIL(5002,"File Merge Succ! MD5 Check FAIL!"),
    FILE_SERVER_ERR(5003, "Server Error Please Retry..."),
    FILE_MERGE_FILE_ERR(5004, "File Merge Fail!"),
    FILE_WRITE_FILE_ERR(5005, "File Write Fail!"),
    FILE_PARAM_ERR(5006, "Param Error!"),
    FILE_GET_FILE_ERR(5007, "File Upload Is Empty!"),




    MAX_ERR_CODE(9999, "unknown error code");

    private int code;
    private String msg;

    RespCode(int code, String msg) {
        this.msg = msg;
        this.code = code;
    }

    public int getCode() {
        return code;
    }
    public String getMsg() {
//        try {
//            return URLDecoder.decode(msg, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        return msg;
    }
}
