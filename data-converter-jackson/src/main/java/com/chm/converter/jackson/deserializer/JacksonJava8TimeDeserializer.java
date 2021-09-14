package com.chm.converter.jackson.deserializer;

import com.chm.converter.core.constant.TimeConstant;
import com.chm.converter.core.Converter;
import com.chm.converter.core.utils.StringUtil;
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

    private final Converter<?> converter;

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

    public JacksonJava8TimeDeserializer(Class<T> clazz, Converter<?> converter) {
        this(clazz, (DateTimeFormatter) null, converter);
    }

    public JacksonJava8TimeDeserializer(Class<T> clazz, String datePattern, Converter<?> converter) {
        this.clazz = clazz;
        if (StringUtil.isNotBlank(datePattern)) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);
            if (clazz == Instant.class && dateFormatter.getZone() == null) {
                TimeZone timeZone = converter != null ? converter.getTimeZone() : TimeZone.getDefault();
                dateFormatter = dateFormatter.withZone(timeZone.toZoneId());
            }
            this.dateFormatter = dateFormatter;
        } else {
            this.dateFormatter = null;
        }
        this.converter = converter;
        this.defaultDateTimeFormatter = TimeConstant.JAVA8_TIME_DEFAULT_FORMATTER_MAP.get(clazz);
        this.temporalQuery = (TemporalQuery<T>) TimeConstant.CLASS_TEMPORAL_QUERY_MAP.get(clazz);
    }

    public JacksonJava8TimeDeserializer(Class<T> clazz, DateTimeFormatter dateFormatter, Converter<?> converter) {
        this.clazz = clazz;
        if (dateFormatter != null && clazz == Instant.class && dateFormatter.getZone() == null) {
            TimeZone timeZone = converter != null ? converter.getTimeZone() : TimeZone.getDefault();
            dateFormatter = dateFormatter.withZone(timeZone.toZoneId());
        }
        this.dateFormatter = dateFormatter;
        this.converter = converter;
        this.defaultDateTimeFormatter = TimeConstant.JAVA8_TIME_DEFAULT_FORMATTER_MAP.get(clazz);
        this.temporalQuery = (TemporalQuery<T>) TimeConstant.CLASS_TEMPORAL_QUERY_MAP.get(clazz);
    }

    public JacksonJava8TimeDeserializer<T> withClass(Class<T> clazz) {
        return new JacksonJava8TimeDeserializer<>(clazz, this.dateFormatter, this.converter);
    }

    public JacksonJava8TimeDeserializer<T> withDatePattern(String datePattern) {
        return new JacksonJava8TimeDeserializer<>(this.clazz, datePattern, this.converter);
    }

    public JacksonJava8TimeDeserializer<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new JacksonJava8TimeDeserializer<>(this.clazz, dateFormatter, this.converter);
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (p.currentTokenId() == JsonToken.VALUE_NULL.id()) {
            return null;
        }
        String str = p.getText();
        if (StringUtil.isBlank(str)) {
            return null;
        }

        DateTimeFormatter dtf = this.dateFormatter;
        if (converter != null && dtf == null) {
            dtf = converter.getDateFormat();
        }
        if (dtf != null) {
            // Instant类需设置时区
            if (clazz == Instant.class && dtf.getZone() == null) {
                dtf = dtf.withZone(ctxt.getTimeZone().toZoneId());
            }
            return dtf.parse(str, temporalQuery);
        } else {
            return defaultDateTimeFormatter.parse(str, temporalQuery);
        }
    }
}
