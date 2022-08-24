package com.chm.converter.fastjson2.writer;

import com.alibaba.fastjson2.modules.ObjectWriterModule;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.alibaba.fastjson2.writer.ObjectWriterCreator;
import com.chm.converter.core.Converter;
import com.chm.converter.core.UseRawJudge;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.codec.UniversalCodecAdapterCreator;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.fastjson2.Fastjson2CoreCodec;

import java.util.List;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-07-21
 **/
public class Fastjson2ObjectWriterCreator extends ObjectWriterCreator {

    private final ObjectWriterCreator creator;

    private final UniversalGenerate<Codec> generate;

    private final UseRawJudge useRawJudge;

    public Fastjson2ObjectWriterCreator(ObjectWriterCreator creator, Converter<?> converter, UseRawJudge useRawJudge) {
        this(creator, converter, null, useRawJudge);
    }

    public Fastjson2ObjectWriterCreator(ObjectWriterCreator creator, Converter<?> converter, UniversalGenerate<Codec> generate, UseRawJudge useRawJudge) {
        this.creator = creator;
        this.generate = generate != null ? generate : DataCodecGenerate.getDataCodecGenerate(converter);
        this.useRawJudge = useRawJudge;
    }

    @Override
    public ObjectWriter createObjectWriter(Class objectClass, long features, final List<ObjectWriterModule> modules) {
        // 使用原始实现
        if (useRawJudge.useRawImpl(objectClass)) {
            return null;
        }
        ObjectWriter priorityUse = UniversalCodecAdapterCreator.createPriorityUse(this.generate, objectClass,
                (t, codec) -> new Fastjson2CoreCodec<>(codec));
        if (priorityUse != null) {
            return priorityUse;
        }
        return creator.createObjectWriter(objectClass, features, modules);
    }
}
