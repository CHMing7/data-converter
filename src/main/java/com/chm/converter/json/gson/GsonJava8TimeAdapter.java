package com.chm.converter.json.gson;

import cn.hutool.core.date.TemporalAccessorUtil;
import cn.hutool.core.util.StrUtil;
import com.chm.converter.TimeConstant;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;

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
    private DateTimeFormatter dateFormatter;

    private final DateTimeFormatter defaultDateTimeFormatter;

    private final TemporalQuery<T> temporalQuery;

    public GsonJava8TimeAdapter(Class<T> clazz) {
        this(clazz, (String) null);
    }

    public GsonJava8TimeAdapter(Class<T> clazz, String datePattern) {
        this.clazz = clazz;
        if (StrUtil.isNotBlank(datePattern)) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);
            if (clazz == Instant.class && dateFormatter.getZone() == null) {
                dateFormatter = dateFormatter.withZone(ZoneId.systemDefault());
            }
            this.dateFormatter = dateFormatter;
        }
        this.defaultDateTimeFormatter = TimeConstant.JAVA8_TIME_DEFAULT_FORMATTER_MAP.get(clazz);
        this.temporalQuery = (TemporalQuery<T>) TimeConstant.CLASS_TEMPORAL_QUERY_MAP.get(clazz);
    }

    public GsonJava8TimeAdapter(Class<T> clazz, DateTimeFormatter dateFormatter) {
        this.clazz = clazz;
        if (dateFormatter != null && clazz == Instant.class && dateFormatter.getZone() == null) {
            dateFormatter = dateFormatter.withZone(ZoneId.systemDefault());
        }
        this.dateFormatter = dateFormatter;
        this.defaultDateTimeFormatter = TimeConstant.JAVA8_TIME_DEFAULT_FORMATTER_MAP.get(clazz);
        this.temporalQuery = (TemporalQuery<T>) TimeConstant.CLASS_TEMPORAL_QUERY_MAP.get(clazz);
    }

    public GsonJava8TimeAdapter<T> withDatePattern(String datePattern) {
        return new GsonJava8TimeAdapter<>(this.clazz, datePattern);
    }

    public GsonJava8TimeAdapter<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new GsonJava8TimeAdapter<>(this.clazz, dateFormatter);
    }

    public GsonJava8TimeAdapter<T> withClass(Class<T> clazz) {
        return new GsonJava8TimeAdapter<>(clazz, this.dateFormatter);
    }

    public void setDatePattern(String datePattern) {
        if (StrUtil.isNotBlank(datePattern)) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);
            if (clazz == Instant.class && dateFormatter.getZone() == null) {
                dateFormatter = dateFormatter.withZone(ZoneId.systemDefault());
            }
            this.dateFormatter = dateFormatter;
        }
    }

    public void setDateFormatter(DateTimeFormatter dateFormatter) {
        if (dateFormatter != null && clazz == Instant.class && dateFormatter.getZone() == null) {
            dateFormatter = dateFormatter.withZone(ZoneId.systemDefault());
        }
        this.dateFormatter = dateFormatter;
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        DateTimeFormatter dtf = this.dateFormatter;
        String str;
        if (dtf != null) {
            str = TemporalAccessorUtil.format(value, dtf);
        } else {
            str = TemporalAccessorUtil.format(value, defaultDateTimeFormatter);
        }
        if (str != null) {
            out.value(str);
        }
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
        DateTimeFormatter dtf = this.dateFormatter;
        if (StrUtil.isBlank(str)) {
            return null;
        }
        if (dtf != null) {
            return dtf.parse(str, temporalQuery);
        } else {
            return defaultDateTimeFormatter.parse(str, temporalQuery);
        }
    }
}
