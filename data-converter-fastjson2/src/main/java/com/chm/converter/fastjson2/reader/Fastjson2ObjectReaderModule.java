package com.chm.converter.fastjson2.reader;

import com.alibaba.fastjson2.modules.ObjectReaderModule;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.reader.ObjectReaderProvider;
import com.chm.converter.core.Converter;
import com.chm.converter.core.UseRawJudge;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.codec.UniversalCodecAdapterCreator;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.fastjson2.Fastjson2CoreCodec;

import java.lang.reflect.Type;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-08-10
 **/
public class Fastjson2ObjectReaderModule implements ObjectReaderModule {

    private final ObjectReaderProvider readerProvider;

    private final UniversalGenerate<Codec> generate;

    private final UseRawJudge useRawJudge;

    public Fastjson2ObjectReaderModule(ObjectReaderProvider readerProvider, Converter<?> converter, UseRawJudge useRawJudge) {
        this(readerProvider, converter, null, useRawJudge);
    }

    public Fastjson2ObjectReaderModule(ObjectReaderProvider readerProvider, Converter<?> converter, UniversalGenerate<Codec> generate, UseRawJudge useRawJudge) {
        this.readerProvider = readerProvider;
        this.generate = generate != null ? generate : DataCodecGenerate.getDataCodecGenerate(converter);
        this.useRawJudge = useRawJudge;
    }

    @Override
    public ObjectReaderProvider getProvider() {
        return this.readerProvider;
    }

    @Override
    public ObjectReader getObjectReader(ObjectReaderProvider provider, Type type) {
        Class<?> clazz = ClassUtil.getClassByType(type);
        // 使用原始实现
        if (useRawJudge.useRawImpl(clazz)) {
            return null;
        }

        ObjectReader priorityUse = UniversalCodecAdapterCreator.createPriorityUse(this.generate, type,
                (t, codec) -> new Fastjson2CoreCodec<>(codec));
        if (priorityUse != null) {
            return priorityUse;
        }
        return null;
    }
}
