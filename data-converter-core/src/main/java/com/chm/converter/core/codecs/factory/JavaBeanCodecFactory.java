package com.chm.converter.core.codecs.factory;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codecs.JavaBeanCodec;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;

/**
 * 通用java bean 编解码
 *
 * @author caihongming
 * @version v1.0
 * @since 2022-01-10
 **/
public class JavaBeanCodecFactory implements UniversalFactory<Codec> {

    private final Converter<?> converter;

    public JavaBeanCodecFactory(Converter<?> converter) {
        this.converter = converter;
    }

    @Override
    public Codec create(UniversalGenerate<Codec> generate, TypeToken<?> typeToken) {
        return new JavaBeanCodec<>(typeToken.getRawType(), generate, converter);
    }
}
