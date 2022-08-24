package com.chm.converter.test.all;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSON;
import com.chm.converter.core.Converter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-15
 **/
public class TestJava8Time {

    Converter converter;

    Java8Time java8Time;

    @Before
    public void before() {
        java8Time = new Java8Time();
        java8Time.setInstant(Instant.now());
        java8Time.setLocalDate(LocalDate.now());
        java8Time.setLocalDateTime(LocalDateTime.now());
        java8Time.setLocalTime(LocalTime.now());
        java8Time.setOffsetDateTime(OffsetDateTime.now());
        java8Time.setOffsetTime(OffsetTime.now());
        java8Time.setZonedDateTime(ZonedDateTime.now());
        java8Time.setMonthDay(MonthDay.now());
        java8Time.setYearMonth(YearMonth.now());
        java8Time.setYear(Year.now());
        java8Time.setZoneOffset(ZoneOffset.MIN);
        java8Time.setDuration(Duration.ofHours(3));
        java8Time.setPeriod(Period.ofDays(3));
        java8Time.setDate(new Date());
        java8Time.setSqlDate(new java.sql.Date(new Date().getTime()));
        java8Time.setTimestamp(new Timestamp(new Date().getTime()));
    }


    public void testJava8Time() {
        Object encode = converter.encode(java8Time);
        StaticLog.info(StrUtil.str(encode, "utf-8"));
        StaticLog.info(StrUtil.str(converter.encode(LocalDateTime.now()), "utf-8"));
        StaticLog.info(StrUtil.str(converter.encode(MonthDay.now()), "utf-8"));
        StaticLog.info(StrUtil.str(converter.encode(null), "utf-8"));
        Java8Time java8Time = (Java8Time) converter.convertToJavaObject(encode, Java8Time.class);
        assertEquals(java8Time, this.java8Time);
        StaticLog.info(StrUtil.str(JSON.toJSONString(Duration.ZERO), "utf-8"));
    }

    @Test
    public void testAll() {
        TestJava8Time converterTest = new TestJava8Time();
        converterTest.before();
        List<DataType> dateTypeList = ConverterSelector.getDateTypeList();
        for (DataType dataType : dateTypeList) {
            List<Converter> converterList = ConverterSelector.getConverterListByDateType(dataType);
            for (Converter converter : converterList) {
                this.converter = converter;
                StaticLog.info(this.converter.getConverterName());
                this.testJava8Time();
            }
        }
    }

    public static class Java8Time implements Serializable {

        //@FieldProperty(name = "instant1", ordinal = 1)
        private Instant instant;

        //@FieldProperty(format = "yyyy-MM-dd")
        private LocalDate localDate;

        //@FieldProperty(format = "yyyy-MM-dd HH:mm:ss.SSS")
        private LocalDateTime localDateTime;

        //@FieldProperty(format = "HH:mm:ss.SSS")
        private LocalTime localTime;

        //@FieldProperty(format = "yyyy-MM-dd HH:mm:ss.SSSZZZZZ")
        private OffsetDateTime offsetDateTime;

        //@FieldProperty(format = "HH:mm:ss.SSSZZZZZ")
        private OffsetTime offsetTime;

        //@FieldProperty(format = "yyyy-MM-dd HH:mm:ss.SSSZZZZZ'['VV']'")
        private ZonedDateTime zonedDateTime;

        //@FieldProperty(format = "MM-dd")
        private MonthDay monthDay;

        //@FieldProperty(format = "yyyy-MM")
        private YearMonth yearMonth;

        //@FieldProperty(format = "yyyy")
        private Year year;

        //@FieldProperty(format = "ZZZZZ")
        private ZoneOffset zoneOffset;

        private Duration duration;

        private Period period;

        // @FieldProperty(format = "yyyy-MM-dd HH:mm:ss.SSS")
        private Date date;

        //@FieldProperty(format = "yyyy-MM-dd HH:mm:ss")
        private java.sql.Date sqlDate;

        //@FieldProperty(format = "yyyy-MM-dd HH:mm:ss")
        private Timestamp timestamp;

        public Instant getInstant() {
            return instant;
        }

        public void setInstant(Instant instant) {
            this.instant = instant;
        }

