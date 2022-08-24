package com.chm.converter.jackson.deserializer;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.codecs.JavaBeanCodec;
import com.chm.converter.core.codecs.ObjectCodec;
import com.chm.converter.core.universal.UniversalGenerate;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-07-20
 **/
public class JacksonDeserializers extends SimpleDeserializers {

    private final UniversalGenerate<Codec> generate;

    public JacksonDeserializers(Converter<?> converter) {
        this.generate = DataCodecGenerate.getDataCodecGenerate(converter);
    }

    public JacksonDeserializers(Converter<?> converter, UniversalGenerate<Codec> generate) {
        this.generate = generate != null ? generate : DataCodecGenerate.getDataCodecGenerate(converter);
    }

    @Override
    public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
        JsonDeserializer<?> beanDeserializer = super.findBeanDeserializer(type, config, beanDesc);
        if (beanDeserializer instanceof JacksonCoreCodecDeserializer) {
            return beanDeserializer;
        }

        Class<?> beanClass = beanDesc.getBeanClass();

        Codec codec = this.generate.get(beanClass);
        if (codec != null &&
                !(codec instanceof JavaBeanCodec) &&
                !(codec instanceof ObjectCodec) &&
                codec.isPriorityUse()) {
            return new JacksonCoreCodecDeserializer<>(codec);
        }

        return null;
    }
}
