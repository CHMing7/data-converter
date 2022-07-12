package com.chm.converter.fastjson2;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.chm.converter.core.Converter;
import com.chm.converter.core.codec.WithFormat;
import com.chm.converter.core.codecs.Java8TimeCodec;
import com.chm.converter.core.utils.StringUtil;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-21
 **/
public class Fastjson2Jdk8DateCodec<T extends TemporalAccessor> implements ObjectWriter<T>, ObjectReader<T>, WithFormat {

    private final Java8TimeCodec<T> java8TimeCodec;

    public Fastjson2Jdk8DateCodec(Class<T> clazz) {
        this(clazz, (DateTimeFormatter) null, null);
    }

    public Fastjson2Jdk8DateCodec(Class<T> clazz, String datePattern) {
        this(clazz, datePattern, null);
    }

    public Fastjson2Jdk8DateCodec(Class<T> clazz, DateTimeFormatter dateFormatter) {
        this(clazz, dateFormatter, null);
    }

    public Fastjson2Jdk8DateCodec(Class<T> clazz, Converter<?> converter) {
        this(clazz, (DateTimeFormatter) null, converter);
    }

    public Fastjson2Jdk8DateCodec(Class<T> clazz, String datePattern, Converter<?> converter) {
        this.java8TimeCodec = new Java8TimeCodec<>(clazz, datePattern, converter);
    }

    public Fastjson2Jdk8DateCodec(Class<T> clazz, DateTimeFormatter dateFormatter, Converter<?> converter) {
        this.java8TimeCodec = new Java8TimeCodec<>(clazz, dateFormatter, converter);
    }

    public Fastjson2Jdk8DateCodec<T> withClass(Class<T> clazz) {
        return new Fastjson2Jdk8DateCodec<>(clazz, this.java8TimeCodec.getDateFormatter(), this.java8TimeCodec.getConverter());
    }

    @Override
    public Fastjson2Jdk8DateCodec<T> withDatePattern(String datePattern) {
        return new Fastjson2Jdk8DateCodec<>(this.java8TimeCodec.getClazz(), datePattern, this.java8TimeCodec.getConverter());
    }

    @Override
    public Fastjson2Jdk8DateCodec<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new Fastjson2Jdk8DateCodec<>(this.java8TimeCodec.getClazz(), dateFormatter, this.java8TimeCodec.getConverter());
    }

    @Override
    public long getFeatures() {
        return ObjectWriter.super.getFeatures();
    }

    @Override
    public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        if (object == null) {
            jsonWriter.writeNull();
            return;
        }
        jsonWriter.writeString(this.java8TimeCodec.encode((T) object));
    }

    @Override
    public T readJSONBObject(JSONReader jsonReader, long features) {
        String str = jsonReader.readString();
        if (StringUtil.isBlank(str)) {
            return null;
        }

        return this.java8TimeCodec.decode(str);
    }

    @Override
    public T readObject(JSONReader jsonReader, long features) {
        String str = jsonReader.readString();
        if (StringUtil.isBlank(str)) {
            return null;
        }

        return this.java8TimeCodec.decode(str);
    }
}
