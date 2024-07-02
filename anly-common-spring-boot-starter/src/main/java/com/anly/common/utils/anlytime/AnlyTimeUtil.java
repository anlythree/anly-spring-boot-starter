package com.anly.common.utils.anlytime;

/**
 * @DATE: 2023/7/27
 * @USER: anlythree
 */


import com.anly.common.constant.AnlyScaConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * LocalDate格式或LocalDateTime格式和String类型的转换
 *
 * @author anlythree
 * @date 2020/5/16 9:07
 */
public class AnlyTimeUtil {

    /**
     * 通用日期格式
     */
    public final static DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern(AnlyScaConstant.DATE_FORMAT);

    /**
     * 通用时间格式
     */
    public final static DateTimeFormatter FMT_DATE_TIME = DateTimeFormatter.ofPattern(AnlyScaConstant.DATETIME_FORMAT);

    /**
     * 默认时区格式
     */
    public final static ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Shanghai");

    /**
     * 时间(日期)格式转成String类型
     *
     * @param localDate
     * @return
     */
    public static String dateToString(LocalDate localDate) {
        if (localDate == null) {
            return "";
        }
        return localDate.toString();

    }

    /**
     * 时间(时间)格式转成String类型
     *
     * @param localDateTime
     * @return
     */
    public static String timeToString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "";
        }
        return localDateTime.format(FMT_DATE_TIME);
    }

    /**
     * String格式转成LocalDate类型
     *
     * @param localDate
     * @return
     */
    public static LocalDate stringToDate(String localDate) {
        if (StringUtils.isEmpty(localDate)) {
            return null;
        }
        return LocalDate.parse(localDate, FMT_DATE);
    }

    /**
     * String格式转成LocalDateTime类型
     *
     * @param localDateTime
     * @return
     */
    public static LocalDateTime stringToTime(String localDateTime) {
        if (StringUtils.isEmpty(localDateTime)) {
            return null;
        }
        return LocalDateTime.parse(localDateTime, FMT_DATE_TIME);
    }

    /**
     * 自定义String格式转成LocalDateTime类型
     *
     * @param localDateTime
     * @return
     */
    public static LocalDateTime stringToTime(String localDateTime, String formate) {
        if (StringUtils.isEmpty(localDateTime) || StringUtils.isEmpty(formate)) {
            return null;
        }
        return LocalDateTime.parse(localDateTime, DateTimeFormatter.ofPattern(formate));
    }

    /**
     * 时分格式转成LocalDateTime类型
     *
     * @param localDateTime
     * @return
     */
    public static LocalDateTime onlyTimeStrToTime(String localDateTime) {
        if (StringUtils.isEmpty(localDateTime)) {
            return null;
        }
        return LocalDateTime.parse(AnlyTimeUtil.dateToString(LocalDate.now()) + " " + localDateTime + ":00", FMT_DATE_TIME);
    }

    /**
     * LocalDate格式转成LocalDateTime格式，时间区域取值 00：00：00
     *
     * @param localDate
     * @return
     */
    public static LocalDateTime dateToTime(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return stringToTime(dateToString(localDate) + " 00:00:00");
    }

    /**
     * LocalDateTime格式转成LocalDate格式，时间格式直接去掉
     *
     * @param localDateTime
     * @return
     */
    public static LocalDate timeToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.toLocalDate();
    }

    /**
     * 根据时间格式分离出日期和时间，（时间只到分钟，不到秒）
     *
     * @param dateTime
     * @return
     */
    public static String[] getDateAndTimeByTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        String[] s = timeToString(dateTime).split(" ");
        s[1] = s[1].substring(0, s[1].lastIndexOf(":"));
        return s;
    }

    /**
     * 返回当前时间到传入时间之间的时间间隔
     * 返回值的isNegative() 返回当前时间是否早于开始时间
     *
     * @param startTime
     * @return
     */
    public static Duration timeInterval(LocalDateTime startTime) {
        if (startTime == null) {
            return null;
        }
        return timeInterval(startTime, LocalDateTime.now());
    }

    /**
     * 返回当前时间到传入时间之间的时间间隔
     * 返回值的isNegative() 返回!!!结束时间是否早于!!!开始时间(不是开始时间早于结束时间)
     *
     * @param startTime
     * @return
     */
    public static Duration timeInterval(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return null;
        }
        return Duration.between(startTime, endTime);
    }

    /**
     * t1是否早于t2
     * 这里的t1和t2都可能为null，为null按照正无穷来算，都为null返回true
     * 时间相同也返回true
     *
     * @param t1
     * @param t2
     * @return
     */
    public boolean earlyThan(LocalDateTime t1, LocalDateTime t2) {
        if (t1 == null || t2 == null) {
            return false;
        }
        // t1和t2都不为null,比较这两个时间的先后
        Duration duration = AnlyTimeUtil.timeInterval(t1, t2);
        return !duration.isNegative();
    }

    /**
     * LocalDateTime转时间戳
     *
     * @param localDateTime
     * @return
     */
    public static Long timeToStamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 时间戳转LocalDateTime
     *
     * @param timeStamp
     * @return
     */
    public static LocalDateTime stampToTime(long timeStamp) {
        return LocalDateTime.ofEpochSecond(timeStamp / 1000, 0, ZoneOffset.ofHours(8));
    }

    /**
     * 获取带时区的时间
     *
     * @param localDateTime
     * @return
     */
    public static ZonedDateTime getZonedTime(LocalDateTime localDateTime, ZoneId zoneId) {
        return (localDateTime == null ? LocalDateTime.now() : localDateTime).atZone(zoneId == null ? DEFAULT_ZONE_ID : zoneId);
    }

    /**
     * 带时区的时间格式延后seconds秒后的时间
     *
     * @param zonedDateTime
     * @param seconds
     * @return
     */
    public static ZonedDateTime plusSecondsByZoneTime(ZonedDateTime zonedDateTime, Integer seconds) {
        return (zonedDateTime == null ? ZonedDateTime.now(DEFAULT_ZONE_ID) : zonedDateTime).plusSeconds(seconds);
    }

    /**
     * 时间格式延后seconds秒后的时间
     *
     * @param localDateTime
     * @param seconds
     * @return
     */
    public static LocalDateTime plusSecondsByTime(LocalDateTime localDateTime, Integer seconds) {
        return (localDateTime == null ? LocalDateTime.now() : localDateTime).plusSeconds(seconds);
    }

}