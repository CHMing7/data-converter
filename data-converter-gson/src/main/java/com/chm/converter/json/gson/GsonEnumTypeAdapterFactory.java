package com.chm.converter.json.gson;

import com.chm.converter.core.Converter;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-26
 **/
public class GsonEnumTypeAdapterFactory implements TypeAdapterFactory {

    private final Converter<?> converter;

    public GsonEnumTypeAdapterFactory(Converter<?> converter) {
        this.converter = converter;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Class<? super T> rawType = typeToken.getRawType();
        if (!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class) {
            return null;
        }
        if (!rawType.isEnum()) {
            rawType = rawType.getSuperclass();
        }
        return new GsonEnumTypeAdapter(rawType, converter);
    }
}
