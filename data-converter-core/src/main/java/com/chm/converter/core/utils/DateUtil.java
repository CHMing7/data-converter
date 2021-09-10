package com.chm.converter.core.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Objects;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-08-26
 **/
public class DateUtil {

    //===========================异常定义============================
    /**
     * 解析日期时异常
     */
    public static final String PARSE_LOCAL_DATE_EXCEPTION = "Unable to obtain";

    /**
     * 根据 formatter格式化 date
     *
     * @param date      Date
     * @param formatter DateTimeFormatter
     * @return String
     */
    public static String format(Date date, DateTimeFormatter formatter) {
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(formatter, "formatter");
        if (formatter.getZone() != null) {
            return formatter.format(Instant.ofEpochMilli(date.getTime()));
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).format(formatter);
    }

    /**
     * 根据 formatter格式化 temporal
     *
     * @param temporal  TemporalAccessor
     * @param formatter DateTimeFormatter
     * @return String
     */
    public static String format(TemporalAccessor temporal, DateTimeFormatter formatter) {
        Objects.requireNonNull(temporal, "temporal");
        Objects.requireNonNull(formatter, "formatter");
        if (temporal instanceof Instant && formatter.getZone() == null) {
            return ((Instant) temporal).atZone(ZoneId.systemDefault()).format(formatter);
        }
        return formatter.format(temporal);
    }

    /**
     * 根据 formatter解析为 Date
     *
     * @param text      待解析字符串
     * @param formatter DateTimeFormatter
     * @return Date
     */
    public static Date parseToDate(String text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        Date date;
        try {
            date = toDate(toLocalDateTime(formatter.parse(text)));
        } catch (DateTimeException e) {
            if (e.getMessage().startsWith(PARSE_LOCAL_DATE_EXCEPTION)) {
                date = toDate(LocalDate.parse(text, formatter));
            } else {
                throw e;
            }
        }
        return date;
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
     * LocalDate转Date
     *
     * @param localDate LocalDate
     * @return Date
     */
    public static Date toDate(LocalDate localDate) {
        Objects.requireNonNull(localDate, "localDate");
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime转Date
     *
     * @param localDateTime LocalDateTime
     * @return Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
