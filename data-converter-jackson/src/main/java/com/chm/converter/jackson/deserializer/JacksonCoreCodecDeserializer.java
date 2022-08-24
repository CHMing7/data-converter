package com.chm.converter.jackson.deserializer;

import com.chm.converter.core.codec.Codec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BasicDeserializerFactory;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;

import java.io.IOException;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-07-20
 **/
public class JacksonCoreCodecDeserializer<T> extends JsonDeserializer<T> {

    private final Codec codec;

    public JacksonCoreCodecDeserializer(Codec codec) {
        this.codec = codec;
    }

    public Codec getCodec() {
        return codec;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (p.currentTokenId() == JsonToken.VALUE_NULL.id()) {
            return null;
        }
        DeserializerFactory factory = ctxt.getFactory();
        if (factory instanceof BasicDeserializerFactory) {
            JavaType javaType = ctxt.constructType(codec.getEncodeType().getRawType());
            BeanDescription beanDescription = ctxt.getConfig().introspect(javaType);
            JsonDeserializer<?> defaultDeserializer = ((BasicDeserializerFactory) factory).findDefaultDeserializer(ctxt, javaType, beanDescription);
            if (defaultDeserializer != null) {
                Object o = defaultDeserializer.deserialize(p, ctxt);
                return (T) codec.decode(o);
            }
        }
        Object o = p.readValueAs(codec.getEncodeType().getRawType());
        return (T) codec.decode(o);
    }
}
