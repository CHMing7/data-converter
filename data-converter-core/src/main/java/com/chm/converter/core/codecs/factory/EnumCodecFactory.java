package com.chm.converter.core.codecs.factory;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codecs.EnumCodec;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;

import java.util.Map;

/**
 * 枚举类型编解码
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-11-18
 **/
public class EnumCodecFactory implements UniversalFactory<Codec> {

    private final Converter<?> converter;

    public EnumCodecFactory(Converter<?> converter) {
        this.converter = converter;
    }

    @Override
    public Codec create(UniversalGenerate<Codec> generate, TypeToken<?> typeToken) {
        Class<?> rawType = typeToken.getRawType();
        if (!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class) {
            return null;
        }
        if (!rawType.isEnum()) {
            // handle anonymous subclasses
            rawType = rawType.getSuperclass();
        }
        Class<? extends Converter> converterClass = converter != null ? converter.getClass() : null;
        JavaBeanInfo javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(rawType, converterClass);
        Map<String, String> aliasMap = javaBeanInfo.getFieldNameAliasMap();
        return new EnumCodec(rawType, aliasMap);
    }

}
