package com.chm.converter.json.gson;

import cn.hutool.core.util.StrUtil;
import com.chm.converter.json.JsonConverter;
import com.chm.converter.utils.DateUtil;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-10
 **/
public class GsonDefaultDateTypeAdapter<T extends Date> extends TypeAdapter<T> {

    private final Class<T> dateType;

    private final DateTimeFormatter dateFormatter;

    private final JsonConverter jsonConverter;

    private static final String DEFAULT_DATE_PATTERN_STR = "yyyy-MM-dd HH:mm:ss.SSSS";

    /**
     * List of 1 or more different date formats used for de-serialization attempts.
     * The first of them is used for serialization as well.
     */
    private static final DateTimeFormatter DEFAULT_DATE_FORMAT = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN_STR);

    public GsonDefaultDateTypeAdapter(Class<T> dateType) {
        this(dateType, (DateTimeFormatter) null, null);
    }

    public GsonDefaultDateTypeAdapter(Class<T> dateType, String datePattern) {
        this(dateType, datePattern, null);
    }

    public GsonDefaultDateTypeAdapter(Class<T> dateType, DateTimeFormatter dateFormatter) {
        this(dateType, dateFormatter, null);
    }

    public GsonDefaultDateTypeAdapter(Class<T> dateType, JsonConverter jsonConverter) {
        this(dateType, (DateTimeFormatter) null, jsonConverter);
    }

    public GsonDefaultDateTypeAdapter(Class<T> dateType, String datePattern, JsonConverter jsonConverter) {
        this.dateType = verifyDateType(dateType);
        if (StrUtil.isNotBlank(datePattern)) {
            this.dateFormatter = DateTimeFormatter.ofPattern(datePattern);
        } else {
            this.dateFormatter = null;
        }
        this.jsonConverter = jsonConverter;
    }

    public GsonDefaultDateTypeAdapter(Class<T> dateType, DateTimeFormatter dateFormatter, JsonConverter jsonConverter) {
        this.dateType = verifyDateType(dateType);
        this.dateFormatter = dateFormatter;
        this.jsonConverter = jsonConverter;
    }

    private Class<T> verifyDateType(Class<T> dateType) {
        if (dateType != Date.class && dateType != java.sql.Date.class && dateType != Timestamp.class) {
            throw new IllegalArgumentException("Date type must be one of " + Date.class + ", " + Timestamp.class + ", or " + java.sql.Date.class + " but was " + dateType);
        }
        return dateType;
    }

    public GsonDefaultDateTypeAdapter<T> withDateType(Class<T> dateType) {
        return new GsonDefaultDateTypeAdapter<>(dateType, this.dateFormatter, this.jsonConverter);
    }

    public GsonDefaultDateTypeAdapter<T> withDatePattern(String datePattern) {
        return new GsonDefaultDateTypeAdapter<>(this.dateType, datePattern, this.jsonConverter);
    }

    public GsonDefaultDateTypeAdapter<T> withDateFormatter(DateTimeFormatter dateFormat) {
        return new GsonDefaultDateTypeAdapter<>(this.dateType, dateFormat, this.jsonConverter);
    }

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        String dateFormatAsString;
        DateTimeFormatter dateFormatter = this.dateFormatter;
        if (dateFormatter == null && this.jsonConverter != null) {
            dateFormatter = this.jsonConverter.getDateFormat();
        }
        if (dateFormatter != null) {
            dateFormatAsString = DateUtil.format(value, dateFormatter);
        } else {
            dateFormatAsString = DateUtil.format(value, GsonDefaultDateTypeAdapter.DEFAULT_DATE_FORMAT);
        }
        out.value(dateFormatAsString);
    }

    @Override
    public T read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        Date date = deserializeToDate(in.nextString());

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

        DateTimeFormatter dateFormatter = this.dateFormatter;

        if (dateFormatter == null && this.jsonConverter != null) {
            dateFormatter = this.jsonConverter.getDateFormat();
        }
        if (dateFormatter != null) {
            return DateUtil.parseToDate(s, dateFormatter);
        } else {
            return DateUtil.parseToDate(s, GsonDefaultDateTypeAdapter.DEFAULT_DATE_FORMAT);
        }
    }
}
