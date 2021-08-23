package com.chm.converter.utils.formatter;

import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Objects;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-15
 **/
public final class DateTimeConverterUtil {

    /**
     * Date转LocalDateTime
     *
     * @param date Date
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        Objects.requireNonNull(date, "date");
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Timestamp转LocalDateTime
     *
     * @param timestamp Timestamp
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        Objects.requireNonNull(timestamp, "timestamp");
        return timestamp.toLocalDateTime();
    }

    /**
     * LocalDate转LocalDateTime
     *
     * @param localDate LocalDate
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(LocalDate localDate) {
        Objects.requireNonNull(localDate, "localDate");
        return localDate.atStartOfDay();
    }

    /**
     * LocalTime转LocalDateTime
     * 以当天的日期+LocalTime组成新的LocalDateTime
     *
     * @param localTime LocalTime
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(LocalTime localTime) {
        Objects.requireNonNull(localTime, "localTime");
        return LocalDate.now().atTime(localTime);
    }

    /**
     * Instant转LocalDateTime
     *
     * @param instant Instant
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * 时间戳epochMilli毫秒转LocalDateTime
     *
     * @param epochMilli 时间戳
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(long epochMilli) {
        Objects.requireNonNull(epochMilli, "epochMilli");
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault());
    }

    /**
     * temporal转LocalDateTime
     *
     * @param temporal TemporalAccessor
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(TemporalAccessor temporal) {
        return LocalDateTime.from(temporal);
    }

    /**
     * ZonedDateTime转LocalDateTime
     * 注意时间对应的时区和默认时区差异
     *
     * @param zonedDateTime ZonedDateTime
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(ZonedDateTime zonedDateTime) {
        Objects.requireNonNull(zonedDateTime, "zonedDateTime");
        return zonedDateTime.toLocalDateTime();
    }

}
