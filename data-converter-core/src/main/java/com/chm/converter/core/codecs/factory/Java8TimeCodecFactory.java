package com.chm.converter.core.codecs.factory;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codecs.Java8TimeCodec;
import com.chm.converter.core.constant.TimeConstant;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;

/**
 * java8时间类编解码
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-11-15
 **/
public class Java8TimeCodecFactory implements UniversalFactory<Codec> {

    private final Converter<?> converter;

    public Java8TimeCodecFactory(Converter<?> converter) {
        this.converter = converter;
    }

    @Override
    public Codec create(UniversalGenerate<Codec> generate, TypeToken<?> typeToken) {
        if (TimeConstant.isJava8Time(typeToken.getRawType())) {
            return new Java8TimeCodec(typeToken.getRawType(), (String) null, converter);
        }
        return null;
    }
}
