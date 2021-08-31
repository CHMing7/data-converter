package com.chm.converter.jackson.deserializer;

import cn.hutool.core.util.StrUtil;
import com.chm.converter.constant.TimeConstant;
import com.chm.converter.jackson.JacksonModule;
import com.chm.converter.json.JsonConverter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.TimeZone;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-14
 **/
public class JacksonJava8TimeDeserializer<T extends TemporalAccessor> extends JsonDeserializer<T> {

    private final Class<T> clazz;

    /**
     * DateTimeFormatter for writing DateTime value to json string
     */
    private final DateTimeFormatter dateFormatter;

    private final JsonConverter jsonConverter;

    private final DateTimeFormatter defaultDateTimeFormatter;

    private final TemporalQuery<T> temporalQuery;

    public JacksonJava8TimeDeserializer(Class<T> clazz) {
        this(clazz, (DateTimeFormatter) null, null);
    }

    public JacksonJava8TimeDeserializer(Class<T> clazz, String datePattern) {
        this(clazz, datePattern, null);
    }

    public JacksonJava8TimeDeserializer(Class<T> clazz, DateTimeFormatter dateFormatter) {
        this(clazz, dateFormatter, null);
    }

    public JacksonJava8TimeDeserializer(Class<T> clazz, JsonConverter jsonConverter) {
        this(clazz, (DateTimeFormatter) null, jsonConverter);
    }

    public JacksonJava8TimeDeserializer(Class<T> clazz, String datePattern, JsonConverter jsonConverter) {
        this.clazz = clazz;
        if (StrUtil.isNotBlank(datePattern)) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);
            if (clazz == Instant.class && dateFormatter.getZone() == null) {
                TimeZone timeZone = jsonConverter != null ? jsonConverter.getTimeZone() : TimeZone.getDefault();
                dateFormatter = dateFormatter.withZone(timeZone.toZoneId());
            }
            this.dateFormatter = dateFormatter;
        } else {
            this.dateFormatter = null;
        }
        this.jsonConverter = jsonConverter;
        this.defaultDateTimeFormatter = TimeConstant.JAVA8_TIME_DEFAULT_FORMATTER_MAP.get(clazz);
        this.temporalQuery = (TemporalQuery<T>) TimeConstant.CLASS_TEMPORAL_QUERY_MAP.get(clazz);
    }

    public JacksonJava8TimeDeserializer(Class<T> clazz, DateTimeFormatter dateFormatter, JsonConverter jsonConverter) {
        this.clazz = clazz;
        if (dateFormatter != null && clazz == Instant.class && dateFormatter.getZone() == null) {
            TimeZone timeZone = jsonConverter != null ? jsonConverter.getTimeZone() : TimeZone.getDefault();
            dateFormatter = dateFormatter.withZone(timeZone.toZoneId());
        }
        this.dateFormatter = dateFormatter;
        this.jsonConverter = jsonConverter;
        this.defaultDateTimeFormatter = TimeConstant.JAVA8_TIME_DEFAULT_FORMATTER_MAP.get(clazz);
        this.temporalQuery = (TemporalQuery<T>) TimeConstant.CLASS_TEMPORAL_QUERY_MAP.get(clazz);
    }

    public JacksonJava8TimeDeserializer<T> withClass(Class<T> clazz) {
        return new JacksonJava8TimeDeserializer<>(clazz, this.dateFormatter, this.jsonConverter);
    }

    public JacksonJava8TimeDeserializer<T> withDatePattern(String datePattern) {
        return new JacksonJava8TimeDeserializer<>(this.clazz, datePattern, this.jsonConverter);
    }

    public JacksonJava8TimeDeserializer<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new JacksonJava8TimeDeserializer<>(this.clazz, dateFormatter, this.jsonConverter);
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (p.currentTokenId() == JsonToken.VALUE_NULL.id()) {
            return null;
        }
        String str = p.getText();
        if (StrUtil.isBlank(str)) {
            return null;
        }
        DateTimeFormatter dateFormatter = JacksonModule.getDateFormatter(this.dateFormatter, jsonConverter, ctxt);

        if (dateFormatter != null) {
            // Instant类需设置时区
            if (clazz == Instant.class && dateFormatter.getZone() == null) {
                dateFormatter = dateFormatter.withZone(ctxt.getTimeZone().toZoneId());
            }
            return dateFormatter.parse(str, temporalQuery);
        } else {
            return defaultDateTimeFormatter.parse(str, temporalQuery);
        }
    }
}
