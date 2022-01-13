package com.chm.converter.core.codecs.factory;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codecs.DefaultDateCodec;
import com.chm.converter.core.constant.TimeConstant;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;

/**
 * 默认实践类编解码
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-11-15
 **/
public class DefaultDateCodecFactory implements UniversalFactory<Codec> {

    private final Converter<?> converter;

    public DefaultDateCodecFactory(Converter<?> converter) {
        this.converter = converter;
    }

    @Override
    public Codec create(UniversalGenerate<Codec> generate, TypeToken<?> typeToken) {
        if (TimeConstant.DEFAULT_DATE_SET.contains(typeToken.getRawType())) {
            return new DefaultDateCodec(typeToken.getRawType(), (String) null, converter);
        }
        return null;
    }
}
