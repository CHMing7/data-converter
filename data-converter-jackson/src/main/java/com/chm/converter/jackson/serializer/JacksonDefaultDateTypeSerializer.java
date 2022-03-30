package com.chm.converter.jackson.serializer;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.DefaultDateCodec;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-14
 **/
public class JacksonDefaultDateTypeSerializer<T extends Date> extends JsonSerializer<T> {

    private final DefaultDateCodec<T> defaultDateCodec;

    public JacksonDefaultDateTypeSerializer(Class<T> dateType) {
        this(dateType, (DateTimeFormatter) null, null);
    }

    public JacksonDefaultDateTypeSerializer(Class<T> dateType, String datePattern) {
        this(dateType, datePattern, null);
    }

    public JacksonDefaultDateTypeSerializer(Class<T> dateType, DateTimeFormatter dateFormatter) {
        this(dateType, dateFormatter, null);
    }

    public JacksonDefaultDateTypeSerializer(Class<T> dateType, Converter<?> converter) {
        this(dateType, (DateTimeFormatter) null, converter);
    }

    public JacksonDefaultDateTypeSerializer(Class<T> dateType, String datePattern, Converter<?> converter) {
        this.defaultDateCodec = new DefaultDateCodec<>(dateType, datePattern, converter);
    }

    public JacksonDefaultDateTypeSerializer(Class<T> dateType, DateTimeFormatter dateFormatter, Converter<?> converter) {
        this.defaultDateCodec = new DefaultDateCodec<>(dateType, dateFormatter, converter);
    }

    public JacksonDefaultDateTypeSerializer(DefaultDateCodec<T> defaultDateCodec) {
        this.defaultDateCodec = defaultDateCodec;
    }

    public JacksonDefaultDateTypeSerializer<T> withClass(Class<T> clazz) {
        return new JacksonDefaultDateTypeSerializer<>(this.defaultDateCodec.withDateType(clazz));
    }

    public JacksonDefaultDateTypeSerializer<T> withDatePattern(String datePattern) {
        return new JacksonDefaultDateTypeSerializer<>(this.defaultDateCodec.withDatePattern(datePattern));
    }

    public JacksonDefaultDateTypeSerializer<T> withDateFormat(DateTimeFormatter dateFormatter) {
        return new JacksonDefaultDateTypeSerializer<>(this.defaultDateCodec.withDateFormat(dateFormatter));
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
        this.defaultDateCodec.write(value, gen::writeString);
    }

}