        public LocalDate getLocalDate() {
            return localDate;
        }

        public void setLocalDate(LocalDate localDate) {

            this.localDate = localDate;
        }

        public LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        public void setLocalDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
        }

        public LocalTime getLocalTime() {
            return localTime;
        }

        public void setLocalTime(LocalTime localTime) {
            this.localTime = localTime;
        }

        public OffsetDateTime getOffsetDateTime() {
            return offsetDateTime;
        }

        public void setOffsetDateTime(OffsetDateTime offsetDateTime) {
            this.offsetDateTime = offsetDateTime;
        }

        public OffsetTime getOffsetTime() {
            return offsetTime;
        }

        public void setOffsetTime(OffsetTime offsetTime) {
            this.offsetTime = offsetTime;
        }

        public ZonedDateTime getZonedDateTime() {
            return zonedDateTime;
        }

        public void setZonedDateTime(ZonedDateTime zonedDateTime) {
            this.zonedDateTime = zonedDateTime;
        }

        public MonthDay getMonthDay() {
            return monthDay;
        }

        public void setMonthDay(MonthDay monthDay) {
            this.monthDay = monthDay;
        }

        public YearMonth getYearMonth() {
            return yearMonth;
        }

        public void setYearMonth(YearMonth yearMonth) {
            this.yearMonth = yearMonth;
        }

        public Year getYear() {
            return year;
        }

        public void setYear(Year year) {
            this.year = year;
        }

        public ZoneOffset getZoneOffset() {
            return zoneOffset;
        }

        public void setZoneOffset(ZoneOffset zoneOffset) {
            this.zoneOffset = zoneOffset;
        }

        public Duration getDuration() {
            return duration;
        }

        public void setDuration(Duration duration) {
            this.duration = duration;
        }

        public Period getPeriod() {
            return period;
        }

        public void setPeriod(Period period) {
            this.period = period;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public java.sql.Date getSqlDate() {
            return sqlDate;
        }

        public void setSqlDate(java.sql.Date sqlDate) {
            this.sqlDate = sqlDate;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Java8Time java8Time = (Java8Time) o;
            return Objects.equals(instant, java8Time.instant) &&
                    Objects.equals(localDate, java8Time.localDate) &&
                    Objects.equals(localDateTime, java8Time.localDateTime) &&
                    Objects.equals(localTime, java8Time.localTime) &&
                    Objects.equals(offsetDateTime, java8Time.offsetDateTime) &&
                    Objects.equals(offsetTime, java8Time.offsetTime) &&
                    Objects.equals(zonedDateTime, java8Time.zonedDateTime) &&
                    Objects.equals(monthDay, java8Time.monthDay) &&
                    Objects.equals(yearMonth, java8Time.yearMonth) &&
                    Objects.equals(year, java8Time.year) &&
                    Objects.equals(zoneOffset, java8Time.zoneOffset) &&
                    Objects.equals(duration, java8Time.duration) &&
                    Objects.equals(period, java8Time.period) &&
                    Objects.equals(date, java8Time.date) &&
                    Objects.equals(sqlDate, java8Time.sqlDate) &&
                    Objects.equals(timestamp, java8Time.timestamp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(instant, localDate, localDateTime, localTime, offsetDateTime, offsetTime, zonedDateTime, monthDay, yearMonth, year, zoneOffset, duration, period, date, sqlDate, timestamp);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Java8Time.class.getSimpleName() + "[", "]")
                    .add("instant=" + instant)
                    .add("localDate=" + localDate)
                    .add("localDateTime=" + localDateTime)
                    .add("localTime=" + localTime)
                    .add("offsetDateTime=" + offsetDateTime)
                    .add("offsetTime=" + offsetTime)
                    .add("zonedDateTime=" + zonedDateTime)
                    .add("monthDay=" + monthDay)
                    .add("yearMonth=" + yearMonth)
                    .add("year=" + year)
                    .add("zoneOffset=" + zoneOffset)
                    .add("duration=" + duration)
                    .add("period=" + period)
                    .add("date=" + date)
                    .add("sqlDate=" + sqlDate)
                    .add("timestamp=" + timestamp)
                    .toString();
        }
    }
}