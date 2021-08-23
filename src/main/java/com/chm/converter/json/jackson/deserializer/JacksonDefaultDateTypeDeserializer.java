package com.chm.converter.json.jackson.deserializer;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-14
 **/
public class JacksonDefaultDateTypeDeserializer<T extends Date> extends JsonDeserializer<T> {

    private DateFormat dateFormat;

    private final Class<T> dateType;

    private static final String DEFAULT_DATE_PATTERN_STR = "yyyy-MM-dd HH:mm:ss";

    /**
     * List of 1 or more different date formats used for de-serialization attempts.
     * The first of them is used for serialization as well.
     */
    private final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(DEFAULT_DATE_PATTERN_STR);

    public JacksonDefaultDateTypeDeserializer(Class<T> dateType) {
        this.dateType = verifyDateType(dateType);
    }

    public JacksonDefaultDateTypeDeserializer(Class<T> dateType, String datePattern) {
        this.dateType = verifyDateType(dateType);
        if (StrUtil.isNotBlank(datePattern)) {
            this.dateFormat = new SimpleDateFormat(datePattern);
        }
    }

    public JacksonDefaultDateTypeDeserializer(Class<T> dateType, DateFormat dateFormat) {
        this.dateType = verifyDateType(dateType);
        this.dateFormat = dateFormat;
    }

    public JacksonDefaultDateTypeDeserializer<T> withDatePattern(String datePattern) {
        return new JacksonDefaultDateTypeDeserializer<>(this.dateType, datePattern);
    }

    public JacksonDefaultDateTypeDeserializer<T> withDateFormat(DateFormat dateFormat) {
        return new JacksonDefaultDateTypeDeserializer<>(this.dateType, dateFormat);
    }

    public JacksonDefaultDateTypeDeserializer<T> withClass(Class<T> clazz) {
        return new JacksonDefaultDateTypeDeserializer<>(clazz, dateFormat);
    }

    private Class<T> verifyDateType(Class<T> dateType) {
        if (dateType != Date.class && dateType != java.sql.Date.class && dateType != Timestamp.class) {
            throw new IllegalArgumentException("Date type must be one of " + Date.class + ", " + Timestamp.class + ", or " + java.sql.Date.class + " but was " + dateType);
        }
        return dateType;
    }

    public void setDateFormat(String datePattern) {
        if (StrUtil.isNotBlank(datePattern)) {
            this.dateFormat = new SimpleDateFormat(datePattern);
        }
    }

    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    protected DeserializationFeature getTimestampsFeature() {
        return DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS;
    }

    protected boolean useTimestamp(DeserializationContext ctxt) {
        // assume that explicit formatter definition implies use of textual format
        return (dateFormat == null) && (ctxt != null)
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
        if (this.dateFormat != null) {
            return DateUtil.parse(s, this.dateFormat);
        } else {
            return DateUtil.parse(s, this.DEFAULT_DATE_FORMAT);
        }
    }
}
