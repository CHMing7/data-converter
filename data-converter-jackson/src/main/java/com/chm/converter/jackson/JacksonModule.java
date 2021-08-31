package com.chm.converter.jackson;

import com.chm.converter.jackson.deserializer.JacksonDefaultDateTypeDeserializer;
import com.chm.converter.jackson.deserializer.JacksonJava8TimeDeserializer;
import com.chm.converter.jackson.serializer.JacksonDefaultDateTypeSerializer;
import com.chm.converter.jackson.serializer.JacksonJava8TimeSerializer;
import com.chm.converter.json.JsonConverter;
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

    public JacksonModule(JsonConverter jsonConverter) {
        // Java8 Time Serializer
        addSerializer(Instant.class, new JacksonJava8TimeSerializer<>(Instant.class, jsonConverter));
        addSerializer(LocalDate.class, new JacksonJava8TimeSerializer<>(LocalDate.class, jsonConverter));
        addSerializer(LocalDateTime.class, new JacksonJava8TimeSerializer<>(LocalDateTime.class, jsonConverter));
        addSerializer(LocalTime.class, new JacksonJava8TimeSerializer<>(LocalTime.class, jsonConverter));
        addSerializer(OffsetDateTime.class, new JacksonJava8TimeSerializer<>(OffsetDateTime.class, jsonConverter));
        addSerializer(OffsetTime.class, new JacksonJava8TimeSerializer<>(OffsetTime.class, jsonConverter));
        addSerializer(ZonedDateTime.class, new JacksonJava8TimeSerializer<>(ZonedDateTime.class, jsonConverter));
        addSerializer(MonthDay.class, new JacksonJava8TimeSerializer<>(MonthDay.class, jsonConverter));
        addSerializer(YearMonth.class, new JacksonJava8TimeSerializer<>(YearMonth.class, jsonConverter));
        addSerializer(Year.class, new JacksonJava8TimeSerializer<>(Year.class, jsonConverter));
        addSerializer(ZoneOffset.class, new JacksonJava8TimeSerializer<>(ZoneOffset.class, jsonConverter));

        // Default Date Serializer
        addSerializer(java.sql.Date.class, new JacksonDefaultDateTypeSerializer<>(jsonConverter));
        addSerializer(Timestamp.class, new JacksonDefaultDateTypeSerializer<>(jsonConverter));
        addSerializer(Date.class, new JacksonDefaultDateTypeSerializer<>(jsonConverter));

        // Java8 Time Deserializer
        addDeserializer(Instant.class, new JacksonJava8TimeDeserializer<>(Instant.class, jsonConverter));
        addDeserializer(LocalDate.class, new JacksonJava8TimeDeserializer<>(LocalDate.class, jsonConverter));
        addDeserializer(LocalDateTime.class, new JacksonJava8TimeDeserializer<>(LocalDateTime.class, jsonConverter));
        addDeserializer(LocalTime.class, new JacksonJava8TimeDeserializer<>(LocalTime.class, jsonConverter));
        addDeserializer(OffsetDateTime.class, new JacksonJava8TimeDeserializer<>(OffsetDateTime.class, jsonConverter));
        addDeserializer(OffsetTime.class, new JacksonJava8TimeDeserializer<>(OffsetTime.class, jsonConverter));
        addDeserializer(ZonedDateTime.class, new JacksonJava8TimeDeserializer<>(ZonedDateTime.class, jsonConverter));
        addDeserializer(MonthDay.class, new JacksonJava8TimeDeserializer<>(MonthDay.class, jsonConverter));
        addDeserializer(YearMonth.class, new JacksonJava8TimeDeserializer<>(YearMonth.class, jsonConverter));
        addDeserializer(Year.class, new JacksonJava8TimeDeserializer<>(Year.class, jsonConverter));
        addDeserializer(ZoneOffset.class, new JacksonJava8TimeDeserializer<>(ZoneOffset.class, jsonConverter));

        // Default Date Serializer
        addDeserializer(java.sql.Date.class, new JacksonDefaultDateTypeDeserializer<>(java.sql.Date.class, jsonConverter));
        addDeserializer(Timestamp.class, new JacksonDefaultDateTypeDeserializer<>(Timestamp.class, jsonConverter));
        addDeserializer(Date.class, new JacksonDefaultDateTypeDeserializer<>(Date.class, jsonConverter));
    }

    public static DateTimeFormatter getDateFormatter(DateTimeFormatter dateFormatter, JsonConverter jsonConverter, DatabindContext databindContext) {
        DateTimeFormatter dtf = dateFormatter;
        if (dtf == null && jsonConverter != null) {
            dtf = jsonConverter.getDateFormat();
        } else if (dtf == null && databindContext != null) {
            // 如果jsonConverter为null，则去jackson设置的DateFormat
            DateFormat dateFormat = databindContext.getConfig().getDateFormat();
            if (dateFormat instanceof SimpleDateFormat) {
                String jacksonDateFormat = ((SimpleDateFormat) dateFormat).toPattern();
                dtf = DateTimeFormatter.ofPattern(jacksonDateFormat);
            }
        }
        return dtf;
    }

}
