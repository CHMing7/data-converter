package com.chm.converter.fastjson2.reader;

import com.alibaba.fastjson2.modules.ObjectReaderModule;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.reader.ObjectReaderProvider;
import com.chm.converter.core.Converter;
import com.chm.converter.core.UseOriginalJudge;
import com.chm.converter.fastjson2.Fastjson2EnumCodec;

import java.lang.reflect.Type;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-07-12
 **/
public class Fastjson2ObjectReaderModule implements ObjectReaderModule {

    private final ObjectReaderProvider readerProvider;

    private final Converter<?> converter;

    private final UseOriginalJudge useOriginalJudge;

    public Fastjson2ObjectReaderModule(ObjectReaderProvider readerProvider, Converter<?> converter, UseOriginalJudge useOriginalJudge) {
        this.readerProvider = readerProvider;
        this.converter = converter;
        this.useOriginalJudge = useOriginalJudge;
    }

    @Override
    public ObjectReaderProvider getProvider() {
        return this.readerProvider;
    }

    @Override
    public ObjectReader getObjectReader(ObjectReaderProvider provider, Type type) {
        if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            // 使用原始实现
            if (useOriginalJudge.useOriginalImpl(clazz)) {
                return null;
            }
            if (Enum.class.isAssignableFrom(clazz) && clazz != Enum.class) {
                if (!clazz.isEnum()) {
                    clazz = clazz.getSuperclass();
                }
                return new Fastjson2EnumCodec(clazz, converter);
            }
        }
        return null;
    }
}
