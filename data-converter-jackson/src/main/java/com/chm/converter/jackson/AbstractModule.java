package com.chm.converter.jackson;

import com.chm.converter.core.Converter;
import com.chm.converter.jackson.deserializer.JacksonDeserializers;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-12-23
 **/
public abstract class AbstractModule extends SimpleModule {

    public AbstractModule(String name, Version version, Converter<?> converter) {
        super(name, version);
        setDeserializers(new JacksonDeserializers(converter));
    }
}
