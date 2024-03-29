package com.chm.converter.core.constant;


import com.chm.converter.core.lang.Pair;
import com.chm.converter.core.utils.CollUtil;
import com.chm.converter.core.utils.MapUtil;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

/**
 * 时间类常量
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-08-13
 **/
public interface TimeConstant {

    /**
     * 支持的java8时间类型集合
     */
    Set<Class<? extends TemporalAccessor>> TEMPORAL_ACCESSOR_SET =
            CollUtil.newLinkedHashSet(Instant.class, LocalDate.class, LocalDateTime.class, LocalTime.class,
                    OffsetDateTime.class, OffsetTime.class, ZonedDateTime.class, MonthDay.class, YearMonth.class,
                    Year.class, ZoneOffset.class);
    /**
     * 默认时间类型集合
     */
    Set<Class<? extends Date>> DEFAULT_DATE_SET = CollUtil.newLinkedHashSet(java.sql.Date.class, Timestamp.class, Date.class);
    /**
     * java8时间类型转换
     */
    Map<Class<? extends TemporalAccessor>, TemporalQuery<?>> CLASS_TEMPORAL_QUERY_MAP = MapUtil.of(
            Pair.of(Instant.class, TemporalQuerys.INSTANT_QUERY),
            Pair.of(LocalDate.class, TemporalQuerys.LOCAL_DATE_QUERY),
            Pair.of(LocalDateTime.class, TemporalQuerys.LOCAL_DATE_TIME_QUERY),
            Pair.of(LocalTime.class, TemporalQuerys.LOCAL_TIME_QUERY),
            Pair.of(OffsetDateTime.class, TemporalQuerys.OFFSET_DATE_TIME_QUERY),
            Pair.of(OffsetTime.class, TemporalQuerys.OFFSET_TIME_QUERY),
            Pair.of(ZonedDateTime.class, TemporalQuerys.ZONED_DATE_TIME_QUERY),
            Pair.of(MonthDay.class, TemporalQuerys.MONTH_DAY_QUERY),
            Pair.of(YearMonth.class, TemporalQuerys.YEAR_MONTH_QUERY),
            Pair.of(Year.class, TemporalQuerys.YEAR_QUERY),
            Pair.of(ZoneOffset.class, TemporalQuerys.ZONE_OFFSET_QUERY)
    );
    /**
     * java8时间类型转换
     */
    Map<Class<? extends TemporalAccessor>, TemporalCreate<?>> CLASS_TEMPORAL_CREATE_MAP = MapUtil.of(
            Pair.of(Instant.class, (TemporalCreate<Instant>) Instant::now),
            Pair.of(LocalDate.class, (TemporalCreate<LocalDate>) LocalDate::now),
            Pair.of(LocalDateTime.class, (TemporalCreate<LocalDateTime>) LocalDateTime::now),
            Pair.of(LocalTime.class, (TemporalCreate<LocalTime>) LocalTime::now),
            Pair.of(OffsetDateTime.class, (TemporalCreate<OffsetDateTime>) OffsetDateTime::now),
            Pair.of(OffsetTime.class, (TemporalCreate<OffsetTime>) OffsetTime::now),
            Pair.of(ZonedDateTime.class, (TemporalCreate<ZonedDateTime>) ZonedDateTime::now),
            Pair.of(MonthDay.class, (TemporalCreate<MonthDay>) MonthDay::now),
            Pair.of(YearMonth.class, (TemporalCreate<YearMonth>) YearMonth::now),
            Pair.of(Year.class, (TemporalCreate<Year>) Year::now),
            Pair.of(ZoneOffset.class, (TemporalCreate<ZoneOffset>) () -> OffsetDateTime.now(ZoneId.systemDefault()).getOffset())
    );
    /**
     * java8时间类型字符串转换器
     */
    Map<Class<? extends TemporalAccessor>, DateTimeFormatter> JAVA8_TIME_DEFAULT_FORMATTER_MAP = MapUtil.of(
            Pair.of(Instant.class, DateTimeFormatter.ISO_INSTANT),
            Pair.of(LocalDate.class, DateTimeFormatter.ISO_LOCAL_DATE),
            Pair.of(LocalDateTime.class, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            Pair.of(LocalTime.class, DateTimeFormatter.ISO_LOCAL_TIME),
            Pair.of(OffsetDateTime.class, DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            Pair.of(OffsetTime.class, DateTimeFormatter.ISO_OFFSET_TIME),
            Pair.of(ZonedDateTime.class, DateTimeFormatter.ISO_ZONED_DATE_TIME),
            Pair.of(MonthDay.class, DateTimeFormatter.ofPattern("--MM-dd")),
            Pair.of(YearMonth.class, new DateTimeFormatterBuilder()
                    .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                    .appendLiteral('-')
                    .appendValue(MONTH_OF_YEAR, 2)
                    .toFormatter()),
            Pair.of(Year.class, new DateTimeFormatterBuilder()
                    .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                    .toFormatter()),
            Pair.of(ZoneOffset.class, DateTimeFormatter.ofPattern("ZZZZZ"))
    );

    /**
     * 判断指定类型是否为java8time
     *
     * @param clazz 指定类型
     * @return boolean
     */
    static boolean isJava8Time(Class<?> clazz) {
        return TEMPORAL_ACCESSOR_SET.contains(clazz) || TEMPORAL_ACCESSOR_SET.stream()
                .anyMatch(temporalAccessorClass -> temporalAccessorClass.isAssignableFrom(clazz));
    }

    @FunctionalInterface
    interface TemporalCreate<R extends TemporalAccessor> {

        /**
         * TemporalAccessor创建方法
         *
         * @return TemporalAccessor
         */
        R create();
    }
}
