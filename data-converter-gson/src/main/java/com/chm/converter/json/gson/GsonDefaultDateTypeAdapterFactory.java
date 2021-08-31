package com.chm.converter.json.gson;

import com.chm.converter.constant.TimeConstant;
import com.chm.converter.json.JsonConverter;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.Optional;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-10
 **/
public class GsonDefaultDateTypeAdapterFactory implements TypeAdapterFactory {

    private final JsonConverter jsonConverter;

    public GsonDefaultDateTypeAdapterFactory(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Class<T> cls = (Class<T>) typeToken.getRawType();
        Optional<Class<? extends Date>> first = TimeConstant.DEFAULT_DATE_SET.stream()
                .filter(dateClass -> dateClass.isAssignableFrom(cls)).findFirst();
        return first.map(clazz -> (TypeAdapter<T>) new GsonDefaultDateTypeAdapter(clazz, jsonConverter)).orElse(null);
    }
}
