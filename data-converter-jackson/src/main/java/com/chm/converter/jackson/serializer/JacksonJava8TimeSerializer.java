package com.chm.converter.jackson.serializer;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codec.WithFormat;
import com.chm.converter.core.codecs.Java8TimeCodec;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-11
 **/
public class JacksonJava8TimeSerializer<T extends TemporalAccessor> extends JsonSerializer<T> implements WithFormat {

    private final Java8TimeCodec<T> java8TimeCodec;

    public JacksonJava8TimeSerializer(Class<T> clazz) {
        this(clazz, (DateTimeFormatter) null, null);
    }

    public JacksonJava8TimeSerializer(Class<T> clazz, String datePattern) {
        this(clazz, datePattern, null);
    }

    public JacksonJava8TimeSerializer(Class<T> clazz, DateTimeFormatter dateFormatter) {
        this(clazz, dateFormatter, null);
    }

    public JacksonJava8TimeSerializer(Class<T> clazz, Converter<?> converter) {
        this(clazz, (DateTimeFormatter) null, converter);
    }

    public JacksonJava8TimeSerializer(Class<T> clazz, String datePattern, Converter<?> converter) {
        this.java8TimeCodec = new Java8TimeCodec<>(clazz, datePattern, converter);
    }

    public JacksonJava8TimeSerializer(Class<T> clazz, DateTimeFormatter dateFormatter, Converter<?> converter) {
        this.java8TimeCodec = new Java8TimeCodec<>(clazz, dateFormatter, converter);
    }

    public JacksonJava8TimeSerializer(Java8TimeCodec<T> java8TimeCodec) {
        this.java8TimeCodec = java8TimeCodec;
    }

    public JacksonJava8TimeSerializer<T> withClass(Class<T> clazz) {
        return new JacksonJava8TimeSerializer<>(this.java8TimeCodec.withClass(clazz));
    }

    @Override
    public JacksonJava8TimeSerializer<T> withDatePattern(String datePattern) {
        return new JacksonJava8TimeSerializer<>(this.java8TimeCodec.withDatePattern(datePattern));
    }

    @Override
    public JacksonJava8TimeSerializer<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new JacksonJava8TimeSerializer<>(this.java8TimeCodec.withDateFormatter(dateFormatter));
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType type) throws JsonMappingException {
        visitor.expectStringFormat(type);
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, T value) {
        return value == null;
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        this.java8TimeCodec.write(value, gen::writeString);
    }
}
