package com.chm.converter.json.gson;

import com.chm.converter.core.constant.TimeConstant;
import com.chm.converter.core.Converter;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.time.temporal.TemporalAccessor;
import java.util.Optional;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-09
 **/
public class GsonJava8TimeTypeAdapterFactory implements TypeAdapterFactory {

    private final Converter<?> converter;

    public GsonJava8TimeTypeAdapterFactory(Converter<?> converter) {
        this.converter = converter;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Class<T> cls = (Class<T>) typeToken.getRawType();
        Optional<Class<? extends TemporalAccessor>> first = TimeConstant.TEMPORAL_ACCESSOR_SET.stream()
                .filter(temporalAccessorClass -> temporalAccessorClass.isAssignableFrom(cls)).findFirst();
        return first.map(clazz -> (TypeAdapter<T>) new GsonJava8TimeAdapter<>(clazz, converter)).orElse(null);
    }

}
