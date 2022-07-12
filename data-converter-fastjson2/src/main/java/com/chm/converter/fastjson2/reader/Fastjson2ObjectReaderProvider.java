package com.chm.converter.fastjson2.reader;

import com.alibaba.fastjson2.reader.ObjectReaderCreator;
import com.alibaba.fastjson2.reader.ObjectReaderProvider;
import com.chm.converter.core.Converter;
import com.chm.converter.core.UseOriginalJudge;
import com.chm.converter.fastjson2.Fastjson2DefaultDateCodec;
import com.chm.converter.fastjson2.Fastjson2Jdk8DateCodec;

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

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-07-05
 **/
public class Fastjson2ObjectReaderProvider extends ObjectReaderProvider {

    private final Converter<?> converter;

    private final UseOriginalJudge useOriginalJudge;

    public Fastjson2ObjectReaderProvider(Converter<?> converter, UseOriginalJudge useOriginalJudge) {
        this.converter = converter;
        this.useOriginalJudge = useOriginalJudge;
        // Java8 Time Deserializer
        this.register(Instant.class, new Fastjson2Jdk8DateCodec<>(Instant.class, converter));
        this.register(LocalDate.class, new Fastjson2Jdk8DateCodec<>(LocalDate.class, converter));
        this.register(LocalDateTime.class, new Fastjson2Jdk8DateCodec<>(LocalDateTime.class, converter));
        this.register(LocalTime.class, new Fastjson2Jdk8DateCodec<>(LocalTime.class, converter));
        this.register(OffsetDateTime.class, new Fastjson2Jdk8DateCodec<>(OffsetDateTime.class, converter));
        this.register(OffsetTime.class, new Fastjson2Jdk8DateCodec<>(OffsetTime.class, converter));
        this.register(ZonedDateTime.class, new Fastjson2Jdk8DateCodec<>(ZonedDateTime.class, converter));
        this.register(MonthDay.class, new Fastjson2Jdk8DateCodec<>(MonthDay.class, converter));
        this.register(YearMonth.class, new Fastjson2Jdk8DateCodec<>(YearMonth.class, converter));
        this.register(Year.class, new Fastjson2Jdk8DateCodec<>(Year.class, converter));
        this.register(ZoneOffset.class, new Fastjson2Jdk8DateCodec<>(ZoneOffset.class, converter));

        // Default Date Deserializer
        this.register(java.sql.Date.class, new Fastjson2DefaultDateCodec<>(java.sql.Date.class, converter));
        this.register(Timestamp.class, new Fastjson2DefaultDateCodec<>(Timestamp.class, converter));
        this.register(Date.class, new Fastjson2DefaultDateCodec<>(Date.class, converter));

        // register module
        this.register(new Fastjson2ObjectReaderModule(this, converter, useOriginalJudge));
    }

    @Override
    public ObjectReaderCreator getCreator() {
        ObjectReaderCreator creator = super.getCreator();
        return new Fastjson2ObjectReaderCreator(creator, converter, useOriginalJudge);
    }

}
