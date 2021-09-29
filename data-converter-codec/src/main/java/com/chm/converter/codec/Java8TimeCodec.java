package com.chm.converter.codec;

import com.chm.converter.core.Converter;
import com.chm.converter.core.constant.TimeConstant;
import com.chm.converter.core.utils.DateUtil;
import com.chm.converter.core.utils.StringUtil;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.Objects;
import java.util.TimeZone;

/**
 * jdk8时间类编解码器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-02
 **/
public class Java8TimeCodec<T extends TemporalAccessor> implements Codec<T, String> {

    private final Class<T> clazz;

    private final DateTimeFormatter dateFormatter;

    private final Converter<?> converter;

    private final DateTimeFormatter defaultDateTimeFormatter;

    private final TemporalQuery<T> temporalQuery;

    public Java8TimeCodec(Class<T> clazz) {
        this(clazz, (DateTimeFormatter) null, null);
    }

    public Java8TimeCodec(Class<T> clazz, String datePattern) {
        this(clazz, datePattern, null);
    }

    public Java8TimeCodec(Class<T> clazz, DateTimeFormatter dateFormatter) {
        this(clazz, dateFormatter, null);
    }

    public Java8TimeCodec(Class<T> clazz, Converter<?> converter) {
        this(clazz, (DateTimeFormatter) null, converter);
    }

    public Java8TimeCodec(Class<T> clazz, String datePattern, Converter<?> converter) {
        Objects.requireNonNull(clazz, "clazz must not be null");
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

    public Java8TimeCodec(Class<T> clazz, DateTimeFormatter dateFormatter, Converter<?> converter) {
        Objects.requireNonNull(clazz, "clazz must not be null");
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

    public Java8TimeCodec<T> withClass(Class<T> clazz) {
        return new Java8TimeCodec<>(clazz, this.dateFormatter, this.converter);
    }

    public Java8TimeCodec<T> withDatePattern(String datePattern) {
        return new Java8TimeCodec<>(this.clazz, datePattern, this.converter);
    }

    public Java8TimeCodec<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new Java8TimeCodec<>(this.clazz, dateFormatter, this.converter);
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public DateTimeFormatter getDateFormatter() {
        return dateFormatter;
    }

    public Converter<?> getConverter() {
        return converter;
    }

    public DateTimeFormatter getDefaultDateTimeFormatter() {
        return defaultDateTimeFormatter;
    }

    public TemporalQuery<T> getTemporalQuery() {
        return temporalQuery;
    }

    /**
     * 将jdk8时间类序列化成字符串
     *
     * @param t
     * @return
     */
    @Override
    public String encode(T t) {
        if (t == null) {
            return null;
        }

        DateTimeFormatter dtf = this.dateFormatter;
        if (converter != null && dtf == null) {
            dtf = converter.getDateFormat();
        }

        String str;
        if (dtf != null) {
            if (t instanceof Instant && dtf.getZone() == null) {
                // Instant类需设置时区
                TimeZone timeZone = this.converter != null ? this.converter.getTimeZone() : TimeZone.getDefault();
                dtf = dtf.withZone(timeZone.toZoneId());
            }
            str = DateUtil.format(t, dtf);
        } else {
            str = DateUtil.format(t, defaultDateTimeFormatter);
        }
        return str;
    }

    @Override
    public T decode(String timeStr) {
        return decode(timeStr, null);
    }

    /**
     * 将字符串反序列化成jdk8时间类
     *
     * @param timeStr
     * @param format
     * @param <T>
     * @return
     */
    public <T> T decode(String timeStr, String format) {
        if (StringUtil.isBlank(timeStr)) {
            return null;
        }

        DateTimeFormatter dtf = this.dateFormatter;
        if (dtf == null && format != null) {
            dtf = DateTimeFormatter.ofPattern(format);
        }

        if (this.converter != null && dtf == null) {
            dtf = this.converter.getDateFormat();
        }

        if (dtf != null) {
            if (clazz == Instant.class && dtf.getZone() == null) {
                // Instant类需设置时区
                TimeZone timeZone = this.converter != null ? this.converter.getTimeZone() : TimeZone.getDefault();
                dtf = dtf.withZone(timeZone.toZoneId());
            }
            return (T) dtf.parse(timeStr, temporalQuery);
        } else {
            return (T) defaultDateTimeFormatter.parse(timeStr, temporalQuery);
        }
    }

}
