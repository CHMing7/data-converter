package com.chm.converter.jackson.serializer;

import com.chm.converter.core.codec.Codec;
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
 * @since 2022-07-19
 **/
public class JacksonCoreCodecSerializer<T> extends JsonSerializer<T> {

    private final Codec codec;

    public JacksonCoreCodecSerializer(Codec codec) {
        this.codec = codec;
    }

    public Codec getCodec() {
        return codec;
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType type) throws JsonMappingException {
        visitor.expectAnyFormat(type);
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, T value) {
        return value == null;
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        Object encode = codec.encode(value);
        if (encode != null && encode.getClass() != value.getClass()) {
            gen.writeObject(encode);
        }
    }
}
