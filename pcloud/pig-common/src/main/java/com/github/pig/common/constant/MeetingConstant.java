package com.github.pig.common.constant;

/**
 * @auther: wjqiu
 * @date: 2019-01-04
 * @description:
 */
public interface MeetingConstant {

    /** @NOTE: Meeting Constant */
    /**
     * 完成
     */
    String MEETING_STATUS_END = "2";
    /**
     * 预约
     */
    String MEETING_STATUS_PLAN = "1";
    /**
     * 进行中
     */
    String MEETING_STATUS_NORMAL = "0";
    /**
     * 自由会议
     */
    String MEETING_TYPE_FREEDOM = "1";
    /**
     * 培训模式
     */
    String MEETING_TYPE_CLASSROOM = "2";
    /**
     * 主持模式
     */
    String MEETING_TYPE_COMPERE = "3";
    /**
     * 主持默认密码
     */
    String MEETING_COMPERE_PWD = "111111";
    /**
     * 入会默认密码
     */
    String MEETING_JOIN_PWD = "000000";

    /** @NOTE: Room Constant */
    /**
     * 会议室状态：空闲
     */
    String ROOM_STATUS_IDLE = "0";
    /**
     * 会议室状态：预约
     */
    String ROOM_STATUS_PLAN = "1";
    /**
     * 会议室状态：占用
     */
    String ROOM_STATUS_INUSE = "2";

    /** @NOTE: 用户管理 Constant */
    /**
     * 有效
     */
    String USER_STATUS_NORMAL = "有效";
    /**
     * 无效
     */

    /** @NOTE: 参会人员 Constant */
    /**
     * 有心跳
     */
    String STATUS_HEART = "0";
    /**
     * 无心跳
     */
    String STATUS_LESSHEART = "1";
    /**
     * 参会方
     */
    String JOIN_TYPE_Join = "2";
    /**
     * 通话方
     */
    String JOIN_TYPE_Video = "1";

    /** @NOTE: 用户 Constant */
    /**
     * 用户权限
     */
    String USER_ROLE_Admin = "role_super";
    String USER_ROLE_Agent = "imeeting_agent";
    Integer USER_ROLE_Admin_ID = 1;
    Integer USER_ROLE_User_ID = 5;
    /**
     * 用户默认密码
     */
    String USER_DEF_PWD = "111111";

    /** @NOTE: License Constant */
    String LICENSE_FILE = "license.lic";
}
