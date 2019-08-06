package com.github.pig.admin.common.util;

import com.github.pig.common.constant.MeetingConstant;
import org.apache.commons.lang.StringUtils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MeetingUtils {
    /**
     * 获取现在时间
     *
     * @return返回长时间格式 yyyy-MM-dd HH:mm:ss
     */
    public static Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(8);
        Date currentTime_2 = formatter.parse(dateString, pos);
        return currentTime_2;
    }

    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * @param
     * @author fmsheng
     * @description 获取当前时间
     * @date 2018/8/3 9:52
     */
    public static String getStringNowDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * @param
     * @author fmsheng
     * @description 获取前一天
     * @date 2018/8/3 9:52
     */
    public static String getStringDateBefore() {
        return LocalDateTime.now().plusDays(-1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * @param
     * @author fmsheng
     * @description 当前时间减去三十秒
     * @date 2018/8/3 9:52
     */
    public static Date getStringDateBeforeThirtySeconds() {
        LocalDateTime localDateTime = LocalDateTime.now().plusSeconds(-30);
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }

    /**
     * @param
     * @author fmsheng
     * @description 当前时间减去十五秒
     * @date 2019/1/3 15:01
     */
    public static Date getStringDateAfterTenSeconds() {
        LocalDateTime localDateTime = LocalDateTime.now().plusSeconds(-15);
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }

    /**
     * @param
     * @author fmsheng
     * @description 当前时间减去五秒
     * @date 2019/1/3 15:01
     */
    public static Date getStringDateAfterFiveSeconds() {
        LocalDateTime localDateTime = LocalDateTime.now().plusSeconds(-5);
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }

    /**
     * @author: wjqiu
     * @date: 2019-01-17
     * @description: 通过分屏类型计算视频数量
     */
    public static int getSplitTypeScreenNum(String splitType) {
        int screenNum = 0;
        if (StringUtils.isEmpty(splitType)){
            return screenNum;
        }
        switch (splitType) {
            case "one":
                screenNum = 1;
                break;
            case "two":
                screenNum = 2;
                break;
            case "twoWithHover":
                screenNum = 2;
                break;
            case "three":
                screenNum = 3;
                break;
            case "threeWithHover":
                screenNum = 3;
                break;
            case "four":
                screenNum = 4;
                break;
            case "six":
                screenNum = 6;
                break;
            default:
                screenNum = 0;
                break;
        }
        return screenNum;
    }

    public static int getVideoNumFromMeetingType(String meetingType) {
        int videoNum = 0;
        if (StringUtils.isEmpty(meetingType)){
            return videoNum;
        }
        switch (meetingType) {
            case MeetingConstant.MEETING_TYPE_FREEDOM:
                videoNum = 6;
                break;
            case MeetingConstant.MEETING_TYPE_CLASSROOM:
                videoNum = 3;
                break;
            case MeetingConstant.MEETING_TYPE_COMPERE:
                videoNum = 6;
                break;
            default:
                videoNum = 6;
                break;
        }
        return videoNum;
    }

}
