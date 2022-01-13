package com.chm.converter.test.thrift;

import cn.hutool.log.StaticLog;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;
import com.chm.converter.core.annotation.FieldProperty;
import com.chm.converter.thrift.DefaultThriftConverter;
import org.junit.Before;
import org.junit.Test;

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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-15
 **/
public class TestJava8Time {

    DefaultThriftConverter converter;

    Java8Time java8Time;

    @Before
    public void before() {
        converter = (DefaultThriftConverter) ConverterSelector.select(DataType.THRIFT_BINARY, DefaultThriftConverter.class);
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
        java8Time.setDate(new Date());
        java8Time.setSqlDate(new java.sql.Date(new Date().getTime()));
        java8Time.setTimestamp(new Timestamp(new Date().getTime()));
    }

    @Test
    public void testProtobuf() {
        byte[] encode = converter.encode(java8Time);
        StaticLog.info(new String(encode));
        StaticLog.info(new String(converter.encode(LocalDateTime.now())));
        StaticLog.info(new String(converter.encode(MonthDay.now())));
        StaticLog.info(new String(converter.encode((MonthDay) null)));
        Java8Time java8Time = converter.convertToJavaObject(encode, Java8Time.class);
        assertEquals(java8Time, this.java8Time);
    }

    public static class Java8Time {

        @FieldProperty(name = "instant1", ordinal = 1)
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

        @FieldProperty(format = "yyyy-MM-dd HH:mm:ss.SSS")
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

            if (!Objects.equals(instant, java8Time.instant)) return false;
            if (!Objects.equals(localDate, java8Time.localDate)) return false;
            if (!Objects.equals(localDateTime, java8Time.localDateTime)) return false;
            if (!Objects.equals(localTime, java8Time.localTime)) return false;
            if (!Objects.equals(offsetDateTime, java8Time.offsetDateTime)) return false;
            if (!Objects.equals(offsetTime, java8Time.offsetTime)) return false;
            if (!Objects.equals(zonedDateTime, java8Time.zonedDateTime)) return false;
            if (!Objects.equals(monthDay, java8Time.monthDay)) return false;
            if (!Objects.equals(yearMonth, java8Time.yearMonth)) return false;
            if (!Objects.equals(year, java8Time.year)) return false;
            return Objects.equals(zoneOffset, java8Time.zoneOffset);
        }

    }
}