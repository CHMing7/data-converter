package com.chm.converter.jackson.deserializer;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.DefaultDateCodec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
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

    public JacksonDefaultDateTypeDeserializer(DefaultDateCodec<T> defaultDateCodec) {
        this.defaultDateCodec = defaultDateCodec;
    }

    public JacksonDefaultDateTypeDeserializer<T> withClass(Class<T> clazz) {
        return new JacksonDefaultDateTypeDeserializer<>(this.defaultDateCodec.withDateType(clazz));
    }

    public JacksonDefaultDateTypeDeserializer<T> withDatePattern(String datePattern) {
        return new JacksonDefaultDateTypeDeserializer<>(this.defaultDateCodec.withDatePattern(datePattern));
    }

    public JacksonDefaultDateTypeDeserializer<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new JacksonDefaultDateTypeDeserializer<>(this.defaultDateCodec.withDateFormatter(dateFormatter));
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.currentTokenId() == JsonToken.VALUE_NULL.id()) {
            return null;
        }
        return this.defaultDateCodec.read(p::getText);
    }
}
