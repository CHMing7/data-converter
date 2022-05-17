package com.chm.converter.smile.jackson;

import com.chm.converter.core.Converter;
import com.chm.converter.jackson.AbstractModule;
import com.fasterxml.jackson.core.json.PackageVersion;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-05-17
 **/
public class JacksonSmileModule extends AbstractModule {

    public JacksonSmileModule(Converter<?> converter) {
        super("JacksonSmileModule", PackageVersion.VERSION, converter);
    }
}