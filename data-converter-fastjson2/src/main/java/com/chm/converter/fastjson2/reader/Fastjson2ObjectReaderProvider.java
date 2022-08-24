package com.chm.converter.fastjson2.reader;

import com.alibaba.fastjson2.reader.ObjectReaderCreator;
import com.alibaba.fastjson2.reader.ObjectReaderProvider;
import com.chm.converter.core.Converter;
import com.chm.converter.core.UseRawJudge;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-07-05
 **/
public class Fastjson2ObjectReaderProvider extends ObjectReaderProvider {

    private final Converter<?> converter;

    private final UseRawJudge useRawJudge;

    public Fastjson2ObjectReaderProvider(Converter<?> converter, UseRawJudge useRawJudge) {
        this.converter = converter;
        this.useRawJudge = useRawJudge;
        // register module
        this.register(new Fastjson2ObjectReaderModule(this, converter, useRawJudge));
    }

    @Override
    public ObjectReaderCreator getCreator() {
        ObjectReaderCreator creator = super.getCreator();
        return new Fastjson2ObjectReaderCreator(creator, converter, useRawJudge);
    }

}
