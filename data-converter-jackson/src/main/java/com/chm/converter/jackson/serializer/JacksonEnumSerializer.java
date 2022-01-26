package com.chm.converter.jackson.serializer;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.EnumCodec;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

import java.io.IOException;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-25
 **/
public class JacksonEnumSerializer<E extends Enum<E>> extends JsonSerializer<E> {

    private final EnumCodec<E> enumCodec;

    public JacksonEnumSerializer(Class<E> classOfT, Converter<?> converter) {
        this.enumCodec = new EnumCodec<>(classOfT, converter);
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType type) throws JsonMappingException {
        visitor.expectStringFormat(type);
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, E value) {
        return value == null;
    }

    @Override
    public void serialize(E value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        this.enumCodec.write(value, gen::writeString);
    }
}
