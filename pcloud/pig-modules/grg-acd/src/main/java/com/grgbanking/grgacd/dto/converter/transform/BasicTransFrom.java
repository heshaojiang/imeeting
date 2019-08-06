package com.grgbanking.grgacd.dto.converter.transform;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author tjshan
 * @since 2019/5/27 19:09
 */
public class BasicTransFrom {

    public Long getDurationSecond(Date from,Date to){
        if (from==null||to==null){
            return 0L;
        }
        return getBetween(from,to).getSeconds();
    }

    public  Long getDurationMinute(Date from,Date to){
        if (from==null||to==null){
            return 0L;
        }
        return getBetween(from,to).toMinutes();
    }
    public Duration getBetween(Date from,Date to){
        Instant instantFrom = from.toInstant();
        Instant instantTO = to.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTimeFrom = LocalDateTime.ofInstant(instantFrom, zone);
        LocalDateTime localDateTimeTo = LocalDateTime.ofInstant(instantTO, zone);
        Duration between = Duration.between(localDateTimeFrom, localDateTimeTo);
        return between;
    }
}
