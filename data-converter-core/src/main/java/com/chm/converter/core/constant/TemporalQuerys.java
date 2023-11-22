package com.chm.converter.core.constant;

import com.chm.converter.core.utils.DateUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalQuery;

/**
 * @author CHMing
 * @date 2023-11-19
 **/
public interface TemporalQuerys {

    TemporalQuery<Instant> INSTANT_QUERY = DateUtil::toInstant;

    TemporalQuery<LocalDate> LOCAL_DATE_QUERY = DateUtil::toLocalDate;

    TemporalQuery<LocalDateTime> LOCAL_DATE_TIME_QUERY = DateUtil::toLocalDateTime;

    TemporalQuery<LocalTime> LOCAL_TIME_QUERY = DateUtil::toLocalTime;

    TemporalQuery<OffsetDateTime> OFFSET_DATE_TIME_QUERY = DateUtil::toOffsetDateTime;

    TemporalQuery<OffsetTime> OFFSET_TIME_QUERY = DateUtil::toOffsetTime;

    TemporalQuery<ZonedDateTime> ZONED_DATE_TIME_QUERY = DateUtil::toZonedDateTime;

    TemporalQuery<MonthDay> MONTH_DAY_QUERY = DateUtil::toMonthDay;

    TemporalQuery<YearMonth> YEAR_MONTH_QUERY = DateUtil::toYearMonth;

    TemporalQuery<Year> YEAR_QUERY = DateUtil::toYear;

    TemporalQuery<ZoneOffset> ZONE_OFFSET_QUERY = DateUtil::toZoneOffset;
}
