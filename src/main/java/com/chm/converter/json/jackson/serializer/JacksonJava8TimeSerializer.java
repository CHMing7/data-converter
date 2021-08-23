package com.chm.converter.json.jackson.serializer;

import cn.hutool.core.date.TemporalAccessorUtil;
import cn.hutool.core.util.StrUtil;
import com.chm.converter.TimeConstant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

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
    private DateTimeFormatter dateFormatter;

    private final DateTimeFormatter defaultDateTimeFormatter;

    public JacksonJava8TimeSerializer(Class<T> clazz) {
        this(clazz, (String) null);
    }

    public JacksonJava8TimeSerializer(Class<T> clazz, String datePattern) {
        this.clazz = clazz;
        if (StrUtil.isNotBlank(datePattern)) {
            this.dateFormatter = DateTimeFormatter.ofPattern(datePattern);
        }
        this.defaultDateTimeFormatter = TimeConstant.JAVA8_TIME_DEFAULT_FORMATTER_MAP.get(clazz);
    }

    public JacksonJava8TimeSerializer(Class<T> clazz, DateTimeFormatter dateFormatter) {
        this.clazz = clazz;
        this.dateFormatter = dateFormatter;
        this.defaultDateTimeFormatter = TimeConstant.JAVA8_TIME_DEFAULT_FORMATTER_MAP.get(clazz);
    }

    public JacksonJava8TimeSerializer<T> withDatePattern(String datePattern) {
        return new JacksonJava8TimeSerializer<>(this.clazz, datePattern);
    }

    public JacksonJava8TimeSerializer<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new JacksonJava8TimeSerializer<>(this.clazz, dateFormatter);
    }

    public JacksonJava8TimeSerializer<T> withClass(Class<T> clazz) {
        return new JacksonJava8TimeSerializer<>(clazz, this.dateFormatter);
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
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String str;
        DateTimeFormatter dateFormatter = this.dateFormatter;
        if (dateFormatter == null) {
            DateFormat dateFormat = serializers.getConfig().getDateFormat();
            if (dateFormat instanceof SimpleDateFormat) {
                String jacksonDateFormat = ((SimpleDateFormat) dateFormat).toPattern();
                dateFormatter = DateTimeFormatter.ofPattern(jacksonDateFormat);
            }
        }
        if (dateFormatter != null) {
            if (value instanceof Instant && dateFormatter.getZone() == null) {
                dateFormatter = dateFormatter.withZone(serializers.getTimeZone().toZoneId());
            }
            str = TemporalAccessorUtil.format(value, dateFormatter);
        } else {
            str = TemporalAccessorUtil.format(value, defaultDateTimeFormatter);
        }
        gen.writeString(str);
    }
}
