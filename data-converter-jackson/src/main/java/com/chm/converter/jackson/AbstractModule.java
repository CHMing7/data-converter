package com.chm.converter.jackson;

import com.chm.converter.core.Converter;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.CollUtil;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.jackson.deserializer.JacksonDeserializers;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeBindings;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author caihongming
 * @version v1.0
 * @date 2021-12-23
 **/
public abstract class AbstractModule extends SimpleModule {

    public AbstractModule(String name, Version version, Converter<?> converter) {
        super(name, version);
        setDeserializers(new JacksonDeserializers(converter));
    }

    public static TypeToken<?> jacksonTypeToLangType(JavaType jacksonType) {
        TypeBindings bindings = jacksonType.getBindings();
        if (bindings == null) {
            return TypeToken.get(jacksonType.getRawClass());
        }
        List<JavaType> typeParameters = bindings.getTypeParameters();
        if (CollUtil.isEmpty(typeParameters)) {
            return TypeToken.get(jacksonType.getRawClass());
        }
        List<Type> typeList = ListUtil.list(true);
        for (JavaType typeParameter : typeParameters) {
            typeList.add(jacksonTypeToLangType(typeParameter).getType());
        }
        return TypeToken.getParameterized(jacksonType.getRawClass(), typeList.toArray(new Type[0]));
    }
}
