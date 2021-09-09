package com.chm.converter.json.jackson;

import com.chm.converter.json.jackson.deserializer.JacksonDefaultDateTypeDeserializer;
import com.chm.converter.json.jackson.deserializer.JacksonJava8TimeDeserializer;
import com.chm.converter.json.jackson.serializer.JacksonDefaultDateTypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.sql.Timestamp;
import java.time.*;
import java.util.Date;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-18
 **/
public class JacksonModule extends SimpleModule {

    public JacksonModule() {
        // Java8 Time Serializer
        addSerializer(Instant.class, new JacksonJava8TimeSerializer<>(Instant.class));
        addSerializer(LocalDate.class, new JacksonJava8TimeSerializer<>(LocalDate.class));
        addSerializer(LocalDateTime.class, new JacksonJava8TimeSerializer<>(LocalDateTime.class));
        addSerializer(LocalTime.class, new JacksonJava8TimeSerializer<>(LocalTime.class));
        addSerializer(OffsetDateTime.class, new JacksonJava8TimeSerializer<>(OffsetDateTime.class));
        addSerializer(OffsetTime.class, new JacksonJava8TimeSerializer<>(OffsetTime.class));
        addSerializer(ZonedDateTime.class, new JacksonJava8TimeSerializer<>(ZonedDateTime.class));
        addSerializer(MonthDay.class, new JacksonJava8TimeSerializer<>(MonthDay.class));
        addSerializer(YearMonth.class, new JacksonJava8TimeSerializer<>(YearMonth.class));
        addSerializer(Year.class, new JacksonJava8TimeSerializer<>(Year.class));
        addSerializer(ZoneOffset.class, new JacksonJava8TimeSerializer<>(ZoneOffset.class));

        // Default Date Serializer
        addSerializer(java.sql.Date.class, new JacksonDefaultDateTypeSerializer<>());
        addSerializer(Timestamp.class, new JacksonDefaultDateTypeSerializer<>());
        addSerializer(Date.class, new JacksonDefaultDateTypeSerializer<>());

        // Java8 Time Deserializer
        addDeserializer(Instant.class, new JacksonJava8TimeDeserializer<>(Instant.class));
        addDeserializer(LocalDate.class, new JacksonJava8TimeDeserializer<>(LocalDate.class));
        addDeserializer(LocalDateTime.class, new JacksonJava8TimeDeserializer<>(LocalDateTime.class));
        addDeserializer(LocalTime.class, new JacksonJava8TimeDeserializer<>(LocalTime.class));
        addDeserializer(OffsetDateTime.class, new JacksonJava8TimeDeserializer<>(OffsetDateTime.class));
        addDeserializer(OffsetTime.class, new JacksonJava8TimeDeserializer<>(OffsetTime.class));
        addDeserializer(ZonedDateTime.class, new JacksonJava8TimeDeserializer<>(ZonedDateTime.class));
        addDeserializer(MonthDay.class, new JacksonJava8TimeDeserializer<>(MonthDay.class));
        addDeserializer(YearMonth.class, new JacksonJava8TimeDeserializer<>(YearMonth.class));
        addDeserializer(Year.class, new JacksonJava8TimeDeserializer<>(Year.class));
        addDeserializer(ZoneOffset.class, new JacksonJava8TimeDeserializer<>(ZoneOffset.class));

        // Default Date Serializer
        addDeserializer(java.sql.Date.class, new JacksonDefaultDateTypeDeserializer<>(java.sql.Date.class));
        addDeserializer(Timestamp.class, new JacksonDefaultDateTypeDeserializer<>(Timestamp.class));
        addDeserializer(Date.class, new JacksonDefaultDateTypeDeserializer<>(Date.class));
    }

}
