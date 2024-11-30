package org.example.ecommercefashion.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {

    private final static TimeZone VN_ZONE = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
    private final static TimeZone GLOBAL_ZONE = TimeZone.getTimeZone("UTC");

    private static Timestamp dateToTimeStamp(Date date) {
        if (date == null) {
            return null;
        }
        return new Timestamp(date.getTime());
    }

    public static SimpleDateFormat getSdf(TimeZone timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdf.setTimeZone(timeZone);
        return sdf;
    }

    private static Timestamp toZoneTime(Date date, TimeZone zone) {
        if(date == null) {
            return null;
        }
        SimpleDateFormat sdf = getSdf(zone);
        Date time = new Date(sdf.format(date));
        return Timestamp.from(time.toInstant());
    }

    public static Timestamp toVnTime(Date date) {
        return toZoneTime(date, VN_ZONE);
    }

    public static Timestamp toGlobalTime(Date date) {
        return toZoneTime(date, GLOBAL_ZONE);
    }

    public static Timestamp getCurrent() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static Timestamp getCurrentVnTime() {
        return toZoneTime(getCurrent(), VN_ZONE);
    }

    public static Timestamp getCurrentGlobalTime() {
        return toZoneTime(getCurrent(), GLOBAL_ZONE);
    }

}


