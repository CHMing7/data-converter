package com.chm.converter.core.codecs;

import com.chm.converter.core.Converter;
import com.chm.converter.core.cfg.ConvertFeature;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.pack.DataReader;
import com.chm.converter.core.pack.DataWriter;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.DateUtil;
import com.chm.converter.core.utils.NumberUtil;
import com.chm.converter.core.utils.StringUtil;

import java.io.IOException;
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

    public Class<T> getDateType() {
        return dateType;
    }

    public DateTimeFormatter getDateFormatter() {
        return dateFormatter;
    }

    public Converter<?> getConverter() {
        return converter;
    }

    @Override
    public String encode(T t) {
        return encode(t, null);
    }

    @Override
    public TypeToken<String> getEncodeType() {
        return TypeToken.get(String.class);
    }

    @Override
    public void writeData(String s, DataWriter dw) throws IOException {
        if (s == null) {
            dw.writeNull();
            return;
        }
        dw.writeString(s);
    }

    public String encode(T t, String format) {
        if (t == null) {
            return null;
        }

        if (useTimestamp(format)) {
            return timestamp(t);
        }
        DateTimeFormatter formatter = getCodecDateFormatter(format);
        return DateUtil.format(t, formatter);
    }

    @Override
    public T decode(String str) {
        return decode(str, null);
    }

    @Override
    public TypeToken<T> getDecodeType() {
        return TypeToken.get(dateType);
    }

    @Override
    public String readData(DataReader dr) throws IOException {
        return dr.readString();
    }

    public <T> T decode(String timeStr, String format) {
        if (StringUtil.isBlank(timeStr)) {
            return null;
        }
        Date date;
        if (useTimestamp(format)) {
            long l = NumberUtil.parseLong(timeStr);
            date = new Date(l);
        } else {
            DateTimeFormatter formatter = getCodecDateFormatter(format);
            date = DateUtil.parseToDate(timeStr, formatter);
        }

        if (dateType == Date.class) {
            return (T) date;
        } else if (dateType == Timestamp.class) {
            return (T) new Timestamp(date.getTime());
        } else if (dateType == java.sql.Date.class) {
            return (T) new java.sql.Date(date.getTime());
        } else {
            // This must never happen: dateType is guarded in the primary constructor
            throw new AssertionError();
        }
    }

    private DateTimeFormatter getCodecDateFormatter(String otherFormat) {
        DateTimeFormatter dtf = null;
        if (StringUtil.isNotBlank(otherFormat)) {
            dtf = DateTimeFormatter.ofPattern(otherFormat);
        }
        if (dtf == null) {
            dtf = this.dateFormatter;
        }
        if (this.converter != null && dtf == null) {
            dtf = this.converter.getDateFormat();
        }

        if (dtf == null) {
            dtf = DEFAULT_DATE_FORMAT;
        }
        return dtf;
    }

    protected boolean useTimestamp(String format) {
        return StringUtil.isBlank(format) && this.dateFormatter == null && this.converter != null &&
                this.converter.getDateFormat() == null &&
                this.converter.isEnabled(ConvertFeature.DATES_AS_TIMESTAMPS);
    }

    protected String timestamp(Date value) {
        return (value == null) ? "" : String.valueOf(value.getTime());
    }
}
