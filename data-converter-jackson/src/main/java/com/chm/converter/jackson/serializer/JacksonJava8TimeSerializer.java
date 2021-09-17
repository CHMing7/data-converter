package com.chm.converter.jackson.serializer;

import com.chm.converter.core.Converter;
import com.chm.converter.core.constant.TimeConstant;
import com.chm.converter.core.utils.DateUtil;
import com.chm.converter.core.utils.StringUtil;
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

    private final Converter<?> converter;

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

    public JacksonJava8TimeSerializer(Class<T> clazz, Converter<?> converter) {
        this(clazz, (DateTimeFormatter) null, converter);
    }

    public JacksonJava8TimeSerializer(Class<T> clazz, String datePattern, Converter<?> converter) {
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
    }

    public JacksonJava8TimeSerializer(Class<T> clazz, DateTimeFormatter dateFormatter, Converter<?> converter) {
        this.clazz = clazz;
        if (dateFormatter != null && clazz == Instant.class && dateFormatter.getZone() == null) {
            TimeZone timeZone = converter != null ? converter.getTimeZone() : TimeZone.getDefault();
            dateFormatter = dateFormatter.withZone(timeZone.toZoneId());
        }
        this.dateFormatter = dateFormatter;
        this.converter = converter;
        this.defaultDateTimeFormatter = TimeConstant.JAVA8_TIME_DEFAULT_FORMATTER_MAP.get(clazz);
    }

    public JacksonJava8TimeSerializer<T> withClass(Class<T> clazz) {
        return new JacksonJava8TimeSerializer<>(clazz, this.dateFormatter, this.converter);
    }

    public JacksonJava8TimeSerializer<T> withDatePattern(String datePattern) {
        return new JacksonJava8TimeSerializer<>(this.clazz, datePattern, this.converter);
    }

    public JacksonJava8TimeSerializer<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new JacksonJava8TimeSerializer<>(this.clazz, dateFormatter, this.converter);
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String str;
        DateTimeFormatter dtf = this.dateFormatter;
        if (converter != null && dtf == null) {
            dtf = converter.getDateFormat();
        }

        if (dtf != null) {
            // Instant类需设置时区
            if (value instanceof Instant && dtf.getZone() == null) {
                TimeZone timeZone = this.converter != null ? this.converter.getTimeZone() : serializers.getTimeZone();
                dtf = dtf.withZone(timeZone.toZoneId());
            }
            str = DateUtil.format(value, dtf);
        } else {
            str = DateUtil.format(value, defaultDateTimeFormatter);
        }
        gen.writeString(str);
    }


}
