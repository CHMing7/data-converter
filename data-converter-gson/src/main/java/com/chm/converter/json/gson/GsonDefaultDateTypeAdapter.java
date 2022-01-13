package com.chm.converter.json.gson;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.DefaultDateCodec;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-10
 **/
public class GsonDefaultDateTypeAdapter<T extends Date> extends TypeAdapter<T> {

    private final DefaultDateCodec<T> defaultDateCodec;

    public GsonDefaultDateTypeAdapter(Class<T> dateType) {
        this(dateType, (DateTimeFormatter) null, null);
    }

    public GsonDefaultDateTypeAdapter(Class<T> dateType, String datePattern) {
        this(dateType, datePattern, null);
    }

    public GsonDefaultDateTypeAdapter(Class<T> dateType, DateTimeFormatter dateFormatter) {
        this(dateType, dateFormatter, null);
    }

    public GsonDefaultDateTypeAdapter(Class<T> dateType, Converter<?> converter) {
        this(dateType, (DateTimeFormatter) null, converter);
    }

    public GsonDefaultDateTypeAdapter(Class<T> dateType, String datePattern, Converter<?> converter) {
        this.defaultDateCodec = new DefaultDateCodec<>(dateType, datePattern, converter);
    }

    public GsonDefaultDateTypeAdapter(Class<T> dateType, DateTimeFormatter dateFormatter, Converter<?> converter) {
        this.defaultDateCodec = new DefaultDateCodec<>(dateType, dateFormatter, converter);
    }

    public GsonDefaultDateTypeAdapter<T> withDateType(Class<T> dateType) {
        return new GsonDefaultDateTypeAdapter<>(dateType, this.defaultDateCodec.getDateFormatter(), this.defaultDateCodec.getConverter());
    }

    public GsonDefaultDateTypeAdapter<T> withDatePattern(String datePattern) {
        return new GsonDefaultDateTypeAdapter<>(this.defaultDateCodec.getDateType(), datePattern, this.defaultDateCodec.getConverter());
    }

    public GsonDefaultDateTypeAdapter<T> withDateFormatter(DateTimeFormatter dateFormat) {
        return new GsonDefaultDateTypeAdapter<>(this.defaultDateCodec.getDateType(), dateFormat, this.defaultDateCodec.getConverter());
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        String dateFormatAsString = this.defaultDateCodec.encode(value);
        out.value(dateFormatAsString);
    }

    @Override
    public T read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return this.defaultDateCodec.decode(in.nextString());
    }
}
