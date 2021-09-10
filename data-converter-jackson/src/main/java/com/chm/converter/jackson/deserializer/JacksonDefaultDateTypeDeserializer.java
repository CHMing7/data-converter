package com.chm.converter.jackson.deserializer;

import cn.hutool.core.util.StrUtil;
import com.chm.converter.core.Converter;
import com.chm.converter.core.utils.DateUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-14
 **/
public class JacksonDefaultDateTypeDeserializer<T extends Date> extends JsonDeserializer<T> {

    private final Class<T> dateType;

    private final DateTimeFormatter dateFormatter;

    private final Converter<?> converter;

    private static final String DEFAULT_DATE_PATTERN_STR = "yyyy-MM-dd HH:mm:ss.SSSS";

    /**
     * List of 1 or more different date formats used for de-serialization attempts.
     * The first of them is used for serialization as well.
     */
    private static final DateTimeFormatter DEFAULT_DATE_FORMAT = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN_STR);

    public JacksonDefaultDateTypeDeserializer(Class<T> dateType) {
        this(dateType, (DateTimeFormatter) null, null);
    }

    public JacksonDefaultDateTypeDeserializer(Class<T> dateType, String datePattern) {
        this(dateType, datePattern, null);
    }

    public JacksonDefaultDateTypeDeserializer(Class<T> dateType, DateTimeFormatter dateFormatter) {
        this(dateType, dateFormatter, null);
    }

    public JacksonDefaultDateTypeDeserializer(Class<T> dateType, Converter<?> converter) {
        this(dateType, (DateTimeFormatter) null, converter);
    }

    public JacksonDefaultDateTypeDeserializer(Class<T> dateType, String datePattern, Converter<?> converter) {
        this.dateType = verifyDateType(dateType);
        if (StrUtil.isNotBlank(datePattern)) {
            this.dateFormatter = DateTimeFormatter.ofPattern(datePattern);
        } else {
            this.dateFormatter = null;
        }
        this.converter = converter;
    }

    public JacksonDefaultDateTypeDeserializer(Class<T> dateType, DateTimeFormatter dateFormatter, Converter<?> converter) {
        this.dateType = verifyDateType(dateType);
        this.dateFormatter = dateFormatter;
        this.converter = converter;
    }

    public JacksonDefaultDateTypeDeserializer<T> withClass(Class<T> clazz) {
        return new JacksonDefaultDateTypeDeserializer<>(clazz, this.dateFormatter, this.converter);
    }

    public JacksonDefaultDateTypeDeserializer<T> withDatePattern(String datePattern) {
        return new JacksonDefaultDateTypeDeserializer<>(this.dateType, datePattern, this.converter);
    }

    public JacksonDefaultDateTypeDeserializer<T> withDateFormat(DateTimeFormatter dateFormatter) {
        return new JacksonDefaultDateTypeDeserializer<>(this.dateType, dateFormatter, this.converter);
    }

    private Class<T> verifyDateType(Class<T> dateType) {
        if (dateType != Date.class && dateType != java.sql.Date.class && dateType != Timestamp.class) {
            throw new IllegalArgumentException("Date type must be one of " + Date.class + ", " + Timestamp.class + ", or " + java.sql.Date.class + " but was " + dateType);
        }
        return dateType;
    }

    protected DeserializationFeature getTimestampsFeature() {
        return DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS;
    }

    protected boolean useTimestamp(DeserializationContext ctxt) {
        // assume that explicit formatter definition implies use of textual format
        return (dateFormatter == null) && (ctxt != null)
                && ctxt.isEnabled(getTimestampsFeature());
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (p.currentTokenId() == JsonToken.VALUE_NULL.id()) {
            return null;
        }
        Date date;
        if (useTimestamp(ctxt)) {
            Number numberValue = p.getNumberValue();
            if (numberValue == null) {
                return null;
            }
            date = new Date(numberValue.longValue());
        } else {
            String str = p.getText();
            date = deserializeToDate(str);
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

    private Date deserializeToDate(String s) {
        if (s == null) {
            return null;
        }
        DateTimeFormatter dtf = this.dateFormatter;
        if (converter != null && dtf == null) {
            dtf = converter.getDateFormat();
        }

        if (dtf != null) {
            return DateUtil.parseToDate(s, dtf);
        } else {
            return DateUtil.parseToDate(s, DEFAULT_DATE_FORMAT);
        }
    }
}
