package com.chm.converter.ion.jackson;

import com.chm.converter.core.Converter;
import com.chm.converter.jackson.AbstractModule;
import com.fasterxml.jackson.core.json.PackageVersion;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-18
 **/
public class JacksonIonModule extends AbstractModule {

    public JacksonIonModule(Converter<?> converter) {
        super("JacksonIonModule", PackageVersion.VERSION, converter);
    }
}
