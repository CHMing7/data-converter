package com.chm.converter.core.codecs.factory;

import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codecs.MapCodec;
import com.chm.converter.core.codecs.RuntimeTypeCodec;
import com.chm.converter.core.reflect.ConverterTypes;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * map类型编解码
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-11-18
 **/
public class MapCodecFactory implements UniversalFactory<Codec> {

    @Override
    public Codec create(UniversalGenerate<Codec> generate, TypeToken<?> typeToken) {
        Class<?> rawTypeOfSrc = typeToken.getRawType();
        if (!Map.class.isAssignableFrom(rawTypeOfSrc)) {
            return null;
        }
        Type type = typeToken.getType();
        Type[] keyAndValueTypes = ConverterTypes.getMapKeyAndValueTypes(type, rawTypeOfSrc);
        Codec kCodec = new RuntimeTypeCodec(generate, generate.get(keyAndValueTypes[0]),
                keyAndValueTypes[0]);
        Codec vCodec = new RuntimeTypeCodec(generate, generate.get(keyAndValueTypes[1]),
                keyAndValueTypes[1]);
        return new MapCodec(typeToken, kCodec, vCodec);
    }
}
