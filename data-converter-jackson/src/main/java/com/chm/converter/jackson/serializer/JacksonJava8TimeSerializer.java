package com.chm.converter.jackson.serializer;

import cn.hutool.core.util.StrUtil;
import com.chm.converter.constant.TimeConstant;
import com.chm.converter.jackson.JacksonModule;
import com.chm.converter.json.JsonConverter;
import com.chm.converter.utils.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.TimeZone;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-11
 **/
public class JacksonJava8TimeSerializer<T extends TemporalAccessor> extends JsonSerializer<T> {

    private final Class<T> clazz;

    /**
     * DateTimeFormatter for writing DateTime value to json string
     */
    private final DateTimeFormatter dateFormatter;

    private final JsonConverter jsonConverter;

    private final DateTimeFormatter defaultDateTimeFormatter;

    public JacksonJava8TimeSerializer(Class<T> clazz) {
        this(clazz, (DateTimeFormatter) null, null);
    }

    public JacksonJava8TimeSerializer(Class<T> clazz, String datePattern) {
        this(clazz, datePattern, null);
    }

    public JacksonJava8TimeSerializer(Class<T> clazz, DateTimeFormatter dateFormatter) {
        this(clazz, dateFormatter, null);
    }

    public JacksonJava8TimeSerializer(Class<T> clazz, JsonConverter jsonConverter) {
        this(clazz, (DateTimeFormatter) null, jsonConverter);
    }

    public JacksonJava8TimeSerializer(Class<T> clazz, String datePattern, JsonConverter jsonConverter) {
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
    }

    public JacksonJava8TimeSerializer(Class<T> clazz, DateTimeFormatter dateFormatter, JsonConverter jsonConverter) {
        this.clazz = clazz;
        if (dateFormatter != null && clazz == Instant.class && dateFormatter.getZone() == null) {
            TimeZone timeZone = jsonConverter != null ? jsonConverter.getTimeZone() : TimeZone.getDefault();
            dateFormatter = dateFormatter.withZone(timeZone.toZoneId());
        }
        this.dateFormatter = dateFormatter;
        this.jsonConverter = jsonConverter;
        this.defaultDateTimeFormatter = TimeConstant.JAVA8_TIME_DEFAULT_FORMATTER_MAP.get(clazz);
    }

    public JacksonJava8TimeSerializer<T> withClass(Class<T> clazz) {
        return new JacksonJava8TimeSerializer<>(clazz, this.dateFormatter, this.jsonConverter);
    }

    public JacksonJava8TimeSerializer<T> withDatePattern(String datePattern) {
        return new JacksonJava8TimeSerializer<>(this.clazz, datePattern, this.jsonConverter);
    }

    public JacksonJava8TimeSerializer<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new JacksonJava8TimeSerializer<>(this.clazz, dateFormatter, this.jsonConverter);
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String str;
        DateTimeFormatter dateFormatter = JacksonModule.getDateFormatter(this.dateFormatter, jsonConverter, serializers);

        if (dateFormatter != null) {
            // Instant类需设置时区
            if (value instanceof Instant && dateFormatter.getZone() == null) {
                TimeZone timeZone = this.jsonConverter != null ? this.jsonConverter.getTimeZone() : serializers.getTimeZone();
                dateFormatter = dateFormatter.withZone(timeZone.toZoneId());
            }
            str = DateUtil.format(value, dateFormatter);
        } else {
            str = DateUtil.format(value, defaultDateTimeFormatter);
        }
        gen.writeString(str);
    }


}
