package com.grgbanking.grgacd.common.util;

import java.time.*;
import java.util.Date;

/**
 * @author tjshan
 * @since 2019/6/11 13:25
 */
public class DateUtils {

    public static LocalDateTime dateToLocalDateTime(Date date){
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime;
    }

    public static LocalDate dateToLocalDate(Date date){
        LocalDateTime localDateTime = DateUtils.dateToLocalDateTime(date);
        LocalDate localDate = localDateTime.toLocalDate();
        return localDate;
    }

    public static LocalTime dateToLocalTime(Date date){
        LocalDateTime localDateTime = DateUtils.dateToLocalDateTime(date);
        LocalTime localTime = localDateTime.toLocalTime();
        return localTime;
    }

    public static Date localDateTimeToDate(LocalDateTime localDateTime){
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }

    public static Date localDateToDate(LocalDate localDate){
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }

    public static Date localTimeToDate(LocalDate localDate,LocalTime localTime){
        if (localDate==null){
            localDate = LocalDate.now();
        }
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }


}
