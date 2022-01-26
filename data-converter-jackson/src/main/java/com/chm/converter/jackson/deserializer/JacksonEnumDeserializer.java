package com.chm.converter.jackson.deserializer;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.EnumCodec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-25
 **/
public class JacksonEnumDeserializer<E extends Enum<E>> extends JsonDeserializer<E> {

    private final EnumCodec<E> enumCodec;

    public JacksonEnumDeserializer(Class<E> classOfT, Converter<?> converter) {
        this.enumCodec = new EnumCodec<>(classOfT, converter);
    }

    @Override
    public E deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (p.currentTokenId() == JsonToken.VALUE_NULL.id()) {
            return null;
        }
        return enumCodec.read(p::getText);
    }
}
