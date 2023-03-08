package com.chm.converter.core.codecs.factory;

import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codecs.CollectionCodec;
import com.chm.converter.core.codecs.RuntimeTypeCodec;
import com.chm.converter.core.reflect.ConverterTypes;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * 集合类编解码器工厂
 *
 * @author caihongming
 * @version v1.0
 * @date 2021-11-30
 **/
public class CollectionCodecFactory implements UniversalFactory<Codec> {

    @Override
    public Codec create(UniversalGenerate<Codec> generate, TypeToken<?> typeToken) {
        Class<?> rawTypeOfSrc = typeToken.getRawType();
        if (!Collection.class.isAssignableFrom(rawTypeOfSrc)) {
            return null;
        }
        Type type = typeToken.getType();
        Type elementType = ConverterTypes.getCollectionElementType(type, rawTypeOfSrc);
        Codec elementCodec = new RuntimeTypeCodec(generate, generate.get(elementType), elementType);
        return new CollectionCodec(typeToken, elementCodec, elementType);
    }
}
