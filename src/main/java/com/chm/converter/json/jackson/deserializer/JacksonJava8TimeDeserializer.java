package com.chm.converter.json.jackson.deserializer;

import cn.hutool.core.util.StrUtil;
import com.chm.converter.TimeConstant;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;

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
    private DateTimeFormatter dateFormatter;

    private final DateTimeFormatter defaultDateTimeFormatter;

    private final TemporalQuery<T> temporalQuery;

    public JacksonJava8TimeDeserializer(Class<T> clazz) {
        this(clazz, (String) null);
    }

    public JacksonJava8TimeDeserializer(Class<T> clazz, String datePattern) {
        this.clazz = clazz;
        if (StrUtil.isNotBlank(datePattern)) {
            this.dateFormatter = DateTimeFormatter.ofPattern(datePattern);
        }
        this.defaultDateTimeFormatter = TimeConstant.JAVA8_TIME_DEFAULT_FORMATTER_MAP.get(clazz);
        this.temporalQuery = (TemporalQuery<T>) TimeConstant.CLASS_TEMPORAL_QUERY_MAP.get(clazz);
    }

    public JacksonJava8TimeDeserializer(Class<T> clazz, DateTimeFormatter dateFormatter) {
        this.clazz = clazz;
        this.dateFormatter = dateFormatter;
        this.defaultDateTimeFormatter = TimeConstant.JAVA8_TIME_DEFAULT_FORMATTER_MAP.get(clazz);
        this.temporalQuery = (TemporalQuery<T>) TimeConstant.CLASS_TEMPORAL_QUERY_MAP.get(clazz);
    }

    public JacksonJava8TimeDeserializer<T> withDatePattern(String datePattern) {
        return new JacksonJava8TimeDeserializer<>(this.clazz, datePattern);
    }

    public JacksonJava8TimeDeserializer<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new JacksonJava8TimeDeserializer<>(this.clazz, dateFormatter);
    }

    public JacksonJava8TimeDeserializer<T> withClass(Class<T> clazz) {
        return new JacksonJava8TimeDeserializer<>(clazz, this.dateFormatter);
    }

    public void setDateFormatter(String datePattern) {
        if (StrUtil.isNotBlank(datePattern)) {
            this.dateFormatter = DateTimeFormatter.ofPattern(datePattern);
        }
    }

    public void setDateFormatter(DateTimeFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
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
        DateTimeFormatter dateFormatter = this.dateFormatter;
        if (dateFormatter == null) {
            DateFormat dateFormat = ctxt.getConfig().getDateFormat();
            if (dateFormat instanceof SimpleDateFormat) {
                String jacksonDateFormat = ((SimpleDateFormat) dateFormat).toPattern();
                dateFormatter = DateTimeFormatter.ofPattern(jacksonDateFormat);
            }
        }
        if (dateFormatter != null && clazz == Instant.class && dateFormatter.getZone() == null) {
            dateFormatter = dateFormatter.withZone(ctxt.getTimeZone().toZoneId());
        }
        if (dateFormatter != null) {
            return dateFormatter.parse(str, temporalQuery);
        } else {
            return defaultDateTimeFormatter.parse(str, temporalQuery);
        }
    }
}
