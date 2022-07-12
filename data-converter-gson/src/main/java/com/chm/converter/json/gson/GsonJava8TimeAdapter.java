package com.chm.converter.json.gson;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codec.WithFormat;
import com.chm.converter.core.codecs.Java8TimeCodec;
import com.chm.converter.core.utils.StringUtil;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-09
 **/
public class GsonJava8TimeAdapter<T extends TemporalAccessor> extends TypeAdapter<T> implements WithFormat {

    private final Java8TimeCodec<T> java8TimeCodec;

    public GsonJava8TimeAdapter(Class<T> clazz) {
        this(clazz, (DateTimeFormatter) null, null);
    }

    public GsonJava8TimeAdapter(Class<T> clazz, String datePattern) {
        this(clazz, datePattern, null);
    }

    public GsonJava8TimeAdapter(Class<T> clazz, DateTimeFormatter dateFormatter) {
        this(clazz, dateFormatter, null);
    }

    public GsonJava8TimeAdapter(Class<T> clazz, Converter<?> converter) {
        this(clazz, (DateTimeFormatter) null, converter);
    }

    public GsonJava8TimeAdapter(Class<T> clazz, String datePattern, Converter<?> converter) {
        this.java8TimeCodec = new Java8TimeCodec<>(clazz, datePattern, converter);
    }

    public GsonJava8TimeAdapter(Class<T> clazz, DateTimeFormatter dateFormatter, Converter<?> converter) {
        this.java8TimeCodec = new Java8TimeCodec<>(clazz, dateFormatter, converter);
    }

    @Override
    public GsonJava8TimeAdapter<T> withDatePattern(String datePattern) {
        return new GsonJava8TimeAdapter<>(this.java8TimeCodec.getClazz(), datePattern, this.java8TimeCodec.getConverter());
    }

    @Override
    public GsonJava8TimeAdapter<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new GsonJava8TimeAdapter<>(this.java8TimeCodec.getClazz(), dateFormatter, this.java8TimeCodec.getConverter());
    }

    public GsonJava8TimeAdapter<T> withClass(Class<T> clazz) {
        return new GsonJava8TimeAdapter<>(clazz, this.java8TimeCodec.getDateFormatter(), this.java8TimeCodec.getConverter());
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        String str = this.java8TimeCodec.encode(value);
        out.value(str);
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
        if (StringUtil.isBlank(str)) {
            return null;
        }
        return this.java8TimeCodec.decode(str);
    }
}
