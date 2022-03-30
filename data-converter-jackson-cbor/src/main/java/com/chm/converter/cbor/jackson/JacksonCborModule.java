package com.chm.converter.cbor.jackson;

import com.chm.converter.core.Converter;
import com.chm.converter.jackson.AbstractModule;
import com.fasterxml.jackson.core.json.PackageVersion;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-18
 **/
public class JacksonCborModule extends AbstractModule {

    public JacksonCborModule(Converter<?> converter) {
        super("JacksonCborModule", PackageVersion.VERSION, converter);
    }

}
