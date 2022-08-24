package com.chm.converter.fastjson2.writer;

import com.alibaba.fastjson2.writer.ObjectWriterCreator;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;
import com.chm.converter.core.Converter;
import com.chm.converter.core.UseRawJudge;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-07-05
 **/
public class Fastjson2ObjectWriterProvider extends ObjectWriterProvider {

    private final Converter<?> converter;

    private final UseRawJudge useRawJudge;

    public Fastjson2ObjectWriterProvider(Converter<?> converter, UseRawJudge useRawJudge) {
        this.converter = converter;
        this.useRawJudge = useRawJudge;
        // register module
        this.register(new Fastjson2ObjectWriterModule(converter, useRawJudge));
    }

    @Override
    public ObjectWriterCreator getCreator() {
        ObjectWriterCreator creator = super.getCreator();
        return new Fastjson2ObjectWriterCreator(creator, converter, useRawJudge);
    }
}
