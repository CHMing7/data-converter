package com.chm.converter.jackson.deserializer;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.DefaultDateCodec;
import com.chm.converter.core.utils.NumberUtil;
import com.fasterxml.jackson.core.JsonParser;
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

    private final DefaultDateCodec<T> defaultDateCodec;

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
        this.defaultDateCodec = new DefaultDateCodec<>(dateType, datePattern, converter);
    }

    public JacksonDefaultDateTypeDeserializer(Class<T> dateType, DateTimeFormatter dateFormatter, Converter<?> converter) {
        this.defaultDateCodec = new DefaultDateCodec<>(dateType, dateFormatter, converter);
    }

    public JacksonDefaultDateTypeDeserializer<T> withClass(Class<T> clazz) {
        return new JacksonDefaultDateTypeDeserializer<>(clazz, this.defaultDateCodec.getDateFormatter(), this.defaultDateCodec.getConverter());
    }

    public JacksonDefaultDateTypeDeserializer<T> withDatePattern(String datePattern) {
        return new JacksonDefaultDateTypeDeserializer<>(this.defaultDateCodec.getDateType(), datePattern, this.defaultDateCodec.getConverter());
    }

    public JacksonDefaultDateTypeDeserializer<T> withDateFormat(DateTimeFormatter dateFormatter) {
        return new JacksonDefaultDateTypeDeserializer<>(this.defaultDateCodec.getDateType(), dateFormatter, this.defaultDateCodec.getConverter());
    }

    protected DeserializationFeature getTimestampsFeature() {
        return DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS;
    }

    protected boolean useTimestamp(DeserializationContext ctxt) {
        // assume that explicit formatter definition implies use of textual format
        return (this.defaultDateCodec.getDateFormatter() == null) && (ctxt != null)
                && ctxt.isEnabled(getTimestampsFeature());
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.currentTokenId() == JsonToken.VALUE_NULL.id()) {
            return null;
        }
        if (useTimestamp(ctxt)) {
            String numberStr = p.getText();
            if (numberStr == null) {
                return null;
            }
            long l = NumberUtil.parseLong(numberStr);
            Date date = new Date(l);
            Class<T> dateType = this.defaultDateCodec.getDateType();
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
        } else {
            String str = p.getText();
            return deserializeToDate(str);
        }
    }

    private T deserializeToDate(String s) {
        if (s == null) {
            return null;
        }
        return this.defaultDateCodec.decode(s);
    }
}
