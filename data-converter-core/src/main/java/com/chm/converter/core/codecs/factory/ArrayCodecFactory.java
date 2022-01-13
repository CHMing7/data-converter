package com.chm.converter.core.codecs.factory;

import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codecs.ArrayCodec;
import com.chm.converter.core.codecs.RuntimeTypeCodec;
import com.chm.converter.core.reflect.ConverterTypes;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * 数组类编解码器工厂
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-11-30
 **/
public class ArrayCodecFactory implements UniversalFactory<Codec> {

    @Override
    public Codec create(UniversalGenerate<Codec> generate, TypeToken<?> typeToken) {
        Type type = typeToken.getType();
        if (!(type instanceof GenericArrayType || type instanceof Class && ((Class<?>) type).isArray())) {
            return null;
        }

        Type componentType = ConverterTypes.getArrayComponentType(type);
        Codec componentTypeCodec = new RuntimeTypeCodec(generate, generate.get(componentType), componentType);
        return new ArrayCodec(componentTypeCodec, typeToken);
    }
}
