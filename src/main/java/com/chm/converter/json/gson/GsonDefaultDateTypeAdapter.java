package com.chm.converter.json.gson;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-10
 **/
public class GsonDefaultDateTypeAdapter<T extends Date> extends TypeAdapter<T> {

    private static final String SIMPLE_NAME = "GsonDefaultDateTypeAdapter";

    private DateFormat dateFormat;

    private final Class<T> dateType;

    private static final String DEFAULT_DATE_PATTERN_STR = "yyyy-MM-dd HH:mm:ss";

    /**
     * List of 1 or more different date formats used for de-serialization attempts.
     * The first of them is used for serialization as well.
     */
    private final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(DEFAULT_DATE_PATTERN_STR);

    public GsonDefaultDateTypeAdapter(Class<T> dateType) {
        this.dateType = verifyDateType(dateType);
    }

    public GsonDefaultDateTypeAdapter(Class<T> dateType, String datePattern) {
        this.dateType = verifyDateType(dateType);
        if (StrUtil.isNotBlank(datePattern)) {
            this.dateFormat = new SimpleDateFormat(datePattern);
        }
    }

    public GsonDefaultDateTypeAdapter(Class<T> dateType, DateFormat dateFormat) {
        this.dateType = verifyDateType(dateType);
        this.dateFormat = dateFormat;
    }

    private Class<T> verifyDateType(Class<T> dateType) {
        if (dateType != Date.class && dateType != java.sql.Date.class && dateType != Timestamp.class) {
            throw new IllegalArgumentException("Date type must be one of " + Date.class + ", " + Timestamp.class + ", or " + java.sql.Date.class + " but was " + dateType);
        }
        return dateType;
    }

    public GsonDefaultDateTypeAdapter<T> withDatePattern(String datePattern) {
        return new GsonDefaultDateTypeAdapter<>(this.dateType, datePattern);
    }

    public GsonDefaultDateTypeAdapter<T> withDateFormat(DateFormat dateFormat) {
        return new GsonDefaultDateTypeAdapter<>(this.dateType, dateFormat);
    }

    public GsonDefaultDateTypeAdapter<T> withDateType(Class<T> dateType) {
        return new GsonDefaultDateTypeAdapter<>(dateType, this.dateFormat);
    }

    public void setDatePattern(String datePattern) {
        if (StrUtil.isNotBlank(datePattern)) {
            this.dateFormat = new SimpleDateFormat(datePattern);
        }
    }

    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        String dateFormatAsString;
        if (this.dateFormat != null) {
            dateFormatAsString = DateUtil.format(value, this.dateFormat);
        } else {
            dateFormatAsString = DateUtil.format(value, this.DEFAULT_DATE_FORMAT);
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
        if (this.dateFormat != null) {
            return DateUtil.parse(s, this.dateFormat);
        } else {
            return DateUtil.parse(s, this.DEFAULT_DATE_FORMAT);
        }
    }
}
