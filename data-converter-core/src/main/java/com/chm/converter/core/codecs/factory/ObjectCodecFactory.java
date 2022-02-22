package com.chm.converter.core.codecs.factory;

import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codecs.ObjectCodec;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-02-18
 **/
public class ObjectCodecFactory implements UniversalFactory<Codec> {

    @Override
    public Codec create(UniversalGenerate<Codec> generate, TypeToken<?> typeToken) {
        if (typeToken.getRawType() == Object.class) {
            return new ObjectCodec(generate);
        }
        return null;
    }
}
