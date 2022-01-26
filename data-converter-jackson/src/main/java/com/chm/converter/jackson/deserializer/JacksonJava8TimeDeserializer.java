package com.chm.converter.jackson.deserializer;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.Java8TimeCodec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-14
 **/
public class JacksonJava8TimeDeserializer<T extends TemporalAccessor> extends JsonDeserializer<T> {

    private final Java8TimeCodec<T> java8TimeCodec;

    public JacksonJava8TimeDeserializer(Class<T> clazz) {
        this(clazz, (DateTimeFormatter) null, null);
    }

    public JacksonJava8TimeDeserializer(Class<T> clazz, String datePattern) {
        this(clazz, datePattern, null);
    }

    public JacksonJava8TimeDeserializer(Class<T> clazz, DateTimeFormatter dateFormatter) {
        this(clazz, dateFormatter, null);
    }

    public JacksonJava8TimeDeserializer(Class<T> clazz, Converter<?> converter) {
        this(clazz, (DateTimeFormatter) null, converter);
    }

    public JacksonJava8TimeDeserializer(Class<T> clazz, String datePattern, Converter<?> converter) {
        this.java8TimeCodec = new Java8TimeCodec<>(clazz, datePattern, converter);
    }

    public JacksonJava8TimeDeserializer(Class<T> clazz, DateTimeFormatter dateFormatter, Converter<?> converter) {
        this.java8TimeCodec = new Java8TimeCodec<>(clazz, dateFormatter, converter);
    }

    public JacksonJava8TimeDeserializer<T> withClass(Class<T> clazz) {
        return new JacksonJava8TimeDeserializer<>(clazz, this.java8TimeCodec.getDateFormatter(), this.java8TimeCodec.getConverter());
    }

    public JacksonJava8TimeDeserializer<T> withDatePattern(String datePattern) {
        return new JacksonJava8TimeDeserializer<>(this.java8TimeCodec.getClazz(), datePattern, this.java8TimeCodec.getConverter());
    }

    public JacksonJava8TimeDeserializer<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new JacksonJava8TimeDeserializer<>(this.java8TimeCodec.getClazz(), dateFormatter, this.java8TimeCodec.getConverter());
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (p.currentTokenId() == JsonToken.VALUE_NULL.id()) {
            return null;
        }
        return this.java8TimeCodec.read(p::getText);
    }
}
