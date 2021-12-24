package com.chm.converter.json.jackson;

import com.chm.converter.core.Converter;
import com.chm.converter.jackson.AbstractModule;
import com.fasterxml.jackson.core.json.PackageVersion;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-18
 **/
public class JacksonModule extends AbstractModule {

    public JacksonModule(Converter<?> converter) {
        super("JacksonModule", PackageVersion.VERSION, converter);
    }

}
