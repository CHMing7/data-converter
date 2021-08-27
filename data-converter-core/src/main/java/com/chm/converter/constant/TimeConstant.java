package com.chm.converter.constant;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;

import java.sql.Timestamp;
import java.time.*;
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
            CollectionUtil.newLinkedHashSet(Instant.class, LocalDate.class, LocalDateTime.class, LocalTime.class,
                    OffsetDateTime.class, OffsetTime.class, ZonedDateTime.class, MonthDay.class, YearMonth.class,
                    Year.class, ZoneOffset.class);

    /**
     * 默认时间类型集合
     */
    Set<Class<? extends Date>> DEFAULT_DATE_SET = CollectionUtil.newLinkedHashSet(java.sql.Date.class, Timestamp.class, Date.class);

    /**
     * java8时间类型转换
     */
    Map<Class<? extends TemporalAccessor>, TemporalQuery<?>> CLASS_TEMPORAL_QUERY_MAP = MapUtil.of(
            Pair.of(Instant.class, (TemporalQuery<Instant>) Instant::from),
            Pair.of(LocalDate.class, (TemporalQuery<LocalDate>) LocalDate::from),
            Pair.of(LocalDateTime.class, (TemporalQuery<LocalDateTime>) LocalDateTime::from),
            Pair.of(LocalTime.class, (TemporalQuery<LocalTime>) LocalTime::from),
            Pair.of(OffsetDateTime.class, (TemporalQuery<OffsetDateTime>) OffsetDateTime::from),
            Pair.of(OffsetTime.class, (TemporalQuery<OffsetTime>) OffsetTime::from),
            Pair.of(ZonedDateTime.class, (TemporalQuery<ZonedDateTime>) ZonedDateTime::from),
            Pair.of(MonthDay.class, (TemporalQuery<MonthDay>) MonthDay::from),
            Pair.of(YearMonth.class, (TemporalQuery<YearMonth>) YearMonth::from),
            Pair.of(Year.class, (TemporalQuery<Year>) Year::from),
            Pair.of(ZoneOffset.class, (TemporalQuery<ZoneOffset>) ZoneOffset::from)
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
}
