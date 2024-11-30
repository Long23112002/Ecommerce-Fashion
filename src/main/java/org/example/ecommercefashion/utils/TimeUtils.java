package org.example.ecommercefashion.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {

    private final static TimeZone VN_ZONE = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
    private final static TimeZone GLOBAL_ZONE = TimeZone.getTimeZone("UTC");

    public static SimpleDateFormat getSdf(TimeZone timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdf.setTimeZone(timeZone);
        return sdf;
    }

    private static Timestamp dateToTimeStamp(Date date) {
        if (date == null) {
            return null;
        }
        return new Timestamp(date.getTime());
    }

    private static Date timeStampToDate(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return new Date(timestamp.getTime());
    }

    public static Date toVnDate(Date date) {
        SimpleDateFormat sdf = getSdf(VN_ZONE);
        return new Date(sdf.format(date));
    }

    public static Timestamp toVnTimestamp(Timestamp timestamp) {
        Date date = toVnDate(timestamp);
        return Timestamp.from(date.toInstant());
    }

    public static Date toGlobalDate(Date date) {
        SimpleDateFormat sdf = getSdf(GLOBAL_ZONE);
        return new Date(sdf.format(date));
    }

    public static Timestamp toGlobalTimestamp(Timestamp timestamp) {
        Date date = toGlobalDate(timestamp);
        return Timestamp.from(date.toInstant());
    }

}
