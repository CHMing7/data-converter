package com.chm.converter.yaml.jackson;

import com.chm.converter.core.Converter;
import com.chm.converter.jackson.AbstractModule;
import com.fasterxml.jackson.core.json.PackageVersion;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-13
 **/
public class JacksonYamlModule extends AbstractModule {

    public JacksonYamlModule(Converter<?> converter) {
        super("JacksonYamlModule", PackageVersion.VERSION, converter);
    }

}
