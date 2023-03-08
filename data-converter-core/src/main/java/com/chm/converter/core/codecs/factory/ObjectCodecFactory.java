package com.chm.converter.core.codecs.factory;

import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codecs.ObjectCodec;
import com.chm.converter.core.codecs.RuntimeTypeCodec;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;

import java.lang.reflect.TypeVariable;

/**
 * @author caihongming
 * @version v1.0
 * @date 2022-02-18
 **/
public class ObjectCodecFactory implements UniversalFactory<Codec> {

    @Override
    public Codec create(UniversalGenerate<Codec> generate, TypeToken<?> typeToken) {
        if (typeToken.getRawType() == Object.class &&
                typeToken.getType() instanceof TypeVariable) {
            return new RuntimeTypeCodec(generate, new ObjectCodec(generate), typeToken.getType());
        } else if (typeToken.getRawType() == Object.class) {
            return new ObjectCodec(generate);
        }
        return null;
    }
}
