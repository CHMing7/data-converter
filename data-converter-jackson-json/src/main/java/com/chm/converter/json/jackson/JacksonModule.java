package com.chm.converter.json.jackson;

import com.chm.converter.core.Converter;
import com.chm.converter.jackson.deserializer.JacksonDefaultDateTypeDeserializer;
import com.chm.converter.jackson.deserializer.JacksonJava8TimeDeserializer;
import com.chm.converter.jackson.serializer.JacksonDefaultDateTypeSerializer;
import com.chm.converter.jackson.serializer.JacksonJava8TimeSerializer;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-18
 **/
public class JacksonModule extends SimpleModule {

    public JacksonModule(Converter<?> converter) {
        super("JacksonModule", PackageVersion.VERSION);
        // Java8 Time Serializer
        addSerializer(Instant.class, new JacksonJava8TimeSerializer<>(Instant.class, converter));
        addSerializer(LocalDate.class, new JacksonJava8TimeSerializer<>(LocalDate.class, converter));
        addSerializer(LocalDateTime.class, new JacksonJava8TimeSerializer<>(LocalDateTime.class, converter));
        addSerializer(LocalTime.class, new JacksonJava8TimeSerializer<>(LocalTime.class, converter));
        addSerializer(OffsetDateTime.class, new JacksonJava8TimeSerializer<>(OffsetDateTime.class, converter));
        addSerializer(OffsetTime.class, new JacksonJava8TimeSerializer<>(OffsetTime.class, converter));
        addSerializer(ZonedDateTime.class, new JacksonJava8TimeSerializer<>(ZonedDateTime.class, converter));
        addSerializer(MonthDay.class, new JacksonJava8TimeSerializer<>(MonthDay.class, converter));
        addSerializer(YearMonth.class, new JacksonJava8TimeSerializer<>(YearMonth.class, converter));
        addSerializer(Year.class, new JacksonJava8TimeSerializer<>(Year.class, converter));
        addSerializer(ZoneOffset.class, new JacksonJava8TimeSerializer<>(ZoneOffset.class, converter));

        // Default Date Serializer
        addSerializer(java.sql.Date.class, new JacksonDefaultDateTypeSerializer<>(converter));
        addSerializer(Timestamp.class, new JacksonDefaultDateTypeSerializer<>(converter));
        addSerializer(Date.class, new JacksonDefaultDateTypeSerializer<>(converter));

        // Java8 Time Deserializer
        addDeserializer(Instant.class, new JacksonJava8TimeDeserializer<>(Instant.class, converter));
        addDeserializer(LocalDate.class, new JacksonJava8TimeDeserializer<>(LocalDate.class, converter));
        addDeserializer(LocalDateTime.class, new JacksonJava8TimeDeserializer<>(LocalDateTime.class, converter));
        addDeserializer(LocalTime.class, new JacksonJava8TimeDeserializer<>(LocalTime.class, converter));
        addDeserializer(OffsetDateTime.class, new JacksonJava8TimeDeserializer<>(OffsetDateTime.class, converter));
        addDeserializer(OffsetTime.class, new JacksonJava8TimeDeserializer<>(OffsetTime.class, converter));
        addDeserializer(ZonedDateTime.class, new JacksonJava8TimeDeserializer<>(ZonedDateTime.class, converter));
        addDeserializer(MonthDay.class, new JacksonJava8TimeDeserializer<>(MonthDay.class, converter));
        addDeserializer(YearMonth.class, new JacksonJava8TimeDeserializer<>(YearMonth.class, converter));
        addDeserializer(Year.class, new JacksonJava8TimeDeserializer<>(Year.class, converter));
        addDeserializer(ZoneOffset.class, new JacksonJava8TimeDeserializer<>(ZoneOffset.class, converter));

        // Default Date Serializer
        addDeserializer(java.sql.Date.class, new JacksonDefaultDateTypeDeserializer<>(java.sql.Date.class, converter));
        addDeserializer(Timestamp.class, new JacksonDefaultDateTypeDeserializer<>(Timestamp.class, converter));
        addDeserializer(Date.class, new JacksonDefaultDateTypeDeserializer<>(Date.class, converter));
    }

}
