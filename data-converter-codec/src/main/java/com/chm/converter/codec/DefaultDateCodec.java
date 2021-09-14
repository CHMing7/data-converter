package com.chm.converter.codec;

import com.chm.converter.core.Converter;
import com.chm.converter.core.utils.DateUtil;
import com.chm.converter.core.utils.StringUtil;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 默认时间类编解码器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-02
 **/
public class DefaultDateCodec<T extends Date> implements Codec<T, String> {

    private final Class<T> dateType;

    private final DateTimeFormatter dateFormatter;

    private final Converter<?> converter;

    private static final String DEFAULT_DATE_PATTERN_STR = "yyyy-MM-dd HH:mm:ss.SSSS";

    /**
     * List of 1 or more different date formats used for de-serialization attempts.
     * The first of them is used for serialization as well.
     */
    private final static DateTimeFormatter DEFAULT_DATE_FORMAT = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN_STR);

    public DefaultDateCodec(Class<T> dateType) {
        this(dateType, (DateTimeFormatter) null, null);
    }

    public DefaultDateCodec(Class<T> dateType, String datePattern) {
        this(dateType, datePattern, null);
    }

    public DefaultDateCodec(Class<T> dateType, DateTimeFormatter dateFormatter) {
        this(dateType, dateFormatter, null);
    }

    public DefaultDateCodec(Class<T> dateType, Converter<?> converter) {
        this(dateType, (DateTimeFormatter) null, converter);
    }

    public DefaultDateCodec(Class<T> dateType, String datePattern, Converter<?> converter) {
        this.dateType = verifyDateType(dateType);
        if (StringUtil.isNotBlank(datePattern)) {
            this.dateFormatter = DateTimeFormatter.ofPattern(datePattern);
        } else {
            this.dateFormatter = null;
        }
        this.converter = converter;
    }

    public DefaultDateCodec(Class<T> dateType, DateTimeFormatter dateFormat, Converter<?> converter) {
        this.dateType = verifyDateType(dateType);
        this.dateFormatter = dateFormat;
        this.converter = converter;
    }

    private Class<T> verifyDateType(Class<T> dateType) {
        if (dateType != Date.class && dateType != java.sql.Date.class && dateType != Timestamp.class) {
            throw new IllegalArgumentException("Date type must be one of " + Date.class + ", " + Timestamp.class + ", or " + java.sql.Date.class + " but was " + dateType);
        }
        return dateType;
    }

    public DefaultDateCodec<T> withDateType(Class<T> dateType) {
        return new DefaultDateCodec<>(dateType, this.dateFormatter, this.converter);
    }

    public DefaultDateCodec<T> withDatePattern(String datePattern) {
        return new DefaultDateCodec<>(this.dateType, datePattern, this.converter);
    }

    public DefaultDateCodec<T> withDateFormat(DateTimeFormatter dateFormat) {
        return new DefaultDateCodec<>(this.dateType, dateFormat, this.converter);
    }

    @Override
    public String encode(T t) {
        if (t == null) {
            return null;
        }

        DateTimeFormatter formatter = getDateFormatter();
        return DateUtil.format(t, formatter);
    }

    @Override
    public T decode(String str) {
        return decode(str, null);
    }

    public <T> T decode(String timeStr, String format) {
        if (StringUtil.isBlank(timeStr)) {
            return null;
        }

        DateTimeFormatter formatter = this.dateFormatter;
        if (formatter == null && format != null) {
            formatter = DateTimeFormatter.ofPattern(format);
        }

        if (formatter == null) {
            formatter = getDateFormatter();
        }

        Date date = DateUtil.parseToDate(timeStr, formatter);
        if (dateType == Date.class) {
            return (T) date;
        } else if (dateType == Timestamp.class) {
            return (T) new Timestamp(date.getTime());
        } else if (dateType == java.sql.Date.class) {
            return (T) new java.sql.Date(date.getTime());
        }
        return (T) date;
    }

    private DateTimeFormatter getDateFormatter() {
        DateTimeFormatter dtf = this.dateFormatter;

        if (converter != null && dtf == null) {
            dtf = converter.getDateFormat();
        }

        if (dtf == null) {
            dtf = DEFAULT_DATE_FORMAT;
        }
        return dtf;
    }
}
