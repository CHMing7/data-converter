package com.chm.converter.json.gson;

import cn.hutool.core.util.StrUtil;
import com.chm.converter.core.constant.TimeConstant;
import com.chm.converter.core.Converter;
import com.chm.converter.core.utils.DateUtil;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.TimeZone;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-09
 **/
public class GsonJava8TimeAdapter<T extends TemporalAccessor> extends TypeAdapter<T> {

    private final Class<T> clazz;

    /**
     * DateTimeFormatter for writing DateTime value to json string
     */
    private final DateTimeFormatter dateFormatter;

    private final Converter<?> converter;

    private final DateTimeFormatter defaultDateTimeFormatter;

    private final TemporalQuery<T> temporalQuery;

    public GsonJava8TimeAdapter(Class<T> clazz) {
        this(clazz, (DateTimeFormatter) null, null);
    }

    public GsonJava8TimeAdapter(Class<T> clazz, String datePattern) {
        this(clazz, datePattern, null);
    }

    public GsonJava8TimeAdapter(Class<T> clazz, DateTimeFormatter dateFormatter) {
        this(clazz, dateFormatter, null);
    }

    public GsonJava8TimeAdapter(Class<T> clazz, Converter<?> converter) {
        this(clazz, (DateTimeFormatter) null, converter);
    }

    public GsonJava8TimeAdapter(Class<T> clazz, String datePattern, Converter<?> converter) {
        this.clazz = clazz;
        if (StrUtil.isNotBlank(datePattern)) {
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

    public GsonJava8TimeAdapter(Class<T> clazz, DateTimeFormatter dateFormatter, Converter<?> converter) {
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

    public GsonJava8TimeAdapter<T> withDatePattern(String datePattern) {
        return new GsonJava8TimeAdapter<>(this.clazz, datePattern, this.converter);
    }

    public GsonJava8TimeAdapter<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new GsonJava8TimeAdapter<>(this.clazz, dateFormatter, this.converter);
    }

    public GsonJava8TimeAdapter<T> withClass(Class<T> clazz) {
        return new GsonJava8TimeAdapter<>(clazz, this.dateFormatter, this.converter);
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        DateTimeFormatter dtf = this.dateFormatter;

        if (dtf == null && this.converter != null) {
            dtf = this.converter.getDateFormat();
        }
        String str;
        if (dtf != null) {
            if (value instanceof Instant && dtf.getZone() == null) {
                // Instant类需设置时区
                TimeZone timeZone = this.converter != null ? this.converter.getTimeZone() : TimeZone.getDefault();
                dtf = dtf.withZone(timeZone.toZoneId());
            }
            str = DateUtil.format(value, dtf);
        } else {
            str = DateUtil.format(value, defaultDateTimeFormatter);
        }
        out.value(str);
    }

    @Override
    public T read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String str = in.nextString();
        return (T) deserializeToTemporalAccessor(str);
    }

    private TemporalAccessor deserializeToTemporalAccessor(String str) {
        if (StrUtil.isBlank(str)) {
            return null;
        }
        DateTimeFormatter dtf = this.dateFormatter;
        if (dtf == null && this.converter != null) {
            dtf = this.converter.getDateFormat();
        }
        if (dtf != null) {
            if (clazz == Instant.class && dtf.getZone() == null) {
                // Instant类需设置时区
                TimeZone timeZone = this.converter != null ? this.converter.getTimeZone() : TimeZone.getDefault();
                dtf = dtf.withZone(timeZone.toZoneId());
            }
            return dtf.parse(str, temporalQuery);
        } else {
            return defaultDateTimeFormatter.parse(str, temporalQuery);
        }
    }
}
